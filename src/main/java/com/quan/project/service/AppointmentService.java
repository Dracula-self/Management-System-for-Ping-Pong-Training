package com.quan.project.service;

import com.github.pagehelper.PageInfo;
import com.quan.project.dto.PageDTO;
import com.quan.project.entity.Appointment;
import com.quan.project.vo.AppointmentVO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 课程预约服务接口
 */
public interface AppointmentService {
    
    /**
     * 学员预约课程
     */
    Appointment createAppointment(Appointment appointment);
    
    /**
     * 更新预约信息
     */
    void updateAppointment(Appointment appointment);
    
    /**
     * 教练处理预约（确认或拒绝）
     */
    void approveAppointment(Integer id, Boolean approved);
    
    /**
     * 申请取消预约
     */
    void requestCancelAppointment(Integer id);
    
    /**
     * 确认取消预约
     */
    void confirmCancelAppointment(Integer id);
    
    /**
     * 根据ID查询预约
     */
    Appointment getAppointmentById(Integer id);
    /**
     * 获取教练课表网格数据
     */
    Map<String, Object> getCoachScheduleGrid(Integer coachId);
    /**
     * 分页查询预约
     */
    PageInfo<Appointment> getAppointmentPage(PageDTO pageRequest);
    
    /**
     * 分页查询预约（包含教练姓名、学员姓名、球台编号）
     */
    PageInfo<AppointmentVO> getAppointmentPageWithInfo(PageDTO pageRequest);
    
    /**
     * 获取教练课表
     */
    List<Appointment> getCoachSchedule(Integer coachId);
    
    /**
     * 查询可用球台
     */
    List<Integer> getAvailableTables(Integer campusId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 检查时间冲突
     */
    boolean checkTimeConflict(Integer coachId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 完成课程（系统自动调用）
     */
    void completeCourse(Integer appointmentId);
    
    /**
     * 查询需要评价的预约
     */
    List<Appointment> getAppointmentsForEvaluation();
    
    /**
     * 检查用户本月取消次数
     */
    boolean canCancelThisMonth();
    
    /**
     * 预览课程费用
     */
    BigDecimal previewCost(Integer coachId, LocalDateTime startTime, LocalDateTime endTime);
}
