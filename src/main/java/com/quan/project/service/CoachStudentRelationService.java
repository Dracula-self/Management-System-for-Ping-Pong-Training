package com.quan.project.service;

import com.github.pagehelper.PageInfo;
import com.quan.project.dto.PageDTO;
import com.quan.project.entity.CoachStudentRelation;
import com.quan.project.entity.User;
import com.quan.project.vo.CoachStudentRelationVO;

import java.util.List;

/**
 * 师生关系服务接口
 */
public interface CoachStudentRelationService {
    
    /**
     * 学员申请选择教练
     */
    void applyCoach(Integer coachId);
    
    /**
     * 教练处理学员申请
     */
    void approveApplication(Integer id, Boolean approved);
    
    /**
     * 查询师生关系
     */
    PageInfo<CoachStudentRelation> getRelationPage(PageDTO pageRequest);
    
    /**
     * 查询师生关系（包含用户信息）
     */
    PageInfo<CoachStudentRelationVO> getRelationPageWithUserInfo(PageDTO pageRequest);
    
    /**
     * 根据ID查询师生关系
     */
    CoachStudentRelation getRelationById(Integer id);
    
    /**
     * 查询学员的教练关系
     */
    List<CoachStudentRelation> getStudentCoaches(Integer studentId);
    
    /**
     * 查询教练的学员关系
     */
    List<CoachStudentRelation> getCoachStudents(Integer coachId);
    
    /**
     * 学员更换教练申请
     */
    void changeCoachApplication(Integer currentCoachId, Integer newCoachId);
    
    /**
     * 解除师生关系
     */
    void terminateRelation(Integer id);
    
    /**
     * 检查师生关系是否存在
     */
    boolean checkRelationExists(Integer coachId, Integer studentId);
    
    /**
     * 检查教练学员数量限制
     */
    boolean checkCoachStudentLimit(Integer coachId);
    
    /**
     * 检查学员教练数量限制
     */
    boolean checkStudentCoachLimit(Integer studentId);
    
    /**
     * 获取可申请的教练列表
     * 返回当前学员可以申请的教练（排除已申请的、学员数量已满的）
     */
    List<User> getAvailableCoaches();
    
    /**
     * 获取学员的教练列表
     * 返回当前学员已确认关系的教练列表（用于预约课程）
     */
    List<User> getMyCoaches();
}
