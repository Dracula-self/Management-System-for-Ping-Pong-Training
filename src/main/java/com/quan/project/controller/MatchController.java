package com.quan.project.controller;

import com.quan.project.common.R;
import com.quan.project.entity.Match;
import com.quan.project.service.MatchService;
import com.quan.project.vo.MatchVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 比赛对阵管理控制器   
 * 基础路径: /api/matches
 */
@RestController
@RequestMapping("/api/matches")
public class MatchController {
    
    private static final Logger log = LoggerFactory.getLogger(MatchController.class);
    
    @Autowired
    private MatchService matchService;
    
    /**
     * 生成比赛对阵 - POST /api/matches/generate
     * 为指定比赛和组别生成对阵
     */
    @PostMapping("/generate")
    public R<List<Match>> generateMatches(@RequestParam Integer competitionId, 
                                         @RequestParam Integer groupLevel) {
        log.debug("生成比赛对阵，比赛ID: {}, 组别: {}", competitionId, groupLevel);
        List<Match> matches = matchService.generateMatches(competitionId, groupLevel);
        return R.success(matches);
    }
    
    /**
     * 获取比赛对阵 - GET /api/matches/competition/{competitionId}
     * 获取指定比赛和组别的所有对阵信息
     */
    @GetMapping("/competition/{competitionId}")
    public R<List<MatchVO>> getMatchesByCompetition(@PathVariable Integer competitionId, 
                                                   @RequestParam Integer groupLevel) {
        log.debug("获取比赛对阵，比赛ID: {}, 组别: {}", competitionId, groupLevel);
        List<MatchVO> matches = matchService.getMatchesByCompetition(competitionId, groupLevel);
        return R.success(matches);
    }
    
    /**
     * 录入比赛结果 - PUT /api/matches/{matchId}/result
     * 录入指定对阵的比赛结果
     */
    @PutMapping("/{matchId}/result")
    public R<Void> recordMatchResult(@PathVariable Integer matchId, 
                                    @RequestParam Integer winnerId, 
                                    @RequestParam String score) {
        log.debug("录入比赛结果，对阵ID: {}, 获胜者ID: {}, 比分: {}", matchId, winnerId, score);
        matchService.recordMatchResult(matchId, winnerId, score);
        return R.success();
    }
    
    /**
     * 生成下一轮对阵 - POST /api/matches/next-round
     * 根据当前轮次结果生成下一轮对阵
     */
    @PostMapping("/next-round")
    public R<List<Match>> generateNextRound(@RequestParam Integer competitionId, 
                                           @RequestParam Integer groupLevel, 
                                           @RequestParam Integer currentRound) {
        log.debug("生成下一轮对阵，比赛ID: {}, 组别: {}, 当前轮次: {}", competitionId, groupLevel, currentRound);
        List<Match> matches = matchService.generateNextRound(competitionId, groupLevel, currentRound);
        return R.success(matches);
    }
    
    /**
     * 获取比赛排名 - GET /api/matches/ranking
     * 获取指定比赛和组别的排名信息
     */
    @GetMapping("/ranking")
    public R<List<Map<String, Object>>> getCompetitionRanking(@RequestParam Integer competitionId, 
                                                              @RequestParam Integer groupLevel) {
        log.debug("获取比赛排名，比赛ID: {}, 组别: {}", competitionId, groupLevel);
        List<Map<String, Object>> ranking = matchService.getCompetitionRanking(competitionId, groupLevel);
        return R.success(ranking);
    }
    
    /**
     * 检查轮次完成状态 - GET /api/matches/round-status
     * 检查指定轮次是否已完成
     */
    @GetMapping("/round-status")
    public R<Boolean> isRoundCompleted(@RequestParam Integer competitionId, 
                                      @RequestParam Integer groupLevel, 
                                      @RequestParam Integer roundNumber) {
        log.debug("检查轮次完成状态，比赛ID: {}, 组别: {}, 轮次: {}", competitionId, groupLevel, roundNumber);
        boolean completed = matchService.isRoundCompleted(competitionId, groupLevel, roundNumber);
        return R.success(completed);
    }
    
    /**
     * 获取当前轮次 - GET /api/matches/current-round
     * 获取指定比赛和组别的当前轮次
     */
    @GetMapping("/current-round")
    public R<Integer> getCurrentRound(@RequestParam Integer competitionId, 
                                     @RequestParam Integer groupLevel) {
        log.debug("获取当前轮次，比赛ID: {}, 组别: {}", competitionId, groupLevel);
        Integer currentRound = matchService.getCurrentRound(competitionId, groupLevel);
        return R.success(currentRound);
    }
}
