package com.quan.project.mapper;

import com.quan.project.dto.PageDTO;
import com.quan.project.entity.Evaluation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 训练评价数据访问层
 */
@Mapper
public interface EvaluationMapper {
    
    /**
     * 插入评价
     */
    int insert(Evaluation evaluation);
    
    /**
     * 根据ID删除评价
     */
    int deleteById(Integer id);
    
    /**
     * 更新评价信息
     */
    int update(Evaluation evaluation);
    
    /**
     * 根据ID查询评价
     */
    Evaluation selectById(Integer id);
    
    /**
     * 分页查询评价（支持动态条件）
     */
    List<Evaluation> selectPageByConditions(@Param("request") PageDTO pageRequest);
    
    /**
     * 根据预约ID查询评价
     */
    Evaluation selectByAppointmentId(Integer appointmentId);
    
    /**
     * 查询学员的评价记录
     */
    List<Evaluation> selectByStudentId(Integer studentId);
    
    /**
     * 查询教练的评价记录
     */
    List<Evaluation> selectByCoachId(Integer coachId);
    
    /**
     * 查询待评价的预约（学员视角）
     */
    List<Evaluation> selectPendingEvaluationsForStudent(Integer studentId);
    
    /**
     * 查询待评价的预约（教练视角）
     */
    List<Evaluation> selectPendingEvaluationsForCoach(Integer coachId);
    
    /**
     * 统计用户评价数量
     */
    int countEvaluationsByUser(@Param("userId") Integer userId, @Param("userRole") Integer userRole);
}
