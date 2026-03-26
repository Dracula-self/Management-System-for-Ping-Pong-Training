package com.quan.project.mapper;

import com.quan.project.entity.CompetitionParticipant;
import com.quan.project.vo.CompetitionParticipantVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 比赛参赛者数据访问层
 */
@Mapper
public interface CompetitionParticipantMapper {
    
    /**
     * 插入参赛者记录
     */
    int insert(CompetitionParticipant participant);
    
    /**
     * 根据ID删除参赛者记录
     */
    int deleteById(Integer id);
    
    /**
     * 根据比赛ID和学员ID删除参赛者记录
     */
    int deleteByCompetitionAndStudent(@Param("competitionId") Integer competitionId, 
                                      @Param("studentId") Integer studentId);
    
    /**
     * 根据ID查询参赛者记录
     */
    CompetitionParticipant selectById(Integer id);
    
    /**
     * 根据比赛ID查询参赛者列表（包含学员信息）
     */
    List<CompetitionParticipantVO> selectByCompetitionId(Integer competitionId);
    
    /**
     * 根据学员ID查询参赛记录列表
     */
    List<CompetitionParticipantVO> selectByStudentId(Integer studentId);
    
    /**
     * 检查学员是否已报名该比赛
     */
    int countByCompetitionAndStudent(@Param("competitionId") Integer competitionId, 
                                     @Param("studentId") Integer studentId);
    
    /**
     * 统计比赛报名人数
     */
    int countByCompetitionId(Integer competitionId);
}
