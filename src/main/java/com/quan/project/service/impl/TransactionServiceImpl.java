package com.quan.project.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.quan.project.common.CurrentUser;
import com.quan.project.dto.PageDTO;
import com.quan.project.entity.Transaction;
import com.quan.project.entity.User;
import com.quan.project.exception.BusinessException;
import com.quan.project.mapper.TransactionMapper;
import com.quan.project.mapper.UserMapper;
import com.quan.project.service.TransactionService;
import com.quan.project.vo.TransactionVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 账户流水服务实现类
 */
@Service
public class TransactionServiceImpl implements TransactionService {
    
    private static final Logger log = LoggerFactory.getLogger(TransactionServiceImpl.class);
    
    @Autowired
    private TransactionMapper transactionMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    @Transactional
    public Transaction recharge(BigDecimal amount, String paymentMethod) {
        try {
            Integer studentId = CurrentUser.getCurrentUserId();
            
            // 验证用户是学员
            User user = userMapper.selectById(studentId);
            if (user == null || user.getUserRole() != User.ROLE_STUDENT) {
                throw new BusinessException("只有学员可以充值");
            }
            
            // 验证充值金额
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessException("充值金额必须大于0");
            }
            
            if (amount.compareTo(BigDecimal.valueOf(10000)) > 0) {
                throw new BusinessException("单次充值金额不能超过10000元");
            }
            
            // 创建充值流水记录
            Transaction transaction = new Transaction();
            transaction.setStudentId(studentId);
            transaction.setType(Transaction.TYPE_RECHARGE);
            transaction.setAmount(amount);
            transaction.setNotes("学员充值 - " + paymentMethod);
            
            int result = transactionMapper.insert(transaction);
            if (result <= 0) {
                throw new BusinessException("创建充值记录失败");
            }
            
            // 更新用户余额
            updateUserBalance(studentId);
            
            log.info("学员充值成功，学员ID: {}, 金额: {}, 支付方式: {}", studentId, amount, paymentMethod);
            return transaction;
            
        } catch (Exception e) {
            log.error("学员充值失败，金额: {}", amount, e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("充值失败");
        }
    }
    
    @Override
    @Transactional
    public Transaction manualRecharge(Integer studentId, BigDecimal amount, String notes) {
        try {
            // 验证管理员权限
            Integer currentUserId = CurrentUser.getCurrentUserId();
            User currentUser = userMapper.selectById(currentUserId);
            if (currentUser == null || (currentUser.getUserRole() != User.ROLE_SUPER_ADMIN && 
                currentUser.getUserRole() != User.ROLE_CAMPUS_ADMIN)) {
                throw new BusinessException("只有管理员可以手工充值");
            }
            
            // 验证学员存在
            User student = userMapper.selectById(studentId);
            if (student == null || student.getUserRole() != User.ROLE_STUDENT) {
                throw new BusinessException("学员不存在");
            }
            
            // 验证充值金额
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessException("充值金额必须大于0");
            }
            
            // 创建手工充值流水记录
            Transaction transaction = new Transaction();
            transaction.setStudentId(studentId);
            transaction.setType(Transaction.TYPE_RECHARGE);
            transaction.setAmount(amount);
            transaction.setNotes("管理员手工充值 - " + (notes != null ? notes : "无备注"));
            
            int result = transactionMapper.insert(transaction);
            if (result <= 0) {
                throw new BusinessException("创建充值记录失败");
            }
            
            // 更新用户余额
            updateUserBalance(studentId);
            
            log.info("管理员手工充值成功，管理员ID: {}, 学员ID: {}, 金额: {}", 
                currentUserId, studentId, amount);
            return transaction;
            
        } catch (Exception e) {
            log.error("管理员手工充值失败，学员ID: {}, 金额: {}", studentId, amount, e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("手工充值失败");
        }
    }
    
    @Override
    @Transactional
    public Transaction createPaymentTransaction(Integer studentId, BigDecimal amount, String notes) {
        try {
            // 验证学员存在
            User student = userMapper.selectById(studentId);
            if (student == null || student.getUserRole() != User.ROLE_STUDENT) {
                throw new BusinessException("学员不存在");
            }
            
            // 验证金额
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessException("消费金额必须大于0");
            }
            
            // 检查余额
            if (!checkBalance(studentId, amount)) {
                throw new BusinessException("账户余额不足");
            }
            
            // 创建消费流水记录（负数）
            Transaction transaction = new Transaction();
            transaction.setStudentId(studentId);
            transaction.setType(Transaction.TYPE_COURSE_PAYMENT);
            transaction.setAmount(amount.negate());
            transaction.setNotes(notes != null ? notes : "课程消费");
            
            int result = transactionMapper.insert(transaction);
            if (result <= 0) {
                throw new BusinessException("创建消费记录失败");
            }
            
            // 更新用户余额
            updateUserBalance(studentId);
            
            log.info("创建消费流水成功，学员ID: {}, 金额: {}", studentId, amount);
            return transaction;
            
        } catch (Exception e) {
            log.error("创建消费流水失败，学员ID: {}, 金额: {}", studentId, amount, e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("创建消费记录失败");
        }
    }
    
    @Override
    @Transactional
    public Transaction createRefundTransaction(Integer studentId, BigDecimal amount, String notes) {
        try {
            // 验证学员存在
            User student = userMapper.selectById(studentId);
            if (student == null || student.getUserRole() != User.ROLE_STUDENT) {
                throw new BusinessException("学员不存在");
            }
            
            // 验证金额
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessException("退款金额必须大于0");
            }
            
            // 创建退款流水记录（正数）
            Transaction transaction = new Transaction();
            transaction.setStudentId(studentId);
            transaction.setType(Transaction.TYPE_COURSE_REFUND);
            transaction.setAmount(amount);
            transaction.setNotes(notes != null ? notes : "课程退款");
            
            int result = transactionMapper.insert(transaction);
            if (result <= 0) {
                throw new BusinessException("创建退款记录失败");
            }
            
            // 更新用户余额
            updateUserBalance(studentId);
            
            log.info("创建退款流水成功，学员ID: {}, 金额: {}", studentId, amount);
            return transaction;
            
        } catch (Exception e) {
            log.error("创建退款流水失败，学员ID: {}, 金额: {}", studentId, amount, e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("创建退款记录失败");
        }
    }
    
    @Override
    @Transactional
    public Transaction createCompetitionFeeTransaction(Integer studentId, BigDecimal amount, String notes) {
        try {
            // 验证学员存在
            User student = userMapper.selectById(studentId);
            if (student == null || student.getUserRole() != User.ROLE_STUDENT) {
                throw new BusinessException("学员不存在");
            }
            
            // 验证金额
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessException("报名费必须大于0");
            }
            
            // 检查余额
            BigDecimal currentBalance = calculateStudentBalance(studentId);
            if (currentBalance.compareTo(amount) < 0) {
                throw new BusinessException("账户余额不足，请先充值。当前余额：" + currentBalance + "元，需要：" + amount + "元");
            }
            
            // 创建比赛报名费流水记录（负数）
            Transaction transaction = new Transaction();
            transaction.setStudentId(studentId);
            transaction.setType(Transaction.TYPE_COMPETITION_FEE);
            transaction.setAmount(amount.negate());
            transaction.setNotes(notes != null ? notes : "比赛报名费");
            
            int result = transactionMapper.insert(transaction);
            if (result <= 0) {
                throw new BusinessException("创建报名费记录失败");
            }
            
            // 更新用户余额
            updateUserBalance(studentId);
            
            log.info("创建比赛报名费流水成功，学员ID: {}, 金额: {}", studentId, amount);
            return transaction;
            
        } catch (Exception e) {
            log.error("创建比赛报名费流水失败，学员ID: {}, 金额: {}", studentId, amount, e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("创建报名费记录失败");
        }
    }
    
    @Override
    @Transactional
    public Transaction createCompetitionFeeTransactionWithoutBalanceCheck(Integer studentId, BigDecimal amount, String notes) {
        try {
            // 验证学员存在
            User student = userMapper.selectById(studentId);
            if (student == null || student.getUserRole() != User.ROLE_STUDENT) {
                throw new BusinessException("学员不存在");
            }
            
            // 验证金额
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessException("报名费必须大于0");
            }
            
            // 跳过余额检查，直接创建比赛报名费流水记录（负数）
            Transaction transaction = new Transaction();
            transaction.setStudentId(studentId);
            transaction.setType(Transaction.TYPE_COMPETITION_FEE);
            transaction.setAmount(amount.negate());
            transaction.setNotes(notes != null ? notes : "比赛报名费");
            
            int result = transactionMapper.insert(transaction);
            if (result <= 0) {
                throw new BusinessException("创建报名费记录失败");
            }
            
            // 更新用户余额（直接从数据库balance字段扣除）
            BigDecimal newBalance = student.getBalance().subtract(amount);
            student.setBalance(newBalance);
            userMapper.update(student);
            
            log.info("创建比赛报名费流水成功，学员ID: {}, 金额: {}, 新余额: {}", studentId, amount, newBalance);
            return transaction;
            
        } catch (Exception e) {
            log.error("创建比赛报名费流水失败，学员ID: {}, 金额: {}", studentId, amount, e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("创建报名费记录失败");
        }
    }
    
    @Override
    public PageInfo<Transaction> getTransactionPage(PageDTO pageRequest) {
        try {
            // 如果不是管理员，只能查看自己的流水
            Integer currentUserId = CurrentUser.getCurrentUserId();
            User currentUser = userMapper.selectById(currentUserId);
            
            if (currentUser.getUserRole() == User.ROLE_STUDENT) {
                // 学员只能查看自己的流水
                pageRequest.addParam("studentId", currentUserId);
            }
            
            PageHelper.startPage(pageRequest.getPageNum(), pageRequest.getPageSize());
            List<Transaction> transactions = transactionMapper.selectPageByConditions(pageRequest);
            return new PageInfo<>(transactions);
        } catch (Exception e) {
            log.error("分页查询流水记录失败", e);
            throw new BusinessException("查询流水记录失败");
        }
    }
    
    @Override
    public PageInfo<TransactionVO> getTransactionPageWithInfo(PageDTO pageRequest) {
        try {
            // 根据当前用户角色进行权限过滤
            Integer currentUserId = CurrentUser.getCurrentUserId();
            Integer currentUserRole = CurrentUser.getCurrentUserRole();
            
            if (currentUserRole == User.ROLE_STUDENT) {
                // 学员只能查看自己的流水
                pageRequest.addParam("studentId", currentUserId);
            } else if (currentUserRole == User.ROLE_CAMPUS_ADMIN) {
                // 校区管理员只能查看本校区学员的流水
                User currentUser = userMapper.selectById(currentUserId);
                if (currentUser != null && currentUser.getCampusId() != null) {
                    pageRequest.addParam("campusId", currentUser.getCampusId());
                }
            }
            // 超级管理员(1)可以查看所有流水，不需要额外过滤
            
            PageHelper.startPage(pageRequest.getPageNum(), pageRequest.getPageSize());
            List<TransactionVO> transactions = transactionMapper.selectPageWithInfo(pageRequest);
            return new PageInfo<>(transactions);
        } catch (Exception e) {
            log.error("分页查询流水记录（含详细信息）失败", e);
            throw new BusinessException("查询流水记录失败");
        }
    }
    
    @Override
    public Transaction getTransactionById(Integer id) {
        try {
            Transaction transaction = transactionMapper.selectById(id);
            if (transaction == null) {
                throw new BusinessException("流水记录不存在");
            }
            
            // 权限检查：学员只能查看自己的流水
            Integer currentUserId = CurrentUser.getCurrentUserId();
            User currentUser = userMapper.selectById(currentUserId);
            
            if (currentUser.getUserRole() == User.ROLE_STUDENT && 
                !transaction.getStudentId().equals(currentUserId)) {
                throw new BusinessException("无权查看此流水记录");
            }
            
            return transaction;
        } catch (Exception e) {
            log.error("查询流水记录失败，ID: {}", id, e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("查询流水记录失败");
        }
    }
    
    @Override
    public List<Transaction> getStudentTransactions(Integer studentId) {
        try {
            // 权限检查
            Integer currentUserId = CurrentUser.getCurrentUserId();
            User currentUser = userMapper.selectById(currentUserId);
            
            if (currentUser.getUserRole() == User.ROLE_STUDENT && !studentId.equals(currentUserId)) {
                throw new BusinessException("只能查看自己的流水记录");
            }
            
            return transactionMapper.selectByStudentId(studentId);
        } catch (Exception e) {
            log.error("查询学员流水记录失败，学员ID: {}", studentId, e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("查询学员流水记录失败");
        }
    }
    
    @Override
    public List<Transaction> getRecentTransactions(Integer studentId, Integer limit) {
        try {
            // 权限检查
            Integer currentUserId = CurrentUser.getCurrentUserId();
            User currentUser = userMapper.selectById(currentUserId);
            
            if (currentUser.getUserRole() == User.ROLE_STUDENT && !studentId.equals(currentUserId)) {
                throw new BusinessException("只能查看自己的流水记录");
            }
            
            return transactionMapper.selectRecentByStudentId(studentId, limit != null ? limit : 10);
        } catch (Exception e) {
            log.error("查询最近流水记录失败，学员ID: {}", studentId, e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("查询最近流水记录失败");
        }
    }
    
    @Override
    public BigDecimal calculateStudentBalance(Integer studentId) {
        try {
            BigDecimal balance = transactionMapper.calculateBalance(studentId);
            return balance != null ? balance : BigDecimal.ZERO;
        } catch (Exception e) {
            log.error("计算学员余额失败，学员ID: {}", studentId, e);
            return BigDecimal.ZERO;
        }
    }
    
    @Override
    @Transactional
    public void updateUserBalance(Integer studentId) {
        try {
            BigDecimal balance = calculateStudentBalance(studentId);
            User user = userMapper.selectById(studentId);
            if (user != null) {
                user.setBalance(balance);
                userMapper.update(user);
                log.debug("更新用户余额成功，学员ID: {}, 余额: {}", studentId, balance);
            }
        } catch (Exception e) {
            log.error("更新用户余额失败，学员ID: {}", studentId, e);
            // 不抛出异常，避免影响主流程
        }
    }
    
    @Override
    public BigDecimal sumTransactionsByType(Integer studentId, Integer type) {
        try {
            BigDecimal sum = transactionMapper.sumAmountByStudentIdAndType(studentId, type);
            return sum != null ? sum : BigDecimal.ZERO;
        } catch (Exception e) {
            log.error("统计流水总额失败，学员ID: {}, 类型: {}", studentId, type, e);
            return BigDecimal.ZERO;
        }
    }
    
    @Override
    public boolean checkBalance(Integer studentId, BigDecimal amount) {
        try {
            // 如果studentId为null，使用当前用户ID
            if (studentId == null) {
                studentId = CurrentUser.getCurrentUserId();
            }
            BigDecimal balance = calculateStudentBalance(studentId);
            return balance.compareTo(amount) >= 0;
        } catch (Exception e) {
            log.error("检查余额失败，学员ID: {}, 所需金额: {}", studentId, amount, e);
            return false; // 出错时保守处理
        }
    }
    
    @Override
    @Transactional
    public void deleteTransaction(Integer id) {
        try {
            Integer currentUserId = CurrentUser.getCurrentUserId();
            Integer currentUserRole = CurrentUser.getCurrentUserRole();
            
            // 查询流水记录
            Transaction transaction = transactionMapper.selectById(id);
            if (transaction == null) {
                throw new BusinessException("流水记录不存在");
            }
            
            // 权限验证：只有管理员可以删除，且只能删除充值记录
            if (currentUserRole != User.ROLE_SUPER_ADMIN && currentUserRole != User.ROLE_CAMPUS_ADMIN) {
                throw new BusinessException("您没有权限删除流水记录");
            }
            
            // 只能删除充值记录
            if (transaction.getType() != Transaction.TYPE_RECHARGE) {
                throw new BusinessException("只能删除充值记录");
            }
            
            // 校区管理员只能删除本校区学员的记录
            if (currentUserRole == User.ROLE_CAMPUS_ADMIN) {
                User currentUser = userMapper.selectById(currentUserId);
                User student = userMapper.selectById(transaction.getStudentId());
                if (currentUser == null || student == null || 
                    !currentUser.getCampusId().equals(student.getCampusId())) {
                    throw new BusinessException("您只能删除本校区学员的流水记录");
                }
            }
            
            // 删除流水记录
            int result = transactionMapper.deleteById(id);
            if (result <= 0) {
                throw new BusinessException("删除流水记录失败");
            }
            
            // 更新学员余额
            updateUserBalance(transaction.getStudentId());
            
            log.info("删除流水记录成功，记录ID: {}, 学员ID: {}, 操作者: {}", 
                id, transaction.getStudentId(), currentUserId);
                
        } catch (Exception e) {
            log.error("删除流水记录失败，记录ID: {}", id, e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("删除流水记录失败");
        }
    }
    
    @Override
    public PageInfo<Transaction> getMyTransactions(PageDTO pageRequest) {
        try {
            Integer currentUserId = CurrentUser.getCurrentUserId();
            log.debug("查询我的流水记录，用户ID: {}, 参数: {}", currentUserId, pageRequest);
            
            // 将当前用户ID添加到查询参数中
            if (pageRequest.getParams() == null) {
                pageRequest.setParams(new java.util.HashMap<>());
            }
            pageRequest.getParams().put("studentId", currentUserId);
            
            // 启动分页
            PageHelper.startPage(pageRequest.getPageNum(), pageRequest.getPageSize());
            
            // 查询数据
            List<Transaction> transactions = transactionMapper.selectPageByConditions(pageRequest);
            
            return new PageInfo<>(transactions);
            
        } catch (Exception e) {
            log.error("查询我的流水记录失败，参数: {}", pageRequest, e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("查询流水记录失败");
        }
    }
    
    @Override
    public Object getMyStatistics() {
        try {
            Integer currentUserId = CurrentUser.getCurrentUserId();
            log.debug("查询我的统计数据，用户ID: {}", currentUserId);
            
            // 获取用户信息（包含当前余额）
            User user = userMapper.selectById(currentUserId);
            
            // 计算统计数据
            BigDecimal totalRecharge = sumTransactionsByType(currentUserId, Transaction.TYPE_RECHARGE);
            BigDecimal totalConsumption = sumTransactionsByType(currentUserId, Transaction.TYPE_COURSE_PAYMENT)
                .add(sumTransactionsByType(currentUserId, Transaction.TYPE_COMPETITION_FEE));
            
            // 统计流水记录总数
            List<Transaction> allTransactions = transactionMapper.selectByStudentId(currentUserId);
            int totalTransactions = allTransactions.size();
            
            // 构建统计数据
            java.util.Map<String, Object> statistics = new java.util.HashMap<>();
            statistics.put("currentBalance", user != null ? user.getBalance() : BigDecimal.ZERO);
            statistics.put("totalRecharge", totalRecharge);
            statistics.put("totalConsumption", totalConsumption.abs()); // 消费金额显示为正数
            statistics.put("totalTransactions", totalTransactions);
            
            return statistics;
            
        } catch (Exception e) {
            log.error("查询我的统计数据失败", e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("查询统计数据失败");
        }
    }
}
