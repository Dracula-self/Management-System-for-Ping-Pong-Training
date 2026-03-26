package com.quan.project.service;

import com.github.pagehelper.PageInfo;
import com.quan.project.dto.PageDTO;
import com.quan.project.entity.Transaction;
import com.quan.project.vo.TransactionVO;

import java.math.BigDecimal;
import java.util.List;

/**
 * 账户流水服务接口
 */
public interface TransactionService {
    
    /**
     * 学员充值
     */
    Transaction recharge(BigDecimal amount, String paymentMethod);
    
    /**
     * 管理员手工充值
     */
    Transaction manualRecharge(Integer studentId, BigDecimal amount, String notes);
    
    /**
     * 创建消费流水记录
     */
    Transaction createPaymentTransaction(Integer studentId, BigDecimal amount, String notes);
    
    /**
     * 创建退款流水记录
     */
    Transaction createRefundTransaction(Integer studentId, BigDecimal amount, String notes);
    
    /**
     * 创建比赛报名费流水记录
     */
    Transaction createCompetitionFeeTransaction(Integer studentId, BigDecimal amount, String notes);
    
    /**
     * 创建比赛报名费流水记录（跳过余额检查）
     */
    Transaction createCompetitionFeeTransactionWithoutBalanceCheck(Integer studentId, BigDecimal amount, String notes);
    
    /**
     * 查询流水记录
     */
    PageInfo<Transaction> getTransactionPage(PageDTO pageRequest);
    
    /**
     * 查询流水记录（包含学员姓名）
     */
    PageInfo<TransactionVO> getTransactionPageWithInfo(PageDTO pageRequest);
    
    /**
     * 根据ID查询流水记录
     */
    Transaction getTransactionById(Integer id);
    
    /**
     * 查询学员流水记录
     */
    List<Transaction> getStudentTransactions(Integer studentId);
    
    /**
     * 查询学员最近的流水记录
     */
    List<Transaction> getRecentTransactions(Integer studentId, Integer limit);
    
    /**
     * 计算学员账户余额
     */
    BigDecimal calculateStudentBalance(Integer studentId);
    
    /**
     * 更新用户余额到用户表
     */
    void updateUserBalance(Integer studentId);
    
    /**
     * 统计学员指定类型的流水总额
     */
    BigDecimal sumTransactionsByType(Integer studentId, Integer type);
    
    /**
     * 检查学员余额是否足够
     */
    boolean checkBalance(Integer studentId, BigDecimal amount);
    
    /**
     * 删除流水记录（仅限充值记录）
     */
    void deleteTransaction(Integer id);
    
    /**
     * 获取我的流水记录（分页查询）
     */
    PageInfo<Transaction> getMyTransactions(PageDTO pageRequest);
    
    /**
     * 获取我的统计数据
     */
    Object getMyStatistics();
}
