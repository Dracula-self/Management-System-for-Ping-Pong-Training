package com.quan.project.service;

import com.github.pagehelper.PageInfo;
import com.quan.project.dto.CompetitionRegistrationDTO;
import com.quan.project.dto.PageDTO;
import com.quan.project.entity.Competition;
import com.quan.project.vo.CompetitionParticipantVO;
import com.quan.project.vo.CompetitionVO;

import java.util.List;

/**
 * 比赛服务接口
 */
public interface CompetitionService {
    
    /**
     * 创建比赛
     */
    Competition createCompetition(Competition competition);
    
    /**
     * 删除比赛
     */
    void deleteCompetition(Integer id);
    
    /**
     * 更新比赛信息
     */
    void updateCompetition(Competition competition);
    
    /**
     * 更新比赛状态
     */
    void updateCompetitionStatus(Integer id, Integer status);
    
    /**
     * 根据ID查询比赛
     */
    Competition getCompetitionById(Integer id);
    
    /**
     * 分页查询比赛（包含报名人数）
     */
    PageInfo<CompetitionVO> getCompetitionPage(PageDTO pageRequest);
    
    /**
     * 查询所有比赛
     */
    List<Competition> getAllCompetitions();
    
    /**
     * 查询可报名的比赛
     */
    List<Competition> getAvailableCompetitions();
    
    /**
     * 查询进行中的比赛
     */
    List<Competition> getInProgressCompetitions();
    
    /**
     * 开始比赛报名
     */
    void startRegistration(Integer competitionId);
    
    /**
     * 结束比赛报名
     */
    void endRegistration(Integer competitionId);
    
    /**
     * 开始比赛
     */
    void startCompetition(Integer competitionId);
    
    /**
     * 结束比赛
     */
    void endCompetition(Integer competitionId);
    
    /**
     * 获取比赛参赛者列表
     */
    List<CompetitionParticipantVO> getParticipantsByCompetitionId(Integer competitionId);
    
    /**
     * 移除参赛者
     */
    void removeParticipant(Integer participantId);
    
    /**
     * 获取我的比赛记录
     */
    List<CompetitionParticipantVO> getMyCompetitions();
    
    /**
     * 比赛报名
     */
    void registerCompetition(CompetitionRegistrationDTO registrationDTO);
}
