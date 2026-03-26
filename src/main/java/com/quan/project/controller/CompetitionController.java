package com.quan.project.controller;

import com.github.pagehelper.PageInfo;
import com.quan.project.common.R;
import com.quan.project.dto.CompetitionRegistrationDTO;
import com.quan.project.dto.PageDTO;
import com.quan.project.entity.Competition;
import com.quan.project.service.CompetitionService;
import com.quan.project.vo.CompetitionParticipantVO;
import com.quan.project.vo.CompetitionVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 比赛管理控制器
 * 基础路径: /api/competitions
 */
@RestController
@RequestMapping("/api/competitions")
public class CompetitionController {
    
    private static final Logger log = LoggerFactory.getLogger(CompetitionController.class);
    
    @Autowired
    private CompetitionService competitionService;
    
    /**
     * 7.1 查询比赛列表 - POST /api/competitions/search
     * 查询比赛列表
     */
    @PostMapping("/search")
    public R<PageInfo<CompetitionVO>> search(@RequestBody PageDTO pageRequest) {
        log.debug("分页查询比赛，参数: {}", pageRequest);
        PageInfo<CompetitionVO> pageInfo = competitionService.getCompetitionPage(pageRequest);
        return R.success(pageInfo);
    }
    
    /**
     * 7.2 创建比赛 - POST /api/competitions
     * 创建新的比赛
     */
    @PostMapping
    public R<Competition> create(@RequestBody Competition competition) {
        log.debug("创建比赛，参数: {}", competition);
        Competition createdCompetition = competitionService.createCompetition(competition);
        return R.success(createdCompetition);
    }
    
    /**
     * 7.3 删除比赛 - DELETE /api/competitions/{id}
     * 删除指定比赛
     */
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Integer id) {
        log.debug("删除比赛，ID: {}", id);
        competitionService.deleteCompetition(id);
        return R.success();
    }
    
    /**
     * 获取比赛详情 - GET /api/competitions/{id}
     * 根据ID获取比赛详情
     */
    @GetMapping("/{id}")
    public R<Competition> getById(@PathVariable Integer id) {
        log.debug("查询比赛详情，ID: {}", id);
        Competition competition = competitionService.getCompetitionById(id);
        return R.success(competition);
    }
    
    /**
     * 更新比赛信息 - PUT /api/competitions/{id}
     * 更新指定比赛信息
     */
    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Integer id, @RequestBody Competition competition) {
        log.debug("更新比赛，ID: {}, 参数: {}", id, competition);
        competition.setId(id);
        competitionService.updateCompetition(competition);
        return R.success();
    }
    
    /**
     * 查询可报名比赛 - GET /api/competitions/available
     * 查询当前可报名的比赛列表（公开接口）
     */
    @GetMapping("/available")
    public R<List<Competition>> getAvailableCompetitions() {
        log.debug("查询可报名比赛");
        List<Competition> competitions = competitionService.getAvailableCompetitions();
        return R.success(competitions);
    }
    
    /**
     * 查询进行中比赛 - GET /api/competitions/in-progress
     * 查询当前进行中的比赛列表
     */
    @GetMapping("/in-progress")
    public R<List<Competition>> getInProgressCompetitions() {
        log.debug("查询进行中比赛");
        List<Competition> competitions = competitionService.getInProgressCompetitions();
        return R.success(competitions);
    }
    
    /**
     * 查询所有比赛 - GET /api/competitions/all
     * 查询所有比赛列表
     */
    @GetMapping("/all")
    public R<List<Competition>> getAllCompetitions() {
        log.debug("查询所有比赛");
        List<Competition> competitions = competitionService.getAllCompetitions();
        return R.success(competitions);
    }
    
    /**
     * 更新比赛状态 - PUT /api/competitions/{id}/status
     * 更新比赛状态
     */
    @PutMapping("/{id}/status")
    public R<Void> updateStatus(@PathVariable Integer id, @RequestParam Integer status) {
        log.debug("更新比赛状态，ID: {}, 状态: {}", id, status);
        competitionService.updateCompetitionStatus(id, status);
        return R.success();
    }
    
    /**
     * 开始比赛报名 - PUT /api/competitions/{id}/start-registration
     * 开始指定比赛的报名
     */
    @PutMapping("/{id}/start-registration")
    public R<Void> startRegistration(@PathVariable Integer id) {
        log.debug("开始比赛报名，ID: {}", id);
        competitionService.startRegistration(id);
        return R.success();
    }
    
    /**
     * 结束比赛报名 - PUT /api/competitions/{id}/end-registration
     * 结束指定比赛的报名
     */
    @PutMapping("/{id}/end-registration")
    public R<Void> endRegistration(@PathVariable Integer id) {
        log.debug("结束比赛报名，ID: {}", id);
        competitionService.endRegistration(id);
        return R.success();
    }
    
    /**
     * 开始比赛 - PUT /api/competitions/{id}/start
     * 开始指定比赛
     */
    @PutMapping("/{id}/start")
    public R<Void> startCompetition(@PathVariable Integer id) {
        log.debug("开始比赛，ID: {}", id);
        competitionService.startCompetition(id);
        return R.success();
    }
    
    /**
     * 结束比赛 - PUT /api/competitions/{id}/end
     * 结束指定比赛
     */
    @PutMapping("/{id}/end")
    public R<Void> endCompetition(@PathVariable Integer id) {
        log.debug("结束比赛，ID: {}", id);
        competitionService.endCompetition(id);
        return R.success();
    }
    
    /**
     * 获取比赛参赛者列表 - GET /api/competitions/{id}/participants
     * 查询指定比赛的所有参赛者信息
     */
    @GetMapping("/{id}/participants")
    public R<List<CompetitionParticipantVO>> getParticipants(@PathVariable Integer id) {
        log.debug("查询比赛参赛者列表，比赛ID: {}", id);
        List<CompetitionParticipantVO> participants = competitionService.getParticipantsByCompetitionId(id);
        return R.success(participants);
    }
    
    /**
     * 移除参赛者 - DELETE /api/competitions/participants/{participantId}
     * 移除指定的参赛者
     */
    @DeleteMapping("/participants/{participantId}")
    public R<Void> removeParticipant(@PathVariable Integer participantId) {
        log.debug("移除参赛者，参赛者ID: {}", participantId);
        competitionService.removeParticipant(participantId);
        return R.success();
    }
    
    /**
     * 获取我的比赛记录 - GET /api/competitions/my-competitions
     * 查询当前用户参与的比赛列表
     */
    @GetMapping("/my-competitions")
    public R<List<CompetitionParticipantVO>> getMyCompetitions() {
        log.debug("查询我的比赛记录");
        List<CompetitionParticipantVO> myCompetitions = competitionService.getMyCompetitions();
        return R.success(myCompetitions);
    }
    
    /**
     * 比赛报名 - POST /api/competitions/register
     * 学员报名参加比赛
     */
    @PostMapping("/register")
    public R<Void> registerCompetition(@RequestBody CompetitionRegistrationDTO registrationDTO) {
        log.debug("比赛报名，参数: {}", registrationDTO);
        competitionService.registerCompetition(registrationDTO);
        return R.success();
    }
}
