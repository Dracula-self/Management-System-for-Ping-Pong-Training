package com.quan.project.service;

import com.quan.project.entity.Match;
import com.quan.project.vo.MatchVO;
import java.util.List;
import java.util.Map;

/**
 * 比赛对阵服务接口
 */
public interface MatchService {
    
    /**
     * 生成比赛对阵（交叉淘汰赛制）
     * @param competitionId 比赛ID
     * @param groupLevel 组别
     * @return 生成的对阵列表
     */
    List<Match> generateMatches(Integer competitionId, Integer groupLevel);
    
    /**
     * 获取比赛的所有对阵信息
     * @param competitionId 比赛ID
     * @param groupLevel 组别
     * @return 对阵信息列表
     */
    List<MatchVO> getMatchesByCompetition(Integer competitionId, Integer groupLevel);
    
    /**
     * 录入比赛结果
     * @param matchId 对阵ID
     * @param winnerId 获胜者ID
     * @param score 比分
     */
    void recordMatchResult(Integer matchId, Integer winnerId, String score);
    
    /**
     * 生成下一轮对阵
     * @param competitionId 比赛ID
     * @param groupLevel 组别
     * @param currentRound 当前轮次
     * @return 下一轮对阵列表
     */
    List<Match> generateNextRound(Integer competitionId, Integer groupLevel, Integer currentRound);
    
    /**
     * 获取比赛排名
     * @param competitionId 比赛ID
     * @param groupLevel 组别
     * @return 排名信息
     */
    List<Map<String, Object>> getCompetitionRanking(Integer competitionId, Integer groupLevel);
    
    /**
     * 检查当前轮次是否已完成
     * @param competitionId 比赛ID
     * @param groupLevel 组别
     * @param roundNumber 轮次
     * @return 是否完成
     */
    boolean isRoundCompleted(Integer competitionId, Integer groupLevel, Integer roundNumber);
    
    /**
     * 获取比赛当前轮次
     * @param competitionId 比赛ID
     * @param groupLevel 组别
     * @return 当前轮次
     */
    Integer getCurrentRound(Integer competitionId, Integer groupLevel);
}
