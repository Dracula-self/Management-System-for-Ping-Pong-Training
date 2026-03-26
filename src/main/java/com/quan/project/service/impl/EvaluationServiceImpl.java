package com.quan.project.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.quan.project.common.CurrentUser;
import com.quan.project.dto.PageDTO;
import com.quan.project.entity.Appointment;
import com.quan.project.entity.Evaluation;
import com.quan.project.entity.User;
import com.quan.project.exception.BusinessException;
import com.quan.project.mapper.AppointmentMapper;
import com.quan.project.mapper.EvaluationMapper;
import com.quan.project.mapper.UserMapper;
import com.quan.project.service.EvaluationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 训练评价服务实现类
 */
@Service
public class EvaluationServiceImpl implements EvaluationService {
    
    private static final Logger log = LoggerFactory.getLogger(EvaluationServiceImpl.class);
    
    @Autowired
    private EvaluationMapper evaluationMapper;
    
    @Autowired
    private AppointmentMapper appointmentMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    @Transactional
    public Evaluation submitEvaluation(Evaluation evaluation) {
        try {
            // 验证预约存在
            Appointment appointment = appointmentMapper.selectById(evaluation.getAppointmentId());
            if (appointment == null) {
                throw new BusinessException("预约不存在");
            }
            
            // 验证预约已完成
            if (appointment.getStatus() != Appointment.STATUS_COMPLETED) {
                throw new BusinessException("只能评价已完成的课程");
            }
            
            // 权限检查
            Integer currentUserId = CurrentUser.getCurrentUserId();
            Integer currentUserRole = CurrentUser.getCurrentUserRole();
            
            if (!appointment.getStudentId().equals(currentUserId) && !appointment.getCoachId().equals(currentUserId)) {
                throw new BusinessException("只能评价自己参与的课程");
            }
            
            // 检查是否已有评价记录
            Evaluation existingEvaluation = evaluationMapper.selectByAppointmentId(evaluation.getAppointmentId());
            
            if (existingEvaluation != null) {
                // 更新现有评价
                if (currentUserRole == User.ROLE_STUDENT) {
                    existingEvaluation.setStudentFeedback(evaluation.getStudentFeedback());
                } else if (currentUserRole == User.ROLE_COACH) {
                    existingEvaluation.setCoachFeedback(evaluation.getCoachFeedback());
                }
                
                evaluationMapper.update(existingEvaluation);
                log.info("更新评价成功，评价ID: {}, 用户ID: {}", existingEvaluation.getId(), currentUserId);
                return existingEvaluation;
            } else {
                // 创建新评价记录
                Evaluation newEvaluation = new Evaluation();
                newEvaluation.setAppointmentId(evaluation.getAppointmentId());
                
                if (currentUserRole == User.ROLE_STUDENT) {
                    newEvaluation.setStudentFeedback(evaluation.getStudentFeedback());
                } else if (currentUserRole == User.ROLE_COACH) {
                    newEvaluation.setCoachFeedback(evaluation.getCoachFeedback());
                }
                
                int result = evaluationMapper.insert(newEvaluation);
                if (result <= 0) {
                    throw new BusinessException("提交评价失败");
                }
                
                log.info("提交评价成功，评价ID: {}, 预约ID: {}, 用户ID: {}", 
                    newEvaluation.getId(), evaluation.getAppointmentId(), currentUserId);
                return newEvaluation;
            }
            
        } catch (Exception e) {
            log.error("提交评价失败，预约ID: {}", evaluation.getAppointmentId(), e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("提交评价失败");
        }
    }
    
    @Override
    @Transactional
    public Evaluation submitStudentEvaluation(Integer appointmentId, String studentFeedback) {
        try {
            Integer currentUserId = CurrentUser.getCurrentUserId();
            
            // 验证用户是学员
            User user = userMapper.selectById(currentUserId);
            if (user.getUserRole() != User.ROLE_STUDENT) {
                throw new BusinessException("只有学员可以提交学员评价");
            }
            
            Evaluation evaluation = new Evaluation();
            evaluation.setAppointmentId(appointmentId);
            evaluation.setStudentFeedback(studentFeedback);
            
            return submitEvaluation(evaluation);
            
        } catch (Exception e) {
            log.error("提交学员评价失败，预约ID: {}", appointmentId, e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("提交学员评价失败");
        }
    }
    
    @Override
    @Transactional
    public Evaluation submitCoachEvaluation(Integer appointmentId, String coachFeedback) {
        try {
            Integer currentUserId = CurrentUser.getCurrentUserId();
            
            // 验证用户是教练
            User user = userMapper.selectById(currentUserId);
            if (user.getUserRole() != User.ROLE_COACH) {
                throw new BusinessException("只有教练可以提交教练评价");
            }
            
            Evaluation evaluation = new Evaluation();
            evaluation.setAppointmentId(appointmentId);
            evaluation.setCoachFeedback(coachFeedback);
            
            return submitEvaluation(evaluation);
            
        } catch (Exception e) {
            log.error("提交教练评价失败，预约ID: {}", appointmentId, e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("提交教练评价失败");
        }
    }
    
    @Override
    @Transactional
    public void updateEvaluation(Evaluation evaluation) {
        try {
            // 验证评价存在
            Evaluation existingEvaluation = evaluationMapper.selectById(evaluation.getId());
            if (existingEvaluation == null) {
                throw new BusinessException("评价不存在");
            }
            
            // 权限检查
            Appointment appointment = appointmentMapper.selectById(existingEvaluation.getAppointmentId());
            Integer currentUserId = CurrentUser.getCurrentUserId();
            Integer currentUserRole = CurrentUser.getCurrentUserRole();
            
            if (!appointment.getStudentId().equals(currentUserId) && !appointment.getCoachId().equals(currentUserId)) {
                throw new BusinessException("只能修改自己的评价");
            }
            
            // 只允许修改对应角色的评价内容
            if (currentUserRole == User.ROLE_STUDENT && evaluation.getStudentFeedback() != null) {
                existingEvaluation.setStudentFeedback(evaluation.getStudentFeedback());
            } else if (currentUserRole == User.ROLE_COACH && evaluation.getCoachFeedback() != null) {
                existingEvaluation.setCoachFeedback(evaluation.getCoachFeedback());
            } else {
                throw new BusinessException("只能修改自己角色对应的评价内容");
            }
            
            int result = evaluationMapper.update(existingEvaluation);
            if (result <= 0) {
                throw new BusinessException("更新评价失败");
            }
            
            log.info("更新评价成功，评价ID: {}, 用户ID: {}", evaluation.getId(), currentUserId);
            
        } catch (Exception e) {
            log.error("更新评价失败，评价ID: {}", evaluation.getId(), e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("更新评价失败");
        }
    }
    
    @Override
    @Transactional
    public void deleteEvaluation(Integer id) {
        try {
            // 验证评价存在
            Evaluation evaluation = evaluationMapper.selectById(id);
            if (evaluation == null) {
                throw new BusinessException("评价不存在");
            }
            
            // 权限检查：只有管理员可以删除评价
            Integer currentUserId = CurrentUser.getCurrentUserId();
            User currentUser = userMapper.selectById(currentUserId);
            
            if (currentUser.getUserRole() != User.ROLE_SUPER_ADMIN && 
                currentUser.getUserRole() != User.ROLE_CAMPUS_ADMIN) {
                throw new BusinessException("只有管理员可以删除评价");
            }
            
            int result = evaluationMapper.deleteById(id);
            if (result <= 0) {
                throw new BusinessException("删除评价失败");
            }
            
            log.info("删除评价成功，评价ID: {}, 操作者: {}", id, currentUserId);
            
        } catch (Exception e) {
            log.error("删除评价失败，评价ID: {}", id, e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("删除评价失败");
        }
    }
    
    @Override
    public Evaluation getEvaluationById(Integer id) {
        try {
            Evaluation evaluation = evaluationMapper.selectById(id);
            if (evaluation == null) {
                throw new BusinessException("评价不存在");
            }
            
            // 权限检查
            Appointment appointment = appointmentMapper.selectById(evaluation.getAppointmentId());
            Integer currentUserId = CurrentUser.getCurrentUserId();
            
            if (!appointment.getStudentId().equals(currentUserId) && !appointment.getCoachId().equals(currentUserId)) {
                User currentUser = userMapper.selectById(currentUserId);
                if (currentUser.getUserRole() != User.ROLE_SUPER_ADMIN && 
                    currentUser.getUserRole() != User.ROLE_CAMPUS_ADMIN) {
                    throw new BusinessException("只能查看自己参与课程的评价");
                }
            }
            
            return evaluation;
            
        } catch (Exception e) {
            log.error("查询评价失败，评价ID: {}", id, e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("查询评价失败");
        }
    }
    
    @Override
    public PageInfo<Evaluation> getEvaluationPage(PageDTO pageRequest) {
        try {
            // 非管理员只能查看自己相关的评价
            Integer currentUserId = CurrentUser.getCurrentUserId();
            User currentUser = userMapper.selectById(currentUserId);
            
            if (currentUser.getUserRole() == User.ROLE_STUDENT) {
                pageRequest.addParam("studentId", currentUserId);
            } else if (currentUser.getUserRole() == User.ROLE_COACH) {
                pageRequest.addParam("coachId", currentUserId);
            }
            
            PageHelper.startPage(pageRequest.getPageNum(), pageRequest.getPageSize());
            List<Evaluation> evaluations = evaluationMapper.selectPageByConditions(pageRequest);
            return new PageInfo<>(evaluations);
            
        } catch (Exception e) {
            log.error("分页查询评价失败", e);
            throw new BusinessException("查询评价列表失败");
        }
    }
    
    @Override
    public Evaluation getEvaluationByAppointmentId(Integer appointmentId) {
        try {
            return evaluationMapper.selectByAppointmentId(appointmentId);
        } catch (Exception e) {
            log.error("根据预约ID查询评价失败，预约ID: {}", appointmentId, e);
            throw new BusinessException("查询评价失败");
        }
    }
    
    @Override
    public List<Evaluation> getStudentEvaluations(Integer studentId) {
        try {
            // 权限检查
            Integer currentUserId = CurrentUser.getCurrentUserId();
            if (!studentId.equals(currentUserId)) {
                User currentUser = userMapper.selectById(currentUserId);
                if (currentUser.getUserRole() != User.ROLE_SUPER_ADMIN && 
                    currentUser.getUserRole() != User.ROLE_CAMPUS_ADMIN) {
                    throw new BusinessException("只能查看自己的评价记录");
                }
            }
            
            return evaluationMapper.selectByStudentId(studentId);
        } catch (Exception e) {
            log.error("查询学员评价记录失败，学员ID: {}", studentId, e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("查询学员评价记录失败");
        }
    }
    
    @Override
    public List<Evaluation> getCoachEvaluations(Integer coachId) {
        try {
            // 权限检查
            Integer currentUserId = CurrentUser.getCurrentUserId();
            if (!coachId.equals(currentUserId)) {
                User currentUser = userMapper.selectById(currentUserId);
                if (currentUser.getUserRole() != User.ROLE_SUPER_ADMIN && 
                    currentUser.getUserRole() != User.ROLE_CAMPUS_ADMIN) {
                    throw new BusinessException("只能查看自己的评价记录");
                }
            }
            
            return evaluationMapper.selectByCoachId(coachId);
        } catch (Exception e) {
            log.error("查询教练评价记录失败，教练ID: {}", coachId, e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("查询教练评价记录失败");
        }
    }
    
    @Override
    public List<Evaluation> getPendingEvaluationsForStudent(Integer studentId) {
        try {
            return evaluationMapper.selectPendingEvaluationsForStudent(studentId);
        } catch (Exception e) {
            log.error("查询学员待评价记录失败，学员ID: {}", studentId, e);
            throw new BusinessException("查询待评价记录失败");
        }
    }
    
    @Override
    public List<Evaluation> getPendingEvaluationsForCoach(Integer coachId) {
        try {
            return evaluationMapper.selectPendingEvaluationsForCoach(coachId);
        } catch (Exception e) {
            log.error("查询教练待评价记录失败，教练ID: {}", coachId, e);
            throw new BusinessException("查询待评价记录失败");
        }
    }
    
    @Override
    public List<Evaluation> getPendingEvaluations() {
        try {
            Integer currentUserId = CurrentUser.getCurrentUserId();
            Integer currentUserRole = CurrentUser.getCurrentUserRole();
            
            if (currentUserRole == User.ROLE_STUDENT) {
                return getPendingEvaluationsForStudent(currentUserId);
            } else if (currentUserRole == User.ROLE_COACH) {
                return getPendingEvaluationsForCoach(currentUserId);
            } else {
                throw new BusinessException("只有学员和教练可以查看待评价记录");
            }
            
        } catch (Exception e) {
            log.error("查询当前用户待评价记录失败", e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("查询待评价记录失败");
        }
    }
    
    @Override
    public int countUserEvaluations(Integer userId, Integer userRole) {
        try {
            return evaluationMapper.countEvaluationsByUser(userId, userRole);
        } catch (Exception e) {
            log.error("统计用户评价数量失败，用户ID: {}, 角色: {}", userId, userRole, e);
            return 0;
        }
    }
    
    @Override
    public boolean canEvaluate(Integer appointmentId, Integer userRole) {
        try {
            // 验证预约存在且已完成
            Appointment appointment = appointmentMapper.selectById(appointmentId);
            if (appointment == null || appointment.getStatus() != Appointment.STATUS_COMPLETED) {
                return false;
            }
            
            // 验证用户权限
            Integer currentUserId = CurrentUser.getCurrentUserId();
            if (!appointment.getStudentId().equals(currentUserId) && !appointment.getCoachId().equals(currentUserId)) {
                return false;
            }
            
            // 检查是否已评价
            Evaluation evaluation = evaluationMapper.selectByAppointmentId(appointmentId);
            if (evaluation == null) {
                return true; // 没有评价记录，可以评价
            }
            
            // 检查对应角色是否已评价
            if (userRole == User.ROLE_STUDENT) {
                return evaluation.getStudentFeedback() == null || evaluation.getStudentFeedback().trim().isEmpty();
            } else if (userRole == User.ROLE_COACH) {
                return evaluation.getCoachFeedback() == null || evaluation.getCoachFeedback().trim().isEmpty();
            }
            
            return false;
        } catch (Exception e) {
            log.error("检查是否可以评价失败，预约ID: {}", appointmentId, e);
            return false;
        }
    }
}
