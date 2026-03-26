package com.quan.project.controller;

import com.github.pagehelper.PageInfo;
import com.quan.project.common.R;
import com.quan.project.dto.PageDTO;
import com.quan.project.entity.Evaluation;
import com.quan.project.service.EvaluationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 训练评价管理控制器
 * 基础路径: /api/evaluations
 */
@RestController
@RequestMapping("/api/evaluations")
public class EvaluationController {
    
    private static final Logger log = LoggerFactory.getLogger(EvaluationController.class);
    
    @Autowired
    private EvaluationService evaluationService;
    
    /**
     * 5.1 提交评价 - POST /api/evaluations
     * 学员或教练提交训练评价
     */
    @PostMapping
    public R<Evaluation> submit(@RequestBody Evaluation evaluation) {
        log.debug("提交评价，参数: {}", evaluation);
        Evaluation submittedEvaluation = evaluationService.submitEvaluation(evaluation);
        return R.success(submittedEvaluation);
    }
    
    /**
     * 5.2 查询评价列表 - POST /api/evaluations/search
     * 查询评价列表
     */
    @PostMapping("/search")
    public R<PageInfo<Evaluation>> search(@RequestBody PageDTO pageRequest) {
        log.debug("分页查询评价，参数: {}", pageRequest);
        PageInfo<Evaluation> pageInfo = evaluationService.getEvaluationPage(pageRequest);
        return R.success(pageInfo);
    }
    
    /**
     * 5.3 获取评价详情 - GET /api/evaluations/{id}
     * 根据ID获取评价详情
     */
    @GetMapping("/{id}")
    public R<Evaluation> getById(@PathVariable Integer id) {
        log.debug("查询评价详情，ID: {}", id);
        Evaluation evaluation = evaluationService.getEvaluationById(id);
        return R.success(evaluation);
    }
    
    /**
     * 学员提交评价 - POST /api/evaluations/student
     * 学员提交课程评价
     */
    @PostMapping("/student")
    public R<Evaluation> submitStudentEvaluation(@RequestParam Integer appointmentId, @RequestParam String studentFeedback) {
        log.debug("学员提交评价，预约ID: {}, 评价内容: {}", appointmentId, studentFeedback);
        Evaluation evaluation = evaluationService.submitStudentEvaluation(appointmentId, studentFeedback);
        return R.success(evaluation);
    }
    
    /**
     * 教练提交评价 - POST /api/evaluations/coach
     * 教练提交课程评价
     */
    @PostMapping("/coach")
    public R<Evaluation> submitCoachEvaluation(@RequestParam Integer appointmentId, @RequestParam String coachFeedback) {
        log.debug("教练提交评价，预约ID: {}, 评价内容: {}", appointmentId, coachFeedback);
        Evaluation evaluation = evaluationService.submitCoachEvaluation(appointmentId, coachFeedback);
        return R.success(evaluation);
    }
    
    /**
     * 更新评价 - PUT /api/evaluations/{id}
     * 更新评价内容
     */
    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Integer id, @RequestBody Evaluation evaluation) {
        log.debug("更新评价，ID: {}, 参数: {}", id, evaluation);
        evaluation.setId(id);
        evaluationService.updateEvaluation(evaluation);
        return R.success();
    }
    
    /**
     * 删除评价 - DELETE /api/evaluations/{id}
     * 删除评价记录（管理员功能）
     */
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Integer id) {
        log.debug("删除评价，ID: {}", id);
        evaluationService.deleteEvaluation(id);
        return R.success();
    }
    
    /**
     * 根据预约ID查询评价 - GET /api/evaluations/appointment/{appointmentId}
     * 根据预约ID获取评价记录
     */
    @GetMapping("/appointment/{appointmentId}")
    public R<Evaluation> getByAppointmentId(@PathVariable Integer appointmentId) {
        log.debug("根据预约ID查询评价，预约ID: {}", appointmentId);
        Evaluation evaluation = evaluationService.getEvaluationByAppointmentId(appointmentId);
        return R.success(evaluation);
    }
    
    /**
     * 查询学员评价记录 - GET /api/evaluations/student/{studentId}
     * 查询指定学员的所有评价记录
     */
    @GetMapping("/student/{studentId}")
    public R<List<Evaluation>> getStudentEvaluations(@PathVariable Integer studentId) {
        log.debug("查询学员评价记录，学员ID: {}", studentId);
        List<Evaluation> evaluations = evaluationService.getStudentEvaluations(studentId);
        return R.success(evaluations);
    }
    
    /**
     * 查询教练评价记录 - GET /api/evaluations/coach/{coachId}
     * 查询指定教练的所有评价记录
     */
    @GetMapping("/coach/{coachId}")
    public R<List<Evaluation>> getCoachEvaluations(@PathVariable Integer coachId) {
        log.debug("查询教练评价记录，教练ID: {}", coachId);
        List<Evaluation> evaluations = evaluationService.getCoachEvaluations(coachId);
        return R.success(evaluations);
    }
    
    /**
     * 查询待评价记录 - GET /api/evaluations/pending
     * 查询当前用户待评价的课程
     */
    @GetMapping("/pending")
    public R<List<Evaluation>> getPendingEvaluations() {
        log.debug("查询当前用户待评价记录");
        List<Evaluation> evaluations = evaluationService.getPendingEvaluations();
        return R.success(evaluations);
    }
    
    /**
     * 统计评价数量 - GET /api/evaluations/count
     * 统计当前用户的评价数量
     */
    @GetMapping("/count")
    public R<Integer> countEvaluations() {
        log.debug("统计当前用户评价数量");
        // 这里需要从当前用户上下文获取用户信息
        // 暂时返回0，实际实现需要完善
        return R.success(0);
    }
    
    /**
     * 检查是否可以评价 - GET /api/evaluations/can-evaluate
     * 检查指定预约是否可以评价
     */
    @GetMapping("/can-evaluate")
    public R<Boolean> canEvaluate(@RequestParam Integer appointmentId, @RequestParam Integer userRole) {
        log.debug("检查是否可以评价，预约ID: {}, 用户角色: {}", appointmentId, userRole);
        boolean canEvaluate = evaluationService.canEvaluate(appointmentId, userRole);
        return R.success(canEvaluate);
    }
}
