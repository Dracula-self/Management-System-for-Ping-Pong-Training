package com.quan.project.controller;

import com.github.pagehelper.PageInfo;
import com.quan.project.common.R;
import com.quan.project.dto.ApplyCoachDTO;
import com.quan.project.dto.PageDTO;
import com.quan.project.entity.CoachStudentRelation;
import com.quan.project.entity.User;
import com.quan.project.service.CoachStudentRelationService;
import com.quan.project.vo.CoachStudentRelationVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 师生关系管理控制器
 * 基础路径: /api/coach-student-relations
 */
@RestController
@RequestMapping("/api/coach-student-relations")
public class CoachStudentRelationController {
    
    private static final Logger log = LoggerFactory.getLogger(CoachStudentRelationController.class);
    
    @Autowired
    private CoachStudentRelationService relationService;
    
    /**
     * 3.1 学员申请选择教练 - POST /api/coach-student-relations/apply
     * 学员向教练发起选择申请
     */
    @PostMapping("/apply")
    public R<Void> apply(@RequestBody ApplyCoachDTO applyCoachDTO) {
        log.debug("学员申请选择教练，请求参数: {}", applyCoachDTO);
        relationService.applyCoach(applyCoachDTO.getCoachId());
        return R.success();
    }
    
    /**
     * 3.2 教练处理申请 - PUT /api/coach-student-relations/{id}/approve
     * 教练确认或拒绝学员申请
     */
    @PutMapping("/{id}/approve")
    public R<Void> approve(@PathVariable Integer id, @RequestParam Boolean approved) {
        log.debug("教练处理申请，关系ID: {}, 是否通过: {}", id, approved);
        relationService.approveApplication(id, approved);
        return R.success();
    }
    
    /**
     * 3.3 查询师生关系 - POST /api/coach-student-relations/search
     * 查询师生关系列表
     */
    @PostMapping("/search")
    public R<PageInfo<CoachStudentRelationVO>> search(@RequestBody PageDTO pageRequest) {
        log.debug("分页查询师生关系，参数: {}", pageRequest);
        PageInfo<CoachStudentRelationVO> pageInfo = relationService.getRelationPageWithUserInfo(pageRequest);
        return R.success(pageInfo);
    }
    
    /**
     * 3.4 学员更换教练申请 - POST /api/coach-student-relations/change-coach
     * 学员申请更换教练
     */
    @PostMapping("/change-coach")
    public R<Void> changeCoach(@RequestParam Integer currentCoachId, @RequestParam Integer newCoachId) {
        log.debug("学员申请更换教练，当前教练ID: {}, 新教练ID: {}", currentCoachId, newCoachId);
        relationService.changeCoachApplication(currentCoachId, newCoachId);
        return R.success();
    }
    
    /**
     * 获取师生关系详情 - GET /api/coach-student-relations/{id}
     * 根据ID获取师生关系详情
     */
    @GetMapping("/{id}")
    public R<CoachStudentRelation> getById(@PathVariable Integer id) {
        log.debug("查询师生关系详情，ID: {}", id);
        CoachStudentRelation relation = relationService.getRelationById(id);
        return R.success(relation);
    }
    
    /**
     * 查询学员的教练关系 - GET /api/coach-student-relations/student/{studentId}/coaches
     * 查询指定学员的所有教练关系
     */
    @GetMapping("/student/{studentId}/coaches")
    public R<List<CoachStudentRelation>> getStudentCoaches(@PathVariable Integer studentId) {
        log.debug("查询学员教练关系，学员ID: {}", studentId);
        List<CoachStudentRelation> relations = relationService.getStudentCoaches(studentId);
        return R.success(relations);
    }
    
    /**
     * 查询教练的学员关系 - GET /api/coach-student-relations/coach/{coachId}/students
     * 查询指定教练的所有学员关系
     */
    @GetMapping("/coach/{coachId}/students")
    public R<List<CoachStudentRelation>> getCoachStudents(@PathVariable Integer coachId) {
        log.debug("查询教练学员关系，教练ID: {}", coachId);
        List<CoachStudentRelation> relations = relationService.getCoachStudents(coachId);
        return R.success(relations);
    }
    
    /**
     * 解除师生关系 - DELETE /api/coach-student-relations/{id}
     * 解除指定的师生关系
     */
    @DeleteMapping("/{id}")
    public R<Void> terminate(@PathVariable Integer id) {
        log.debug("解除师生关系，ID: {}", id);
        relationService.terminateRelation(id);
        return R.success();
    }
    
    /**
     * 检查教练学员数量 - GET /api/coach-student-relations/coach/{coachId}/check-limit
     * 检查教练是否还能接收新学员
     */
    @GetMapping("/coach/{coachId}/check-limit")
    public R<Boolean> checkCoachLimit(@PathVariable Integer coachId) {
        log.debug("检查教练学员数量限制，教练ID: {}", coachId);
        boolean canAccept = relationService.checkCoachStudentLimit(coachId);
        return R.success(canAccept);
    }
    
    /**
     * 获取可申请的教练列表 - GET /api/coach-student-relations/available-coaches
     * 学员获取可以申请的教练列表（排除已申请的、学员数量已满的）
     */
    @GetMapping("/available-coaches")
    public R<List<User>> getAvailableCoaches() {
        log.debug("获取可申请的教练列表");
        List<User> availableCoaches = relationService.getAvailableCoaches();
        return R.success(availableCoaches);
    }
    
    /**
     * 获取学员的教练列表 - GET /api/coach-student-relations/my-coaches
     * 学员获取自己已确认关系的教练列表（用于预约课程）
     */
    @GetMapping("/my-coaches")
    public R<List<User>> getMyCoaches() {
        log.debug("获取学员的教练列表");
        List<User> myCoaches = relationService.getMyCoaches();
        return R.success(myCoaches);
    }
}
