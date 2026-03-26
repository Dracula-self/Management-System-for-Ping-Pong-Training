package com.quan.project.service.impl;

import com.quan.project.entity.Match;
import com.quan.project.exception.BusinessException;
import com.quan.project.mapper.CompetitionParticipantMapper;
import com.quan.project.mapper.MatchMapper;
import com.quan.project.mapper.TableMapper;
import com.quan.project.service.MatchService;
import com.quan.project.vo.CompetitionParticipantVO;
import com.quan.project.vo.MatchVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 比赛对阵服务实现类
 */
@Service
public class MatchServiceImpl implements MatchService {
    
    private static final Logger log = LoggerFactory.getLogger(MatchServiceImpl.class);
    
    @Autowired
    private MatchMapper matchMapper;
    
    @Autowired
    private CompetitionParticipantMapper participantMapper;
    
    @Autowired
    private TableMapper tableMapper;
    
    @Override
    @Transactional
    public List<Match> generateMatches(Integer competitionId, Integer groupLevel) {
        try {
            log.debug("开始生成比赛对阵，比赛ID: {}, 组别: {}", competitionId, groupLevel);
            
            // 清除该组别的所有对阵记录
            matchMapper.deleteByCompetition(competitionId, groupLevel);
            
            // 获取参赛选手
            List<CompetitionParticipantVO> participants = participantMapper.selectByCompetitionId(competitionId)
                .stream()
                .filter(p -> p.getGroupLevel().equals(groupLevel))
                .collect(Collectors.toList());
            
            if (participants.size() < 2) {
                throw new BusinessException("参赛人数不足，无法生成对阵");
            }
            
            log.debug("找到 {} 名参赛选手", participants.size());
            
            // 随机打乱参赛选手顺序
            Collections.shuffle(participants);
            
            // 生成第一轮对阵
            List<Match> firstRoundMatches = generateFirstRound(competitionId, groupLevel, participants);
            
            // 批量插入对阵记录
            if (!firstRoundMatches.isEmpty()) {
                matchMapper.insertBatch(firstRoundMatches);
                log.debug("成功生成 {} 场第一轮比赛", firstRoundMatches.size());
            }
            
            return firstRoundMatches;
            
        } catch (Exception e) {
            log.error("生成比赛对阵失败，比赛ID: {}, 组别: {}", competitionId, groupLevel, e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("生成比赛对阵失败");
        }
    }
    
    /**
     * 生成第一轮对阵
     */
    private List<Match> generateFirstRound(Integer competitionId, Integer groupLevel, List<CompetitionParticipantVO> participants) {
        List<Match> matches = new ArrayList<>();
        int participantCount = participants.size();
        
        // 获取可用球台
        List<Integer> availableTables = getAvailableTables();
        if (availableTables.isEmpty()) {
            throw new BusinessException("没有可用的球台");
        }
        
        int tableIndex = 0;
        
        if (participantCount <= 6) {
            // 6人以下直接循环赛制或简单对阵
            for (int i = 0; i < participantCount; i += 2) {
                if (i + 1 < participantCount) {
                    // 正常对阵
                    Match match = new Match();
                    match.setCompetitionId(competitionId);
                    match.setGroupLevel(groupLevel);
                    match.setRoundNumber(1);
                    match.setPlayer1Id(participants.get(i).getStudentId());
                    match.setPlayer2Id(participants.get(i + 1).getStudentId());
                    match.setTableId(availableTables.get(tableIndex % availableTables.size()));
                    matches.add(match);
                    tableIndex++;
                } else {
                    // 奇数人数，最后一人轮空
                    Match match = new Match();
                    match.setCompetitionId(competitionId);
                    match.setGroupLevel(groupLevel);
                    match.setRoundNumber(1);
                    match.setPlayer1Id(participants.get(i).getStudentId());
                    match.setPlayer2Id(null); // 轮空
                    match.setTableId(availableTables.get(tableIndex % availableTables.size()));
                    match.setWinnerId(participants.get(i).getStudentId()); // 轮空直接获胜
                    match.setScore("轮空");
                    matches.add(match);
                }
            }
        } else {
            // 6人以上使用交叉淘汰赛制
            // 第一轮：分成偶数组，组内第一名第二名进行交叉淘汰
            int groupSize = 4; // 每组4人
            List<List<CompetitionParticipantVO>> groups = new ArrayList<>();
            
            for (int i = 0; i < participantCount; i += groupSize) {
                List<CompetitionParticipantVO> group = new ArrayList<>();
                for (int j = i; j < Math.min(i + groupSize, participantCount); j++) {
                    group.add(participants.get(j));
                }
                if (group.size() >= 2) {
                    groups.add(group);
                }
            }
            
            // 处理剩余不足一组的选手
            if (participantCount % groupSize != 0) {
                List<CompetitionParticipantVO> lastGroup = groups.get(groups.size() - 1);
                if (lastGroup.size() < 2) {
                    // 如果最后一组人数不足2人，合并到前一组
                    if (groups.size() > 1) {
                        List<CompetitionParticipantVO> prevGroup = groups.get(groups.size() - 2);
                        prevGroup.addAll(lastGroup);
                        groups.remove(groups.size() - 1);
                    }
                }
            }
            
            // 为每组生成对阵
            for (List<CompetitionParticipantVO> group : groups) {
                // 组内交叉对阵
                for (int i = 0; i < group.size(); i += 2) {
                    if (i + 1 < group.size()) {
                        Match match = new Match();
                        match.setCompetitionId(competitionId);
                        match.setGroupLevel(groupLevel);
                        match.setRoundNumber(1);
                        match.setPlayer1Id(group.get(i).getStudentId());
                        match.setPlayer2Id(group.get(i + 1).getStudentId());
                        match.setTableId(availableTables.get(tableIndex % availableTables.size()));
                        matches.add(match);
                        tableIndex++;
                    } else {
                        // 奇数人数，轮空
                        Match match = new Match();
                        match.setCompetitionId(competitionId);
                        match.setGroupLevel(groupLevel);
                        match.setRoundNumber(1);
                        match.setPlayer1Id(group.get(i).getStudentId());
                        match.setPlayer2Id(null);
                        match.setTableId(availableTables.get(tableIndex % availableTables.size()));
                        match.setWinnerId(group.get(i).getStudentId());
                        match.setScore("轮空");
                        matches.add(match);
                    }
                }
            }
        }
        
        return matches;
    }
    
    @Override
    public List<MatchVO> getMatchesByCompetition(Integer competitionId, Integer groupLevel) {
        try {
            log.debug("获取比赛对阵信息，比赛ID: {}, 组别: {}", competitionId, groupLevel);
            return matchMapper.selectByCompetitionAndGroup(competitionId, groupLevel);
        } catch (Exception e) {
            log.error("获取比赛对阵信息失败，比赛ID: {}, 组别: {}", competitionId, groupLevel, e);
            throw new BusinessException("获取比赛对阵信息失败");
        }
    }
    
    @Override
    @Transactional
    public void recordMatchResult(Integer matchId, Integer winnerId, String score) {
        try {
            log.debug("录入比赛结果，对阵ID: {}, 获胜者ID: {}, 比分: {}", matchId, winnerId, score);
            
            Match match = matchMapper.selectById(matchId);
            if (match == null) {
                throw new BusinessException("比赛对阵不存在");
            }
            
            // 验证获胜者是否为参赛选手之一
            if (!winnerId.equals(match.getPlayer1Id()) && !winnerId.equals(match.getPlayer2Id())) {
                throw new BusinessException("获胜者必须是参赛选手之一");
            }
            
            // 更新比赛结果
            int result = matchMapper.updateResult(matchId, winnerId, score);
            if (result <= 0) {
                throw new BusinessException("更新比赛结果失败");
            }
            
            log.debug("比赛结果录入成功");
            
        } catch (Exception e) {
            log.error("录入比赛结果失败，对阵ID: {}", matchId, e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("录入比赛结果失败");
        }
    }
    
    @Override
    @Transactional
    public List<Match> generateNextRound(Integer competitionId, Integer groupLevel, Integer currentRound) {
        try {
            log.debug("生成下一轮对阵，比赛ID: {}, 组别: {}, 当前轮次: {}", competitionId, groupLevel, currentRound);
            
            // 检查当前轮次是否已完成
            if (!isRoundCompleted(competitionId, groupLevel, currentRound)) {
                throw new BusinessException("当前轮次尚未完成，无法生成下一轮");
            }
            
            // 获取当前轮次的获胜者
            List<Match> currentRoundMatches = matchMapper.selectByRound(competitionId, groupLevel, currentRound);
            List<Integer> winners = currentRoundMatches.stream()
                .map(Match::getWinnerId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
            
            if (winners.size() <= 1) {
                log.debug("比赛已结束，获胜者: {}", winners.isEmpty() ? "无" : winners.get(0));
                return new ArrayList<>();
            }
            
            // 随机打乱获胜者顺序
            Collections.shuffle(winners);
            
            List<Match> nextRoundMatches = new ArrayList<>();
            int nextRound = currentRound + 1;
            
            // 获取可用球台
            List<Integer> availableTables = getAvailableTables();
            int tableIndex = 0;
            
            // 如果人数不是4的倍数，随机抽两个人轮空
            if (winners.size() % 4 != 0 && winners.size() > 2) {
                int byeCount = winners.size() % 2; // 确保剩余人数为偶数
                for (int i = 0; i < byeCount; i++) {
                    if (!winners.isEmpty()) {
                        Integer byePlayer = winners.remove(0);
                        Match byeMatch = new Match();
                        byeMatch.setCompetitionId(competitionId);
                        byeMatch.setGroupLevel(groupLevel);
                        byeMatch.setRoundNumber(nextRound);
                        byeMatch.setPlayer1Id(byePlayer);
                        byeMatch.setPlayer2Id(null);
                        byeMatch.setTableId(availableTables.get(tableIndex % availableTables.size()));
                        byeMatch.setWinnerId(byePlayer);
                        byeMatch.setScore("轮空");
                        nextRoundMatches.add(byeMatch);
                        tableIndex++;
                    }
                }
            }
            
            // 生成正常对阵
            for (int i = 0; i < winners.size(); i += 2) {
                if (i + 1 < winners.size()) {
                    Match match = new Match();
                    match.setCompetitionId(competitionId);
                    match.setGroupLevel(groupLevel);
                    match.setRoundNumber(nextRound);
                    match.setPlayer1Id(winners.get(i));
                    match.setPlayer2Id(winners.get(i + 1));
                    match.setTableId(availableTables.get(tableIndex % availableTables.size()));
                    nextRoundMatches.add(match);
                    tableIndex++;
                }
            }
            
            // 批量插入下一轮对阵
            if (!nextRoundMatches.isEmpty()) {
                matchMapper.insertBatch(nextRoundMatches);
                log.debug("成功生成 {} 场第{}轮比赛", nextRoundMatches.size(), nextRound);
            }
            
            return nextRoundMatches;
            
        } catch (Exception e) {
            log.error("生成下一轮对阵失败，比赛ID: {}, 组别: {}, 当前轮次: {}", competitionId, groupLevel, currentRound, e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("生成下一轮对阵失败");
        }
    }
    
    @Override
    public List<Map<String, Object>> getCompetitionRanking(Integer competitionId, Integer groupLevel) {
        try {
            log.debug("获取比赛排名，比赛ID: {}, 组别: {}", competitionId, groupLevel);
            
            // 获取所有对阵记录
            List<MatchVO> matches = matchMapper.selectByCompetitionAndGroup(competitionId, groupLevel);
            
            // 统计每个选手的积分（胜2分，负1分，弃权0分）
            Map<Integer, Integer> playerScores = new HashMap<>();
            Map<Integer, String> playerNames = new HashMap<>();
            Map<Integer, Integer> playerWins = new HashMap<>();
            Map<Integer, Integer> playerLosses = new HashMap<>();
            
            for (MatchVO match : matches) {
                if (match.getWinnerId() != null) {
                    // 记录选手姓名
                    if (match.getPlayer1Id() != null) {
                        playerNames.put(match.getPlayer1Id(), match.getPlayer1Name());
                    }
                    if (match.getPlayer2Id() != null) {
                        playerNames.put(match.getPlayer2Id(), match.getPlayer2Name());
                    }
                    
                    if (match.getPlayer2Id() == null) {
                        // 轮空，直接获胜
                        playerScores.put(match.getWinnerId(), playerScores.getOrDefault(match.getWinnerId(), 0) + 2);
                        playerWins.put(match.getWinnerId(), playerWins.getOrDefault(match.getWinnerId(), 0) + 1);
                    } else {
                        // 正常比赛
                        Integer winner = match.getWinnerId();
                        Integer loser = winner.equals(match.getPlayer1Id()) ? match.getPlayer2Id() : match.getPlayer1Id();
                        
                        // 胜者得2分
                        playerScores.put(winner, playerScores.getOrDefault(winner, 0) + 2);
                        playerWins.put(winner, playerWins.getOrDefault(winner, 0) + 1);
                        
                        // 负者得1分
                        playerScores.put(loser, playerScores.getOrDefault(loser, 0) + 1);
                        playerLosses.put(loser, playerLosses.getOrDefault(loser, 0) + 1);
                    }
                }
            }
            
            // 转换为排名列表并排序
            List<Map<String, Object>> ranking = new ArrayList<>();
            for (Map.Entry<Integer, Integer> entry : playerScores.entrySet()) {
                Map<String, Object> playerRank = new HashMap<>();
                playerRank.put("playerId", entry.getKey());
                playerRank.put("playerName", playerNames.get(entry.getKey()));
                playerRank.put("score", entry.getValue());
                playerRank.put("wins", playerWins.getOrDefault(entry.getKey(), 0));
                playerRank.put("losses", playerLosses.getOrDefault(entry.getKey(), 0));
                ranking.add(playerRank);
            }
            
            // 按积分降序排序
            ranking.sort((a, b) -> {
                int scoreCompare = Integer.compare((Integer) b.get("score"), (Integer) a.get("score"));
                if (scoreCompare == 0) {
                    // 积分相同，按胜场数排序
                    return Integer.compare((Integer) b.get("wins"), (Integer) a.get("wins"));
                }
                return scoreCompare;
            });
            
            // 添加排名
            for (int i = 0; i < ranking.size(); i++) {
                ranking.get(i).put("rank", i + 1);
            }
            
            return ranking;
            
        } catch (Exception e) {
            log.error("获取比赛排名失败，比赛ID: {}, 组别: {}", competitionId, groupLevel, e);
            throw new BusinessException("获取比赛排名失败");
        }
    }
    
    @Override
    public boolean isRoundCompleted(Integer competitionId, Integer groupLevel, Integer roundNumber) {
        try {
            Integer completedCount = matchMapper.countCompletedMatches(competitionId, groupLevel, roundNumber);
            Integer totalCount = matchMapper.countTotalMatches(competitionId, groupLevel, roundNumber);
            return completedCount != null && totalCount != null && completedCount.equals(totalCount);
        } catch (Exception e) {
            log.error("检查轮次完成状态失败，比赛ID: {}, 组别: {}, 轮次: {}", competitionId, groupLevel, roundNumber, e);
            return false;
        }
    }
    
    @Override
    public Integer getCurrentRound(Integer competitionId, Integer groupLevel) {
        try {
            Integer maxRound = matchMapper.selectMaxRound(competitionId, groupLevel);
            return maxRound != null ? maxRound : 0;
        } catch (Exception e) {
            log.error("获取当前轮次失败，比赛ID: {}, 组别: {}", competitionId, groupLevel, e);
            return 0;
        }
    }
    
    /**
     * 获取可用球台列表
     */
    private List<Integer> getAvailableTables() {
        try {
            // 简单实现：使用固定的球台ID，确保数据库中有对应记录
            return Arrays.asList(1, 2, 3, 4); // 使用初始化的4张球台
        } catch (Exception e) {
            log.warn("获取可用球台失败，使用默认球台", e);
            return Arrays.asList(1); // 默认球台ID=1
        }
    }
}
