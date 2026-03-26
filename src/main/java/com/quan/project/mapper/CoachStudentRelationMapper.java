package com.quan.project.mapper;

import com.quan.project.dto.PageDTO;
import com.quan.project.entity.CoachStudentRelation;
import com.quan.project.vo.CoachStudentRelationVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 师生关系数据访问层
 */
@Mapper
public interface CoachStudentRelationMapper {
    
    /**
     * 插入师生关系
     */
    int insert(CoachStudentRelation relation);
    
    /**
     * 根据ID删除师生关系
     */
    int deleteById(Integer id);
    
    /**
     * 更新师生关系状态
     */
    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);
    
    /**
     * 根据ID查询师生关系
     */
    CoachStudentRelation selectById(Integer id);
    
    /**
     * 分页查询师生关系（支持动态条件）
     */
    List<CoachStudentRelation> selectPageByConditions(@Param("request") PageDTO pageRequest);
    
    /**
     * 分页查询师生关系（包含用户信息）
     */
    List<CoachStudentRelationVO> selectPageWithUserInfo(@Param("request") PageDTO pageRequest);
    
    /**
     * 根据教练ID查询师生关系列表
     */
    List<CoachStudentRelation> selectByCoachId(Integer coachId);
    
    /**
     * 根据学员ID查询师生关系列表
     */
    List<CoachStudentRelation> selectByStudentId(Integer studentId);
    
    /**
     * 查询教练和学员之间的关系
     */
    CoachStudentRelation selectByCoachAndStudent(@Param("coachId") Integer coachId, @Param("studentId") Integer studentId);
    
    /**
     * 统计教练的学员数量
     */
    int countStudentsByCoachId(Integer coachId);
    
    /**
     * 统计学员的教练数量
     */
    int countCoachesByStudentId(Integer studentId);
    
    /**
     * 查询学员的已确认教练关系
     */
    List<CoachStudentRelation> selectConfirmedByStudentId(Integer studentId);
    
    /**
     * 查询教练的已确认学员关系
     */
    List<CoachStudentRelation> selectConfirmedByCoachId(Integer coachId);
}
