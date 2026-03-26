package com.quan.project.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.quan.project.common.CurrentUser;
import com.quan.project.dto.CompetitionRegistrationDTO;
import com.quan.project.dto.PageDTO;
import com.quan.project.entity.Competition;
import com.quan.project.entity.CompetitionParticipant;
import com.quan.project.entity.User;
import com.quan.project.exception.BusinessException;
import com.quan.project.mapper.CompetitionMapper;
import com.quan.project.mapper.CompetitionParticipantMapper;
import com.quan.project.mapper.UserMapper;
import com.quan.project.service.CompetitionService;
import com.quan.project.service.TransactionService;
import com.quan.project.vo.CompetitionParticipantVO;
import com.quan.project.vo.CompetitionVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * 比赛服务实现类
 */
@Service
public class CompetitionServiceImpl implements CompetitionService {
    
    private static final Logger log = LoggerFactory.getLogger(CompetitionServiceImpl.class);
    
    @Autowired
    private CompetitionMapper competitionMapper;
    
    @Autowired
    private CompetitionParticipantMapper competitionParticipantMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private TransactionService transactionService;
    
    @Override
    @Transactional
    public Competition createCompetition(Competition competition) {
        try {
            // 验证管理员权限
            Integer currentUserId = CurrentUser.getCurrentUserId();
            User currentUser = userMapper.selectById(currentUserId);
            if (currentUser.getUserRole() != User.ROLE_SUPER_ADMIN && 
                currentUser.getUserRole() != User.ROLE_CAMPUS_ADMIN) {
                throw new BusinessException("只有管理员可以创建比赛");
            }
            
            // 验证比赛信息
            if (competition.getName() == null || competition.getName().trim().isEmpty()) {
                throw new BusinessException("比赛名称不能为空");
            }
            
            if (competition.getCompetitionDate() == null) {
                throw new BusinessException("比赛日期不能为空");
            }
            
            if (competition.getCompetitionDate().isBefore(LocalDate.now())) {
                throw new BusinessException("比赛日期不能早于当前日期");
            }
            
            // 设置初始状态为报名中
            if (competition.getStatus() == null) {
                competition.setStatus(Competition.STATUS_REGISTRATION);
            }
            
            int result = competitionMapper.insert(competition);
            if (result <= 0) {
                throw new BusinessException("创建比赛失败");
            }
            
            log.info("创建比赛成功，比赛ID: {}, 名称: {}, 创建者: {}", 
                competition.getId(), competition.getName(), currentUserId);
            return competition;
            
        } catch (Exception e) {
            log.error("创建比赛失败", e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("创建比赛失败");
        }
    }
    
    @Override
    @Transactional
    public void deleteCompetition(Integer id) {
        try {
            // 验证管理员权限
            Integer currentUserId = CurrentUser.getCurrentUserId();
            User currentUser = userMapper.selectById(currentUserId);
            if (currentUser.getUserRole() != User.ROLE_SUPER_ADMIN && 
                currentUser.getUserRole() != User.ROLE_CAMPUS_ADMIN) {
                throw new BusinessException("只有管理员可以删除比赛");
            }
            
            // 检查比赛是否存在
            Competition competition = competitionMapper.selectById(id);
            if (competition == null) {
                throw new BusinessException("比赛不存在");
            }
            
            // 检查比赛状态，只能删除未开始的比赛
            if (competition.getStatus() == Competition.STATUS_IN_PROGRESS || 
                competition.getStatus() == Competition.STATUS_COMPLETED) {
                throw new BusinessException("进行中或已完成的比赛不能删除");
            }
            
            // TODO: 检查是否有学员已报名，有报名的比赛需要先处理退费
            
            int result = competitionMapper.deleteById(id);
            if (result <= 0) {
                throw new BusinessException("删除比赛失败");
            }
            
            log.info("删除比赛成功，比赛ID: {}, 操作者: {}", id, currentUserId);
            
        } catch (Exception e) {
            log.error("删除比赛失败，比赛ID: {}", id, e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("删除比赛失败");
        }
    }
    
    @Override
    @Transactional
    public void updateCompetition(Competition competition) {
        try {
            // 验证管理员权限
            Integer currentUserId = CurrentUser.getCurrentUserId();
            User currentUser = userMapper.selectById(currentUserId);
            if (currentUser.getUserRole() != User.ROLE_SUPER_ADMIN && 
                currentUser.getUserRole() != User.ROLE_CAMPUS_ADMIN) {
                throw new BusinessException("只有管理员可以修改比赛");
            }
            
            // 检查比赛是否存在
            Competition existingCompetition = competitionMapper.selectById(competition.getId());
            if (existingCompetition == null) {
                throw new BusinessException("比赛不存在");
            }
            
            // 验证比赛信息
            if (competition.getName() != null && competition.getName().trim().isEmpty()) {
                throw new BusinessException("比赛名称不能为空");
            }
            
            if (competition.getCompetitionDate() != null && 
                competition.getCompetitionDate().isBefore(LocalDate.now())) {
                throw new BusinessException("比赛日期不能早于当前日期");
            }
            
            int result = competitionMapper.update(competition);
            if (result <= 0) {
                throw new BusinessException("更新比赛失败");
            }
            
            log.info("更新比赛成功，比赛ID: {}, 操作者: {}", competition.getId(), currentUserId);
            
        } catch (Exception e) {
            log.error("更新比赛失败，比赛ID: {}", competition.getId(), e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("更新比赛失败");
        }
    }
    
    @Override
    @Transactional
    public void updateCompetitionStatus(Integer id, Integer status) {
        try {
            // 验证管理员权限
            Integer currentUserId = CurrentUser.getCurrentUserId();
            User currentUser = userMapper.selectById(currentUserId);
            if (currentUser.getUserRole() != User.ROLE_SUPER_ADMIN && 
                currentUser.getUserRole() != User.ROLE_CAMPUS_ADMIN) {
                throw new BusinessException("只有管理员可以修改比赛状态");
            }
            
            // 检查比赛是否存在
            Competition competition = competitionMapper.selectById(id);
            if (competition == null) {
                throw new BusinessException("比赛不存在");
            }
            
            // 验证状态转换的合法性
            validateStatusTransition(competition.getStatus(), status);
            
            int result = competitionMapper.updateStatus(id, status);
            if (result <= 0) {
                throw new BusinessException("更新比赛状态失败");
            }
            
            log.info("更新比赛状态成功，比赛ID: {}, 原状态: {}, 新状态: {}, 操作者: {}", 
                id, competition.getStatus(), status, currentUserId);
            
        } catch (Exception e) {
            log.error("更新比赛状态失败，比赛ID: {}, 状态: {}", id, status, e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("更新比赛状态失败");
        }
    }
    
    @Override
    public Competition getCompetitionById(Integer id) {
        try {
            Competition competition = competitionMapper.selectById(id);
            if (competition == null) {
                throw new BusinessException("比赛不存在");
            }
            return competition;
        } catch (Exception e) {
            log.error("查询比赛失败，比赛ID: {}", id, e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("查询比赛失败");
        }
    }
    
    @Override
    public PageInfo<CompetitionVO> getCompetitionPage(PageDTO pageRequest) {
        try {
            PageHelper.startPage(pageRequest.getPageNum(), pageRequest.getPageSize());
            List<CompetitionVO> competitions = competitionMapper.selectPageByConditions(pageRequest);
            return new PageInfo<>(competitions);
        } catch (Exception e) {
            log.error("分页查询比赛失败", e);
            throw new BusinessException("查询比赛列表失败");
        }
    }
    
    @Override
    public List<Competition> getAllCompetitions() {
        try {
            return competitionMapper.selectAll();
        } catch (Exception e) {
            log.error("查询所有比赛失败", e);
            throw new BusinessException("查询比赛列表失败");
        }
    }
    
    @Override
    public List<Competition> getAvailableCompetitions() {
        try {
            return competitionMapper.selectAvailableCompetitions();
        } catch (Exception e) {
            log.error("查询可报名比赛失败", e);
            throw new BusinessException("查询可报名比赛失败");
        }
    }
    
    @Override
    public List<Competition> getInProgressCompetitions() {
        try {
            return competitionMapper.selectInProgressCompetitions();
        } catch (Exception e) {
            log.error("查询进行中比赛失败", e);
            throw new BusinessException("查询进行中比赛失败");
        }
    }
    
    @Override
    @Transactional
    public void startRegistration(Integer competitionId) {
        try {
            updateCompetitionStatus(competitionId, Competition.STATUS_REGISTRATION);
            log.info("开始比赛报名，比赛ID: {}", competitionId);
        } catch (Exception e) {
            log.error("开始比赛报名失败，比赛ID: {}", competitionId, e);
            throw new BusinessException("开始比赛报名失败");
        }
    }
    
    @Override
    @Transactional
    public void endRegistration(Integer competitionId) {
        try {
            updateCompetitionStatus(competitionId, Competition.STATUS_REGISTRATION_CLOSED);
            log.info("结束比赛报名，比赛ID: {}", competitionId);
        } catch (Exception e) {
            log.error("结束比赛报名失败，比赛ID: {}", competitionId, e);
            throw new BusinessException("结束比赛报名失败");
        }
    }
    
    @Override
    @Transactional
    public void startCompetition(Integer competitionId) {
        try {
            updateCompetitionStatus(competitionId, Competition.STATUS_IN_PROGRESS);
            log.info("开始比赛，比赛ID: {}", competitionId);
        } catch (Exception e) {
            log.error("开始比赛失败，比赛ID: {}", competitionId, e);
            throw new BusinessException("开始比赛失败");
        }
    }
    
    @Override
    @Transactional
    public void endCompetition(Integer competitionId) {
        try {
            updateCompetitionStatus(competitionId, Competition.STATUS_COMPLETED);
            log.info("结束比赛，比赛ID: {}", competitionId);
        } catch (Exception e) {
            log.error("结束比赛失败，比赛ID: {}", competitionId, e);
            throw new BusinessException("结束比赛失败");
        }
    }
    
    /**
     * 验证状态转换的合法性
     */
    private void validateStatusTransition(Integer currentStatus, Integer newStatus) {
        if (currentStatus.equals(newStatus)) {
            throw new BusinessException("比赛状态未发生变化");
        }
        
        // 定义合法的状态转换
        switch (currentStatus) {
            case Competition.STATUS_REGISTRATION:
                if (newStatus != Competition.STATUS_REGISTRATION_CLOSED) {
                    throw new BusinessException("报名中的比赛只能转换为报名结束状态");
                }
                break;
            case Competition.STATUS_REGISTRATION_CLOSED:
                if (newStatus != Competition.STATUS_IN_PROGRESS && newStatus != Competition.STATUS_REGISTRATION) {
                    throw new BusinessException("报名结束的比赛只能转换为进行中或重新开放报名");
                }
                break;
            case Competition.STATUS_IN_PROGRESS:
                if (newStatus != Competition.STATUS_COMPLETED) {
                    throw new BusinessException("进行中的比赛只能转换为已完成状态");
                }
                break;
            case Competition.STATUS_COMPLETED:
                throw new BusinessException("已完成的比赛不能修改状态");
            default:
                throw new BusinessException("无效的比赛状态");
        }
    }
    
    @Override
    public List<CompetitionParticipantVO> getParticipantsByCompetitionId(Integer competitionId) {
        try {
            log.debug("查询比赛参赛者列表，比赛ID: {}", competitionId);
            
            // 验证比赛是否存在
            Competition competition = competitionMapper.selectById(competitionId);
            if (competition == null) {
                throw new BusinessException("比赛不存在");
            }
            
            return competitionParticipantMapper.selectByCompetitionId(competitionId);
            
        } catch (Exception e) {
            log.error("查询比赛参赛者列表失败，比赛ID: {}", competitionId, e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("查询参赛者列表失败");
        }
    }
    
    @Override
    @Transactional
    public void removeParticipant(Integer participantId) {
        try {
            log.debug("移除参赛者，参赛者ID: {}", participantId);
            
            // 验证参赛者记录是否存在
            if (competitionParticipantMapper.selectById(participantId) == null) {
                throw new BusinessException("参赛者记录不存在");
            }
            
            int result = competitionParticipantMapper.deleteById(participantId);
            if (result <= 0) {
                throw new BusinessException("移除参赛者失败");
            }
            
            log.info("移除参赛者成功，参赛者ID: {}", participantId);
            
        } catch (Exception e) {
            log.error("移除参赛者失败，参赛者ID: {}", participantId, e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("移除参赛者失败");
        }
    }
    
    @Override
    public List<CompetitionParticipantVO> getMyCompetitions() {
        try {
            Integer currentUserId = CurrentUser.getCurrentUserId();
            log.debug("查询我的比赛记录，用户ID: {}", currentUserId);
            
            return competitionParticipantMapper.selectByStudentId(currentUserId);
            
        } catch (Exception e) {
            log.error("查询我的比赛记录失败", e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("查询我的比赛记录失败");
        }
    }
    
    @Override
    @Transactional
    public void registerCompetition(CompetitionRegistrationDTO registrationDTO) {
        try {
            Integer currentUserId = CurrentUser.getCurrentUserId();
            log.debug("比赛报名，用户ID: {}, 参数: {}", currentUserId, registrationDTO);
            
            // 验证比赛是否存在且可报名
            Competition competition = competitionMapper.selectById(registrationDTO.getCompetitionId());
            if (competition == null) {
                throw new BusinessException("比赛不存在");
            }
            
            if (competition.getStatus() != Competition.STATUS_REGISTRATION) {
                throw new BusinessException("比赛当前不可报名");
            }
            
            // 检查是否已经报名
            int existingCount = competitionParticipantMapper.countByCompetitionAndStudent(
                registrationDTO.getCompetitionId(), currentUserId);
            if (existingCount > 0) {
                throw new BusinessException("您已经报名该比赛");
            }
            
            // 验证用户角色（只有学员可以报名）
            User user = userMapper.selectById(currentUserId);
            if (user == null || user.getUserRole() != User.ROLE_STUDENT) {
                throw new BusinessException("只有学员可以报名比赛");
            }
            
            // 扣除报名费（30元）
            java.math.BigDecimal registrationFee = new java.math.BigDecimal("30.00");
            
            // 检查数据库中的余额（使用实际的balance字段）
            if (user.getBalance() == null) {
                throw new BusinessException("账户余额信息异常，请联系管理员");
            }
            
            if (user.getBalance().compareTo(registrationFee) < 0) {
                throw new BusinessException("账户余额不足，请先充值。当前余额：" + user.getBalance() + "元，需要：" + registrationFee + "元");
            }
            
            // 创建报名费流水记录并更新余额（跳过流水计算的余额检查）
            transactionService.createCompetitionFeeTransactionWithoutBalanceCheck(
                currentUserId, registrationFee, "比赛报名费 - " + competition.getName());
            
            // 创建参赛记录
            CompetitionParticipant participant = new CompetitionParticipant(
                registrationDTO.getCompetitionId(),
                currentUserId,
                registrationDTO.getGroupLevel()
            );
            
            int result = competitionParticipantMapper.insert(participant);
            if (result <= 0) {
                throw new BusinessException("报名失败");
            }
            
            log.info("比赛报名成功，用户ID: {}, 比赛ID: {}, 组别: {}", 
                currentUserId, registrationDTO.getCompetitionId(), registrationDTO.getGroupLevel());
            
        } catch (Exception e) {
            log.error("比赛报名失败，参数: {}", registrationDTO, e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("比赛报名失败");
        }
    }
}
