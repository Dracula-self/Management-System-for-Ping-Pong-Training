package com.quan.project.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.quan.project.common.CurrentUser;
import com.quan.project.dto.PageDTO;
import com.quan.project.entity.CoachStudentRelation;
import com.quan.project.entity.User;
import com.quan.project.exception.BusinessException;
import com.quan.project.mapper.CoachStudentRelationMapper;
import com.quan.project.mapper.UserMapper;
import com.quan.project.service.CoachStudentRelationService;
import com.quan.project.vo.CoachStudentRelationVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 师生关系服务实现类
 */
@Service
public class CoachStudentRelationServiceImpl implements CoachStudentRelationService {
    
    private static final Logger log = LoggerFactory.getLogger(CoachStudentRelationServiceImpl.class);
    
    @Autowired
    private CoachStudentRelationMapper relationMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    @Transactional
    public void applyCoach(Integer coachId) {
        try {
            Integer studentId = CurrentUser.getCurrentUserId();
            
            // 验证教练存在且为教练角色
            User coach = userMapper.selectById(coachId);
            if (coach == null || coach.getUserRole() != User.ROLE_COACH) {
                throw new BusinessException("教练不存在或角色不正确");
            }
            
            // 检查是否已存在关系
            if (checkRelationExists(coachId, studentId)) {
                throw new BusinessException("已存在师生关系，请勿重复申请");
            }
            
            // 检查学员教练数量限制（最多2个）
            if (!checkStudentCoachLimit(studentId)) {
                throw new BusinessException("您最多只能选择2位教练");
            }
            
            // 检查教练学员数量限制（最多20个）
            if (!checkCoachStudentLimit(coachId)) {
                throw new BusinessException("该教练学员已满，无法申请");
            }
            
            // 创建申请记录
            CoachStudentRelation relation = new CoachStudentRelation();
            relation.setCoachId(coachId);
            relation.setStudentId(studentId);
            relation.setStatus(CoachStudentRelation.STATUS_PENDING);
            
            int result = relationMapper.insert(relation);
            if (result <= 0) {
                throw new BusinessException("申请失败");
            }
            
            // TODO: 发送系统消息通知教练
            
            log.info("学员申请教练成功，学员ID: {}, 教练ID: {}, 关系ID: {}", 
                studentId, coachId, relation.getId());
                
        } catch (Exception e) {
            log.error("学员申请教练失败，教练ID: {}", coachId, e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("申请教练失败");
        }
    }
    
    @Override
    @Transactional
    public void approveApplication(Integer id, Boolean approved) {
        try {
            Integer coachId = CurrentUser.getCurrentUserId();
            CoachStudentRelation relation = relationMapper.selectById(id);
            
            if (relation == null) {
                throw new BusinessException("申请记录不存在");
            }
            
            if (!relation.getCoachId().equals(coachId)) {
                throw new BusinessException("只能处理自己的申请");
            }
            
            if (relation.getStatus() != CoachStudentRelation.STATUS_PENDING) {
                throw new BusinessException("申请状态不正确");
            }
            
            if (approved) {
                // 再次检查限制
                if (!checkCoachStudentLimit(coachId) || !checkStudentCoachLimit(relation.getStudentId())) {
                    throw new BusinessException("人数限制已达上限，无法通过申请");
                }
                
                relationMapper.updateStatus(id, CoachStudentRelation.STATUS_CONFIRMED);
                log.info("教练确认师生关系，关系ID: {}, 教练ID: {}", id, coachId);
            } else {
                relationMapper.updateStatus(id, CoachStudentRelation.STATUS_TERMINATED);
                log.info("教练拒绝师生关系，关系ID: {}, 教练ID: {}", id, coachId);
            }
            
            // TODO: 发送系统消息通知学员
            
        } catch (Exception e) {
            log.error("处理师生关系申请失败，关系ID: {}", id, e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("处理申请失败");
        }
    }
    
    @Override
    public PageInfo<CoachStudentRelation> getRelationPage(PageDTO pageRequest) {
        try {
            PageHelper.startPage(pageRequest.getPageNum(), pageRequest.getPageSize());
            List<CoachStudentRelation> relations = relationMapper.selectPageByConditions(pageRequest);
            return new PageInfo<>(relations);
        } catch (Exception e) {
            log.error("分页查询师生关系失败", e);
            throw new BusinessException("分页查询师生关系失败");
        }
    }
    
    @Override
    public PageInfo<CoachStudentRelationVO> getRelationPageWithUserInfo(PageDTO pageRequest) {
        try {
            // 根据当前用户角色自动过滤数据
            Integer currentUserId = CurrentUser.getCurrentUserId();
            Integer currentUserRole = CurrentUser.getCurrentUserRole();
            
            // 自动根据角色过滤查询条件
            if (currentUserRole == User.ROLE_COACH) {
                // 教练只能看到自己相关的师生关系
                pageRequest.addParam("coachId", currentUserId);
            } else if (currentUserRole == User.ROLE_STUDENT) {
                // 学员只能看到自己相关的师生关系
                pageRequest.addParam("studentId", currentUserId);
            } else if (currentUserRole == User.ROLE_CAMPUS_ADMIN) {
                // 校区管理员只能看到本校区的师生关系
                User currentUser = userMapper.selectById(currentUserId);
                if (currentUser != null && currentUser.getCampusId() != null) {
                    pageRequest.addParam("campusId", currentUser.getCampusId());
                }
            }
            // 超级管理员可以查看所有数据，不需要额外过滤
            
            PageHelper.startPage(pageRequest.getPageNum(), pageRequest.getPageSize());
            List<CoachStudentRelationVO> relations = relationMapper.selectPageWithUserInfo(pageRequest);
            return new PageInfo<>(relations);
        } catch (Exception e) {
            log.error("分页查询师生关系（含用户信息）失败", e);
            throw new BusinessException("分页查询师生关系失败");
        }
    }
    
    @Override
    public CoachStudentRelation getRelationById(Integer id) {
        try {
            CoachStudentRelation relation = relationMapper.selectById(id);
            if (relation == null) {
                throw new BusinessException("师生关系不存在");
            }
            return relation;
        } catch (Exception e) {
            log.error("查询师生关系失败，ID: {}", id, e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("查询师生关系失败");
        }
    }
    
    @Override
    public List<CoachStudentRelation> getStudentCoaches(Integer studentId) {
        try {
            return relationMapper.selectConfirmedByStudentId(studentId);
        } catch (Exception e) {
            log.error("查询学员教练关系失败，学员ID: {}", studentId, e);
            throw new BusinessException("查询学员教练关系失败");
        }
    }
    
    @Override
    public List<CoachStudentRelation> getCoachStudents(Integer coachId) {
        try {
            return relationMapper.selectConfirmedByCoachId(coachId);
        } catch (Exception e) {
            log.error("查询教练学员关系失败，教练ID: {}", coachId, e);
            throw new BusinessException("查询教练学员关系失败");
        }
    }
    
    @Override
    @Transactional
    public void changeCoachApplication(Integer currentCoachId, Integer newCoachId) {
        try {
            Integer studentId = CurrentUser.getCurrentUserId();
            
            // 验证当前师生关系存在
            CoachStudentRelation currentRelation = relationMapper.selectByCoachAndStudent(currentCoachId, studentId);
            if (currentRelation == null || currentRelation.getStatus() != CoachStudentRelation.STATUS_CONFIRMED) {
                throw new BusinessException("当前师生关系不存在或状态不正确");
            }
            
            // 验证新教练存在
            User newCoach = userMapper.selectById(newCoachId);
            if (newCoach == null || newCoach.getUserRole() != User.ROLE_COACH) {
                throw new BusinessException("新教练不存在或角色不正确");
            }
            
            // 检查新教练学员数量限制
            if (!checkCoachStudentLimit(newCoachId)) {
                throw new BusinessException("新教练学员已满，无法更换");
            }
            
            // TODO: 发送更换教练申请消息给当前教练、新教练和校区管理员
            // 这里需要三方都同意才能更换，实际实现需要更复杂的流程控制
            
            log.info("学员申请更换教练，学员ID: {}, 当前教练ID: {}, 新教练ID: {}", 
                studentId, currentCoachId, newCoachId);
                
        } catch (Exception e) {
            log.error("申请更换教练失败，当前教练ID: {}, 新教练ID: {}", currentCoachId, newCoachId, e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("申请更换教练失败");
        }
    }
    
    @Override
    @Transactional
    public void terminateRelation(Integer id) {
        try {
            CoachStudentRelation relation = relationMapper.selectById(id);
            if (relation == null) {
                throw new BusinessException("师生关系不存在");
            }
            
            relationMapper.updateStatus(id, CoachStudentRelation.STATUS_TERMINATED);
            log.info("解除师生关系，关系ID: {}", id);
            
        } catch (Exception e) {
            log.error("解除师生关系失败，关系ID: {}", id, e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("解除师生关系失败");
        }
    }
    
    @Override
    public boolean checkRelationExists(Integer coachId, Integer studentId) {
        try {
            CoachStudentRelation relation = relationMapper.selectByCoachAndStudent(coachId, studentId);
            return relation != null && relation.getStatus() != CoachStudentRelation.STATUS_TERMINATED;
        } catch (Exception e) {
            log.error("检查师生关系失败", e);
            return true; // 出错时保守处理
        }
    }
    
    @Override
    public boolean checkCoachStudentLimit(Integer coachId) {
        try {
            int studentCount = relationMapper.countStudentsByCoachId(coachId);
            return studentCount < 20; // 最多20个学员
        } catch (Exception e) {
            log.error("检查教练学员数量限制失败", e);
            return false; // 出错时保守处理
        }
    }
    
    @Override
    public boolean checkStudentCoachLimit(Integer studentId) {
        try {
            int coachCount = relationMapper.countCoachesByStudentId(studentId);
            return coachCount < 2; // 最多2个教练
        } catch (Exception e) {
            log.error("检查学员教练数量限制失败", e);
            return false; // 出错时保守处理
        }
    }
    
    @Override
    public List<User> getAvailableCoaches() {
        try {
            Integer studentId = CurrentUser.getCurrentUserId();
            Integer currentUserRole = CurrentUser.getCurrentUserRole();
            
            // 只有学员才能获取可申请的教练列表
            if (currentUserRole != User.ROLE_STUDENT) {
                throw new BusinessException("只有学员才能申请教练");
            }
            
            // 获取当前学员的校区ID
            User currentUser = userMapper.selectById(studentId);
            if (currentUser == null || currentUser.getCampusId() == null) {
                throw new BusinessException("学员信息异常或未关联校区");
            }
            
            // 获取同校区的所有教练
            List<User> allCoaches = userMapper.selectByRoleAndCampus(User.ROLE_COACH, currentUser.getCampusId());
            
            // 过滤条件：
            // 1. 排除已经申请过的教练（包括待确认、已确认状态）
            // 2. 排除学员数量已满的教练
            List<User> availableCoaches = allCoaches.stream()
                .filter(coach -> {
                    // 检查是否已有师生关系（排除已解约的）
                    CoachStudentRelation existingRelation = relationMapper.selectByCoachAndStudent(coach.getId(), studentId);
                    boolean hasActiveRelation = existingRelation != null && 
                        existingRelation.getStatus() != CoachStudentRelation.STATUS_TERMINATED;
                    
                    // 检查教练学员数量限制
                    boolean coachNotFull = checkCoachStudentLimit(coach.getId());
                    
                    return !hasActiveRelation && coachNotFull;
                })
                .collect(java.util.stream.Collectors.toList());
            
            log.debug("获取可申请教练列表成功，学员ID: {}, 校区ID: {}, 可选教练数量: {}", 
                studentId, currentUser.getCampusId(), availableCoaches.size());
            
            return availableCoaches;
            
        } catch (Exception e) {
            log.error("获取可申请教练列表失败", e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("获取可申请教练列表失败");
        }
    }
    
    @Override
    public List<User> getMyCoaches() {
        try {
            Integer studentId = CurrentUser.getCurrentUserId();
            Integer currentUserRole = CurrentUser.getCurrentUserRole();
            
            // 只有学员才能获取自己的教练列表
            if (currentUserRole != User.ROLE_STUDENT) {
                throw new BusinessException("只有学员才能查看自己的教练");
            }
            
            // 获取已确认关系的教练关系
            List<CoachStudentRelation> confirmedRelations = relationMapper.selectConfirmedByStudentId(studentId);
            
            if (confirmedRelations.isEmpty()) {
                return new ArrayList<>();
            }
            
            // 获取教练详细信息
            List<User> myCoaches = new ArrayList<>();
            for (CoachStudentRelation relation : confirmedRelations) {
                User coach = userMapper.selectById(relation.getCoachId());
                if (coach != null && coach.getUserStatus() == 1) { // 只返回正常状态的教练
                    myCoaches.add(coach);
                }
            }
            
            log.debug("获取学员教练列表成功，学员ID: {}, 教练数量: {}", studentId, myCoaches.size());
            
            return myCoaches;
            
        } catch (Exception e) {
            log.error("获取学员教练列表失败", e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("获取教练列表失败");
        }
    }
}
