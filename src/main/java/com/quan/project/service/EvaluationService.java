package com.quan.project.service;

import com.github.pagehelper.PageInfo;
import com.quan.project.dto.PageDTO;
import com.quan.project.entity.Evaluation;

import java.util.List;

/**
 * 训练评价服务接口
 */
public interface EvaluationService {
    
    /**
     * 提交评价
     */
    Evaluation submitEvaluation(Evaluation evaluation);
    
    /**
     * 学员提交评价
     */
    Evaluation submitStudentEvaluation(Integer appointmentId, String studentFeedback);
    
    /**
     * 教练提交评价
     */
    Evaluation submitCoachEvaluation(Integer appointmentId, String coachFeedback);
    
    /**
     * 更新评价
     */
    void updateEvaluation(Evaluation evaluation);
    
    /**
     * 删除评价
     */
    void deleteEvaluation(Integer id);
    
    /**
     * 根据ID查询评价
     */
    Evaluation getEvaluationById(Integer id);
    
    /**
     * 分页查询评价
     */
    PageInfo<Evaluation> getEvaluationPage(PageDTO pageRequest);
    
    /**
     * 根据预约ID查询评价
     */
    Evaluation getEvaluationByAppointmentId(Integer appointmentId);
    
    /**
     * 查询学员的评价记录
     */
    List<Evaluation> getStudentEvaluations(Integer studentId);
    
    /**
     * 查询教练的评价记录
     */
    List<Evaluation> getCoachEvaluations(Integer coachId);
    
    /**
     * 查询待评价的预约（学员视角）
     */
    List<Evaluation> getPendingEvaluationsForStudent(Integer studentId);
    
    /**
     * 查询待评价的预约（教练视角）
     */
    List<Evaluation> getPendingEvaluationsForCoach(Integer coachId);
    
    /**
     * 查询当前用户待评价的预约
     */
    List<Evaluation> getPendingEvaluations();
    
    /**
     * 统计用户评价数量
     */
    int countUserEvaluations(Integer userId, Integer userRole);
    
    /**
     * 检查是否可以评价
     */
    boolean canEvaluate(Integer appointmentId, Integer userRole);
}
