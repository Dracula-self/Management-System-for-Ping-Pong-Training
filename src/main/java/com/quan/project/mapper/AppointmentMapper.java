package com.quan.project.mapper;

import com.quan.project.dto.PageDTO;
import com.quan.project.entity.Appointment;
import com.quan.project.vo.AppointmentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 课程预约数据访问层
 */
@Mapper
public interface AppointmentMapper {
    
    /**
     * 插入预约
     */
    int insert(Appointment appointment);
    
    /**
     * 根据ID删除预约
     */
    int deleteById(Integer id);
    
    /**
     * 更新预约信息
     */
    int update(Appointment appointment);
    
    /**
     * 更新预约状态
     */
    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);
    
    /**
     * 根据ID查询预约
     */
    Appointment selectById(Integer id);
    
    /**
     * 分页查询预约（支持动态条件）
     */
    List<Appointment> selectPageByConditions(@Param("request") PageDTO pageRequest);
    
    /**
     * 分页查询预约（包含教练姓名、学员姓名、球台编号）
     */
    List<AppointmentVO> selectPageWithInfo(@Param("request") PageDTO pageRequest);
    
    /**
     * 根据教练ID查询预约列表
     */
    List<Appointment> selectByCoachId(Integer coachId);
    
    /**
     * 根据学员ID查询预约列表
     */
    List<Appointment> selectByStudentId(Integer studentId);
    
    /**
     * 查询教练在指定时间段的预约
     */
    List<Appointment> selectByCoachAndTimeRange(@Param("coachId") Integer coachId, 
                                               @Param("startTime") LocalDateTime startTime, 
                                               @Param("endTime") LocalDateTime endTime);
    
    /**
     * 查询球台在指定时间段的预约
     */
    List<Appointment> selectByTableAndTimeRange(@Param("tableId") Integer tableId, 
                                               @Param("startTime") LocalDateTime startTime, 
                                               @Param("endTime") LocalDateTime endTime);
    
    /**
     * 查询教练未来一周的课表
     */
    List<Appointment> selectCoachSchedule(@Param("coachId") Integer coachId, 
                                         @Param("startTime") LocalDateTime startTime, 
                                         @Param("endTime") LocalDateTime endTime);
    
    /**
     * 查询需要评价的已完成预约
     */
    List<Appointment> selectCompletedForEvaluation(@Param("userId") Integer userId, 
                                                  @Param("userRole") Integer userRole);
    
    /**
     * 统计用户本月取消次数
     */
    int countCancelledByUserThisMonth(@Param("userId") Integer userId, 
                                     @Param("userRole") Integer userRole);
}
