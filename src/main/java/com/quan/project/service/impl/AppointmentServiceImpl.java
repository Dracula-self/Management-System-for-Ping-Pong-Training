package com.quan.project.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.quan.project.common.CurrentUser;
import com.quan.project.dto.PageDTO;
import com.quan.project.entity.Appointment;
import com.quan.project.entity.CoachStudentRelation;
import com.quan.project.entity.SystemMessage;
import com.quan.project.entity.Transaction;
import com.quan.project.entity.User;
import com.quan.project.exception.BusinessException;
import com.quan.project.mapper.*;
import com.quan.project.service.AppointmentService;
import com.quan.project.service.SystemMessageService;
import com.quan.project.service.TableService;
import com.quan.project.vo.AppointmentVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDate;
import com.quan.project.entity.Table;
import java.util.ArrayList;
/**
 * 课程预约服务实现类
 */
@Service
public class AppointmentServiceImpl implements AppointmentService {
    
    private static final Logger log = LoggerFactory.getLogger(AppointmentServiceImpl.class);
    @Autowired
    private TableMapper tableMapper;        // ← 添加这行


    @Autowired
    private AppointmentMapper appointmentMapper;
    
    @Autowired
    private CoachStudentRelationMapper relationMapper;
    
    @Autowired
    private TransactionMapper transactionMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private TableService tableService;
    
    @Autowired
    private SystemMessageService messageService;
    
    @Override
    @Transactional
    public Appointment createAppointment(Appointment appointment) {
        try {
            Integer studentId = CurrentUser.getCurrentUserId();
            appointment.setStudentId(studentId);
            appointment.setStatus(Appointment.STATUS_PENDING);
            
            // 验证师生关系（符合业务需求：在双选关系确立后，学生可以预约课程）
            CoachStudentRelation relation = relationMapper.selectByCoachAndStudent(
                appointment.getCoachId(), studentId);
            if (relation == null || relation.getStatus() != CoachStudentRelation.STATUS_CONFIRMED) {
                throw new BusinessException("您还未与该教练建立师生关系，请先申请建立师生关系");
            }
            
            // 检查时间冲突
            if (checkTimeConflict(appointment.getCoachId(), appointment.getStartTime(), appointment.getEndTime())) {
                throw new BusinessException("该时间段教练已有安排");
            }
            
            // 计算费用（使用分钟级别精确计算）
            User coach = userMapper.selectById(appointment.getCoachId());
            BigDecimal hourlyRate = getHourlyRate(coach.getCoachLevel());
            long minutes = ChronoUnit.MINUTES.between(appointment.getStartTime(), appointment.getEndTime());
            BigDecimal hours = BigDecimal.valueOf(minutes).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
            appointment.setCost(hourlyRate.multiply(hours).setScale(2, RoundingMode.HALF_UP));
            
            // 检查学员余额
            User student = userMapper.selectById(studentId);
            if (student.getBalance().compareTo(appointment.getCost()) < 0) {
                throw new BusinessException("账户余额不足，请先充值");
            }
            
            int result = appointmentMapper.insert(appointment);
            if (result <= 0) {
                throw new BusinessException("创建预约失败");
            }
            
            // 创建预约申请系统消息
            try {
                User studentUser = userMapper.selectById(studentId);
                String appointmentTime = appointment.getStartTime().toString() + " - " + appointment.getEndTime().toString();
                SystemMessage message = new SystemMessage();
                message.setTitle("新的预约申请");
                message.setContent(String.format("学员【%s】申请预约课程，时间：%s，请及时处理。", 
                    studentUser.getRealName(), appointmentTime));
                message.setType(SystemMessage.TYPE_SYSTEM_NOTICE);
                message.setStatus(SystemMessage.STATUS_PUBLISHED);
                messageService.create(message);
            } catch (Exception e) {
                log.warn("创建预约申请消息失败", e);
            }
            
            log.info("创建预约成功，预约ID: {}, 学员ID: {}, 教练ID: {}", 
                appointment.getId(), studentId, appointment.getCoachId());
            return appointment;
            
        } catch (Exception e) {
            log.error("创建预约失败", e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("创建预约失败");
        }
    }
    
    @Override
    @Transactional
    public void updateAppointment(Appointment appointment) {
        try {
            Integer currentUserId = CurrentUser.getCurrentUserId();
            Integer currentUserRole = CurrentUser.getCurrentUserRole();
            
            // 获取原预约信息
            Appointment existingAppointment = appointmentMapper.selectById(appointment.getId());
            if (existingAppointment == null) {
                throw new BusinessException("预约不存在");
            }
            
            // 权限验证：只有相关用户或管理员可以更新预约
            if (currentUserRole == 3) { // 教练
                if (!existingAppointment.getCoachId().equals(currentUserId)) {
                    throw new BusinessException("您只能更新自己的预约");
                }
            } else if (currentUserRole == 4) { // 学员
                if (!existingAppointment.getStudentId().equals(currentUserId)) {
                    throw new BusinessException("您只能更新自己的预约");
                }
            } else if (currentUserRole == 2) { // 校区管理员
                // 校区管理员可以更新本校区的预约，这里需要验证校区权限
                // 暂时允许校区管理员更新所有预约
            } else if (currentUserRole != 1) { // 非超级管理员
                throw new BusinessException("您没有权限更新此预约");
            }
            
            // 如果是状态更新为已完成，只需要记录课程完成（费用已在教练确认时扣除）
            if (appointment.getStatus() != null && appointment.getStatus() == Appointment.STATUS_COMPLETED 
                && existingAppointment.getStatus() != Appointment.STATUS_COMPLETED) {
                
                // 验证只能从"已预约"状态完成课程
                if (existingAppointment.getStatus() != Appointment.STATUS_CONFIRMED) {
                    throw new BusinessException("只能完成已确认的预约");
                }
                
                log.info("课程标记为完成: 预约ID={}, 学员ID={}, 教练ID={}", 
                    existingAppointment.getId(), existingAppointment.getStudentId(), existingAppointment.getCoachId());
            }
            
            // 更新预约信息
            int result = appointmentMapper.update(appointment);
            if (result <= 0) {
                throw new BusinessException("更新预约失败");
            }
            
            log.info("更新预约成功，预约ID: {}, 操作者: {}", appointment.getId(), currentUserId);
            
        } catch (Exception e) {
            log.error("更新预约失败，预约ID: {}", appointment.getId(), e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("更新预约失败");
        }
    }
    
    @Override
    @Transactional
    public void approveAppointment(Integer id, Boolean approved) {
        try {
            Integer currentUserId = CurrentUser.getCurrentUserId();
            Integer currentUserRole = CurrentUser.getCurrentUserRole();
            
            Appointment appointment = appointmentMapper.selectById(id);
            
            if (appointment == null) {
                throw new BusinessException("预约不存在");
            }
            
            if (appointment.getStatus() != Appointment.STATUS_PENDING) {
                throw new BusinessException("预约状态不正确");
            }
            
            // 权限验证：教练只能处理自己的预约，校区管理员可以处理本校区的预约
            if (currentUserRole == 3) { // 教练角色
                if (!appointment.getCoachId().equals(currentUserId)) {
                    throw new BusinessException("您只能处理自己的预约");
                }
            } else if (currentUserRole == 2) { // 校区管理员
                // 校区管理员可以处理本校区内的预约
                User currentUser = userMapper.selectById(currentUserId);
                User appointmentCoach = userMapper.selectById(appointment.getCoachId());
                
                if (currentUser == null || currentUser.getCampusId() == null) {
                    throw new BusinessException("校区管理员信息异常");
                }
                
                if (appointmentCoach == null || !currentUser.getCampusId().equals(appointmentCoach.getCampusId())) {
                    throw new BusinessException("您只能处理本校区内教练的预约");
                }
            } else if (currentUserRole != 1) { // 非超级管理员
                throw new BusinessException("您没有权限处理此预约");
            }
            // 超级管理员(1)可以处理所有预约
            
            if (approved) {
                // 确认预约，扣除费用
                appointmentMapper.updateStatus(id, Appointment.STATUS_CONFIRMED);
                
                // 创建消费流水记录
                Transaction transaction = new Transaction();
                transaction.setStudentId(appointment.getStudentId());
                transaction.setType(Transaction.TYPE_COURSE_PAYMENT);
                transaction.setAmount(appointment.getCost().negate()); // 负数表示消费
                transaction.setNotes("课程费用支付 - 预约ID: " + id);
                transaction.setTransactionTime(LocalDateTime.now());
                transactionMapper.insert(transaction);
                
                // 更新学员余额
                User student = userMapper.selectById(appointment.getStudentId());
                student.setBalance(student.getBalance().subtract(appointment.getCost()));
                userMapper.update(student);
                
                // 创建预约确认系统消息
                try {
                    User coach = userMapper.selectById(appointment.getCoachId());
                    String appointmentTime = appointment.getStartTime().toString() + " - " + appointment.getEndTime().toString();
                    SystemMessage message = new SystemMessage();
                    message.setTitle("预约确认通知");
                    message.setContent(String.format("教练【%s】已确认学员【%s】的预约，时间：%s，请准时上课。", 
                        coach.getRealName(), student.getRealName(), appointmentTime));
                    message.setType(SystemMessage.TYPE_SYSTEM_NOTICE);
                    message.setStatus(SystemMessage.STATUS_PUBLISHED);
                    messageService.create(message);
                } catch (Exception e) {
                    log.warn("创建预约确认消息失败", e);
                }
                
                log.info("确认预约成功，预约ID: {}, 教练ID: {}, 操作者: {}", id, appointment.getCoachId(), currentUserId);
            } else {
                // 拒绝预约
                appointmentMapper.updateStatus(id, Appointment.STATUS_CANCELLED);
                
                // 创建预约拒绝系统消息
                try {
                    User coach = userMapper.selectById(appointment.getCoachId());
                    User studentUser = userMapper.selectById(appointment.getStudentId());
                    String appointmentTime = appointment.getStartTime().toString() + " - " + appointment.getEndTime().toString();
                    SystemMessage message = new SystemMessage();
                    message.setTitle("预约拒绝通知");
                    message.setContent(String.format("很抱歉，教练【%s】无法确认学员【%s】的预约（时间：%s）。", 
                        coach.getRealName(), studentUser.getRealName(), appointmentTime));
                    message.setType(SystemMessage.TYPE_SYSTEM_NOTICE);
                    message.setStatus(SystemMessage.STATUS_PUBLISHED);
                    messageService.create(message);
                } catch (Exception e) {
                    log.warn("创建预约拒绝消息失败", e);
                }
                
                log.info("拒绝预约，预约ID: {}, 教练ID: {}, 操作者: {}", id, appointment.getCoachId(), currentUserId);
            }
            
        } catch (Exception e) {
            log.error("处理预约失败，预约ID: {}", id, e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("处理预约失败");
        }
    }
    
    @Override
    public void requestCancelAppointment(Integer id) {
        try {
            Integer userId = CurrentUser.getCurrentUserId();
            Appointment appointment = appointmentMapper.selectById(id);
            
            if (appointment == null) {
                throw new BusinessException("预约不存在");
            }
            
            // 检查权限
            if (!appointment.getStudentId().equals(userId) && !appointment.getCoachId().equals(userId)) {
                throw new BusinessException("只能取消自己的预约");
            }
            
            if (appointment.getStatus() != Appointment.STATUS_CONFIRMED) {
                throw new BusinessException("只能取消已确认的预约");
            }
            
            // 检查时间限制（24小时前）
            if (appointment.getStartTime().minusHours(24).isBefore(LocalDateTime.now())) {
                throw new BusinessException("课程开始前24小时内不能取消预约");
            }
            
            // 检查本月取消次数
            if (!canCancelThisMonth()) {
                throw new BusinessException("本月取消次数已达上限（3次）");
            }
            
            // TODO: 发送取消确认消息给对方
            
            log.info("申请取消预约，预约ID: {}, 申请人: {}", id, userId);
            
        } catch (Exception e) {
            log.error("申请取消预约失败，预约ID: {}", id, e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("申请取消预约失败");
        }
    }
    
    @Override
    @Transactional
    public void confirmCancelAppointment(Integer id) {
        try {
            Appointment appointment = appointmentMapper.selectById(id);
            
            if (appointment == null) {
                throw new BusinessException("预约不存在");
            }
            
            // 取消预约
            appointmentMapper.updateStatus(id, Appointment.STATUS_CANCELLED);
            
            // 退款
            Transaction refundTransaction = new Transaction();
            refundTransaction.setStudentId(appointment.getStudentId());
            refundTransaction.setType(Transaction.TYPE_COURSE_REFUND);
            refundTransaction.setAmount(appointment.getCost()); // 正数表示退款
            refundTransaction.setNotes("课程取消退款 - 预约ID: " + id);
            transactionMapper.insert(refundTransaction);
            
            // 更新学员余额
            User student = userMapper.selectById(appointment.getStudentId());
            student.setBalance(student.getBalance().add(appointment.getCost()));
            userMapper.update(student);
            
            log.info("确认取消预约成功，预约ID: {}", id);
            
        } catch (Exception e) {
            log.error("确认取消预约失败，预约ID: {}", id, e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("确认取消预约失败");
        }
    }
    
    @Override
    public Appointment getAppointmentById(Integer id) {
        try {
            Appointment appointment = appointmentMapper.selectById(id);
            if (appointment == null) {
                throw new BusinessException("预约不存在");
            }
            
            // 根据用户角色进行权限验证
            Integer currentUserId = CurrentUser.getCurrentUserId();
            Integer userRole = CurrentUser.getCurrentUserRole();
            
            if (userRole == 3) { // 教练角色
                if (!appointment.getCoachId().equals(currentUserId)) {
                    throw new BusinessException("您只能查看自己的预约");
                }
            } else if (userRole == 4) { // 学员角色
                if (!appointment.getStudentId().equals(currentUserId)) {
                    throw new BusinessException("您只能查看自己的预约");
                }
            }
            // 超级管理员(1)和校区管理员(2)可以查看所有预约
            
            return appointment;
        } catch (Exception e) {
            log.error("查询预约失败，预约ID: {}", id, e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("查询预约失败");
        }
    }
    
    @Override
    public PageInfo<Appointment> getAppointmentPage(PageDTO pageRequest) {
        try {
            // 获取当前用户信息
            Integer currentUserId = CurrentUser.getCurrentUserId();
            Integer userRole = CurrentUser.getCurrentUserRole();
            
            // 根据用户角色进行权限过滤
            if (userRole == 3) { // 教练角色
                // 教练只能查看自己的预约，强制设置coachId
                if (pageRequest.getParams() == null) {
                    pageRequest.setParams(new java.util.HashMap<>());
                }
                pageRequest.getParams().put("coachId", currentUserId);
                log.debug("教练角色查询预约，强制过滤coachId: {}", currentUserId);
            } else if (userRole == 4) { // 学员角色
                // 学员只能查看自己的预约，强制设置studentId
                if (pageRequest.getParams() == null) {
                    pageRequest.setParams(new java.util.HashMap<>());
                }
                pageRequest.getParams().put("studentId", currentUserId);
                log.debug("学员角色查询预约，强制过滤studentId: {}", currentUserId);
            }
            // 超级管理员(1)和校区管理员(2)可以查看所有预约，不做额外过滤
            
            PageHelper.startPage(pageRequest.getPageNum(), pageRequest.getPageSize());
            List<Appointment> appointments = appointmentMapper.selectPageByConditions(pageRequest);
            return new PageInfo<>(appointments);
        } catch (Exception e) {
            log.error("分页查询预约失败", e);
            throw new BusinessException("分页查询预约失败");
        }
    }
    
    @Override
    public PageInfo<AppointmentVO> getAppointmentPageWithInfo(PageDTO pageRequest) {
        try {
            // 获取当前用户信息
            Integer currentUserId = CurrentUser.getCurrentUserId();
            Integer userRole = CurrentUser.getCurrentUserRole();
            
            // 根据用户角色进行权限过滤
            if (userRole == 3) { // 教练角色
                // 教练只能查看自己的预约，强制设置coachId
                if (pageRequest.getParams() == null) {
                    pageRequest.setParams(new java.util.HashMap<>());
                }
                pageRequest.getParams().put("coachId", currentUserId);
                log.debug("教练角色查询预约，强制过滤coachId: {}", currentUserId);
            } else if (userRole == 4) { // 学员角色
                // 学员只能查看自己的预约，强制设置studentId
                if (pageRequest.getParams() == null) {
                    pageRequest.setParams(new java.util.HashMap<>());
                }
                pageRequest.getParams().put("studentId", currentUserId);
                log.debug("学员角色查询预约，强制过滤studentId: {}", currentUserId);
            } else if (userRole == 2) { // 校区管理员
                // 校区管理员只能查看本校区的预约
                User currentUser = userMapper.selectById(currentUserId);
                if (currentUser != null && currentUser.getCampusId() != null) {
                    if (pageRequest.getParams() == null) {
                        pageRequest.setParams(new java.util.HashMap<>());
                    }
                    pageRequest.getParams().put("campusId", currentUser.getCampusId());
                    log.debug("校区管理员角色查询预约，强制过滤campusId: {}", currentUser.getCampusId());
                }
            }
            // 超级管理员(1)可以查看所有预约，不做额外过滤
            
            PageHelper.startPage(pageRequest.getPageNum(), pageRequest.getPageSize());
            List<AppointmentVO> appointments = appointmentMapper.selectPageWithInfo(pageRequest);
            return new PageInfo<>(appointments);
        } catch (Exception e) {
            log.error("分页查询预约（含详细信息）失败", e);
            throw new BusinessException("分页查询预约失败");
        }
    }
    
    @Override
    public List<Appointment> getCoachSchedule(Integer coachId) {
        try {
            // 权限验证：教练只能查看自己的课表
            Integer currentUserId = CurrentUser.getCurrentUserId();
            Integer userRole = CurrentUser.getCurrentUserRole();
            
            if (userRole == 3 && !coachId.equals(currentUserId)) {
                throw new BusinessException("您只能查看自己的课表");
            }
            // 超级管理员(1)、校区管理员(2)和学员(4)可以查看指定教练的课表
            
            LocalDateTime startTime = LocalDateTime.now();
            LocalDateTime endTime = startTime.plusWeeks(1);
            return appointmentMapper.selectCoachSchedule(coachId, startTime, endTime);
        } catch (Exception e) {
            log.error("查询教练课表失败，教练ID: {}", coachId, e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("查询教练课表失败");
        }
    }
    
    @Override
    public List<Integer> getAvailableTables(Integer campusId, LocalDateTime startTime, LocalDateTime endTime) {
        try {
            // 使用TableService查询可用球台
            List<com.quan.project.entity.Table> availableTables = tableService.getAvailableTables(campusId, startTime, endTime);
            
            // 转换为ID列表
            return availableTables.stream()
                    .map(com.quan.project.entity.Table::getId)
                    .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            log.error("查询可用球台失败，校区ID: {}", campusId, e);
            throw new BusinessException("查询可用球台失败");
        }
    }
    
    @Override
    public boolean checkTimeConflict(Integer coachId, LocalDateTime startTime, LocalDateTime endTime) {
        try {
            List<Appointment> conflicts = appointmentMapper.selectByCoachAndTimeRange(coachId, startTime, endTime);
            return !conflicts.isEmpty();
        } catch (Exception e) {
            log.error("检查时间冲突失败", e);
            return true; // 出错时保守处理，认为有冲突
        }
    }
    
    @Override
    public void completeCourse(Integer appointmentId) {
        try {
            appointmentMapper.updateStatus(appointmentId, Appointment.STATUS_COMPLETED);
            log.info("课程完成，预约ID: {}", appointmentId);
        } catch (Exception e) {
            log.error("完成课程失败，预约ID: {}", appointmentId, e);
        }
    }
    
    @Override
    public List<Appointment> getAppointmentsForEvaluation() {
        try {
            Integer userId = CurrentUser.getCurrentUserId();
            Integer userRole = CurrentUser.getCurrentUserRole();
            return appointmentMapper.selectCompletedForEvaluation(userId, userRole);
        } catch (Exception e) {
            log.error("查询待评价预约失败", e);
            throw new BusinessException("查询待评价预约失败");
        }
    }
    
    @Override
    public boolean canCancelThisMonth() {
        try {
            Integer userId = CurrentUser.getCurrentUserId();
            Integer userRole = CurrentUser.getCurrentUserRole();
            int cancelCount = appointmentMapper.countCancelledByUserThisMonth(userId, userRole);
            return cancelCount < 3;
        } catch (Exception e) {
            log.error("检查取消次数失败", e);
            return false; // 出错时保守处理
        }
    }
    
    /**
     * 根据教练级别获取小时费率
     */
    private BigDecimal getHourlyRate(Integer coachLevel) {
        switch (coachLevel) {
            case 1: // 高级教练
                return BigDecimal.valueOf(200);
            case 2: // 中级教练
                return BigDecimal.valueOf(150);
            case 3: // 初级教练
                return BigDecimal.valueOf(80);
            default:
                return BigDecimal.valueOf(100);
        }
    }
    
    @Override
    public BigDecimal previewCost(Integer coachId, LocalDateTime startTime, LocalDateTime endTime) {
        if (coachId == null || startTime == null || endTime == null) {
            throw new BusinessException("参数不能为空");
        }
        
        if (endTime.isBefore(startTime) || endTime.equals(startTime)) {
            throw new BusinessException("结束时间必须晚于开始时间");
        }
        
        try {
            // 获取教练信息
            User coach = userMapper.selectById(coachId);
            if (coach == null) {
                throw new BusinessException("教练不存在");
            }
            
            // 计算费用
            BigDecimal hourlyRate = getHourlyRate(coach.getCoachLevel());
            
            // 计算时长（分钟）
            long minutes = ChronoUnit.MINUTES.between(startTime, endTime);
            BigDecimal hours = BigDecimal.valueOf(minutes).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
            
            return hourlyRate.multiply(hours).setScale(2, RoundingMode.HALF_UP);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("预览课程费用失败，教练ID: {}", coachId, e);
            throw new BusinessException("预览费用失败");
        }
    }
    @Override
    public Map<String, Object> getCoachScheduleGrid(Integer coachId) {
        try {
            // 获取教练信息
            User coach = userMapper.selectById(coachId);
            if (coach == null || coach.getUserRole() != 3) {
                throw new BusinessException("教练不存在");
            }

            // 计算本周时间范围（从今天开始的7天）
            LocalDateTime startTime = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
            LocalDateTime endTime = startTime.plusDays(7);

            // 查询教练的预约记录
            List<Appointment> appointments = appointmentMapper.selectCoachSchedule(coachId, startTime, endTime);

            // 构建返回数据
            Map<String, Object> result = new HashMap<>();

            // 教练信息
            result.put("coachName", coach.getRealName());

            // 生成一周的日期
            List<Map<String, String>> weekDays = new ArrayList<>();
            LocalDate startDate = startTime.toLocalDate();
            for (int i = 0; i < 7; i++) {
                LocalDate date = startDate.plusDays(i);
                Map<String, String> dayInfo = new HashMap<>();
                dayInfo.put("date", date.toString());
                dayInfo.put("dayName", getDayName(date.getDayOfWeek().getValue()));
                weekDays.add(dayInfo);
            }
            result.put("weekDays", weekDays);

            // 构建课表数据
            Map<String, Map<String, Object>> scheduleData = new HashMap<>();
            for (Appointment apt : appointments) {
                // 只显示待确认和已确认的预约
                if (apt.getStatus() == 0 || apt.getStatus() == 1) {
                    String date = apt.getStartTime().toLocalDate().toString();
                    int hour = apt.getStartTime().getHour();
                    String key = date + "-" + hour;

                    Map<String, Object> cellData = new HashMap<>();
                    cellData.put("appointmentId", apt.getId());
                    cellData.put("studentName", getStudentName(apt.getStudentId()));
                    cellData.put("tableNumber", getTableNumber(apt.getTableId()));
                    cellData.put("status", apt.getStatus() == 0 ? "待确认" : "已确认");
                    cellData.put("timeRange", String.format("%02d:00-%02d:00", hour, hour + 1));

                    scheduleData.put(key, cellData);
                }
            }
            result.put("scheduleData", scheduleData);

            return result;

        } catch (Exception e) {
            log.error("获取教练课表失败，教练ID: {}", coachId, e);
            throw new BusinessException("获取课表失败");
        }
    }

    // 辅助方法
    private String getStudentName(Integer studentId) {
        User student = userMapper.selectById(studentId);
        return student != null ? student.getRealName() : "未知学员";
    }

    private String getTableNumber(Integer tableId) {
        Table table = tableMapper.selectById(tableId);
        return table != null ? table.getTableNumber() : "未知";
    }

    private String getDayName(int dayOfWeek) {
        String[] dayNames = {"", "周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        return dayNames[dayOfWeek];
    }
}
