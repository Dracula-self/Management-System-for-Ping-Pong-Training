package com.quan.project.controller;

import com.github.pagehelper.PageInfo;
import com.quan.project.common.R;
import com.quan.project.dto.PageDTO;
import com.quan.project.entity.Appointment;
import com.quan.project.service.AppointmentService;
import com.quan.project.vo.AppointmentVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import com.quan.project.common.CurrentUser;
/**
 * 课程预约管理控制器
 * 基础路径: /api/appointments
 */

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
    
    private static final Logger log = LoggerFactory.getLogger(AppointmentController.class);
    
    @Autowired
    private AppointmentService appointmentService;
    
    /**
     * 4.1 学员预约课程 - POST /api/appointments
     * 学员向教练预约课程
     */
    @PostMapping
    public R<Appointment> create(@RequestBody Appointment appointment) {
        log.debug("学员预约课程，参数: {}", appointment);
        Appointment createdAppointment = appointmentService.createAppointment(appointment);
        return R.success(createdAppointment);
    }
    
    /**
     * 4.2 教练处理预约 - PUT /api/appointments/{id}/approve
     * 教练确认或拒绝预约
     */
    @PutMapping("/{id}/approve")
    public R<Void> approve(@PathVariable Integer id, @RequestParam Boolean approved) {
        log.debug("教练处理预约，预约ID: {}, 是否通过: {}", id, approved);
        appointmentService.approveAppointment(id, approved);
        return R.success();
    }
    
    /**
     * 4.3 取消预约申请 - PUT /api/appointments/{id}/cancel
     * 学员或教练申请取消预约
     */
    @PutMapping("/{id}/cancel")
    public R<Void> requestCancel(@PathVariable Integer id) {
        log.debug("申请取消预约，预约ID: {}", id);
        appointmentService.requestCancelAppointment(id);
        return R.success();
    }
    
    /**
     * 4.4 确认取消预约 - PUT /api/appointments/{id}/confirm-cancel
     * 对方确认取消预约
     */
    @PutMapping("/{id}/confirm-cancel")
    public R<Void> confirmCancel(@PathVariable Integer id) {
        log.debug("确认取消预约，预约ID: {}", id);
        appointmentService.confirmCancelAppointment(id);
        return R.success();
    }
    
    /**
     * 4.5 查询预约列表 - POST /api/appointments/search
     * 查询预约列表（包含教练姓名、学员姓名、球台编号）
     */
    @PostMapping("/search")
    public R<PageInfo<AppointmentVO>> search(@RequestBody PageDTO pageRequest) {
        log.debug("分页查询预约，参数: {}", pageRequest);
        PageInfo<AppointmentVO> pageInfo = appointmentService.getAppointmentPageWithInfo(pageRequest);
        return R.success(pageInfo);
    }
    
    /**
     * 4.6 获取教练课表 - GET /api/appointments/coach/{coachId}/schedule
     * 获取教练未来一周课表
     */
    @GetMapping("/coach/{coachId}/schedule")
    public R<List<Appointment>> getCoachSchedule(@PathVariable Integer coachId) {
        log.debug("查询教练课表，教练ID: {}", coachId);
        List<Appointment> schedule = appointmentService.getCoachSchedule(coachId);
        return R.success(schedule);
    }
    /**
     * 获取教练课表
     */
    @GetMapping("/coach/my-schedule")
    public R<Map<String, Object>> getMySchedule() {
        log.debug("获取当前教练的课表");
        // 获取当前登录的教练ID
        Integer currentUserId = CurrentUser.getCurrentUserId();
        Integer userRole = CurrentUser.getCurrentUserRole();
        // 验证是否是教练
        if (userRole != 3) {
            return R.error("只有教练可以查看课表");
        }
        Map<String, Object> result = appointmentService.getCoachScheduleGrid(currentUserId);
        return R.success(result);
    }
    /**
     * 获取预约详情 - GET /api/appointments/{id}
     * 根据ID获取预约详情
     */
    @GetMapping("/{id}")
    public R<Appointment> getById(@PathVariable Integer id) {
        log.debug("查询预约详情，ID: {}", id);
        Appointment appointment = appointmentService.getAppointmentById(id);
        return R.success(appointment);
    }
    
    /**
     * 查询待评价预约 - GET /api/appointments/for-evaluation
     * 查询需要评价的已完成预约
     */
    @GetMapping("/for-evaluation")
    public R<List<Appointment>> getForEvaluation() {
        log.debug("查询待评价预约");
        List<Appointment> appointments = appointmentService.getAppointmentsForEvaluation();
        return R.success(appointments);
    }
    
    /**
     * 检查本月取消次数 - GET /api/appointments/can-cancel
     * 检查当前用户本月是否还能取消预约
     */
    @GetMapping("/can-cancel")
    public R<Boolean> canCancel() {
        log.debug("检查本月取消次数");
        boolean canCancel = appointmentService.canCancelThisMonth();
        return R.success(canCancel);
    }
    
    /**
     * 更新预约信息 - PUT /api/appointments/{id}
     * 更新预约的详细信息（如状态等）
     */
    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Integer id, @RequestBody Appointment appointment) {
        log.debug("更新预约信息，预约ID: {}, 参数: {}", id, appointment);
        appointment.setId(id);
        appointmentService.updateAppointment(appointment);
        return R.success();
    }
    
    /**
     * 预览课程费用
     * GET /api/appointments/preview-cost?coachId=1&startTime=2024-01-01T10:00:00&endTime=2024-01-01T12:00:00
     */
    @GetMapping("/preview-cost")
    public R<BigDecimal> previewCost(@RequestParam Integer coachId,
                                   @RequestParam String startTime,
                                   @RequestParam String endTime) {
        try {
            log.debug("预览课程费用，教练ID: {}, 开始时间: {}, 结束时间: {}", coachId, startTime, endTime);
            
            LocalDateTime start = LocalDateTime.parse(startTime);
            LocalDateTime end = LocalDateTime.parse(endTime);
            
            BigDecimal cost = appointmentService.previewCost(coachId, start, end);
            return R.success(cost);
        } catch (Exception e) {
            log.error("预览费用失败", e);
            return R.error("预览费用失败: " + e.getMessage());
        }
    }
}
