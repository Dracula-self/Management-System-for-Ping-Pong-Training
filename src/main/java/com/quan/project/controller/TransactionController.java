package com.quan.project.controller;

import com.github.pagehelper.PageInfo;
import com.quan.project.common.R;
import com.quan.project.dto.PageDTO;
import com.quan.project.entity.Transaction;
import com.quan.project.service.TransactionService;
import com.quan.project.vo.TransactionVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 账户流水管理控制器
 * 基础路径: /api/transactions
 */
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    
    private static final Logger log = LoggerFactory.getLogger(TransactionController.class);
    
    @Autowired
    private TransactionService transactionService;
    
    /**
     * 6.1 学员充值 - POST /api/transactions/recharge
     * 学员账户充值
     */
    @PostMapping("/recharge")
    public R<Transaction> recharge(@RequestParam BigDecimal amount, @RequestParam String paymentMethod) {
        log.debug("学员充值，金额: {}, 支付方式: {}", amount, paymentMethod);
        Transaction transaction = transactionService.recharge(amount, paymentMethod);
        return R.success(transaction);
    }
    
    /**
     * 6.2 查询流水记录 - POST /api/transactions/search
     * 查询账户流水记录（包含学员姓名）
     */
    @PostMapping("/search")
    public R<PageInfo<TransactionVO>> search(@RequestBody PageDTO pageRequest) {
        log.debug("分页查询流水记录，参数: {}", pageRequest);
        PageInfo<TransactionVO> pageInfo = transactionService.getTransactionPageWithInfo(pageRequest);
        return R.success(pageInfo);
    }
    
    /**
     * 6.3 管理员手工充值 - POST /api/transactions/manual-recharge
     * 管理员为学员手工充值
     */
    @PostMapping("/manual-recharge")
    public R<Transaction> manualRecharge(@RequestParam Integer studentId, @RequestParam BigDecimal amount, 
                                        @RequestParam(required = false) String notes) {
        log.debug("管理员手工充值，学员ID: {}, 金额: {}, 备注: {}", studentId, amount, notes);
        Transaction transaction = transactionService.manualRecharge(studentId, amount, notes);
        return R.success(transaction);
    }
    
    /**
     * 获取流水详情 - GET /api/transactions/{id}
     * 根据ID获取流水详情
     */
    @GetMapping("/{id}")
    public R<Transaction> getById(@PathVariable Integer id) {
        log.debug("查询流水详情，ID: {}", id);
        Transaction transaction = transactionService.getTransactionById(id);
        return R.success(transaction);
    }
    
    /**
     * 查询学员流水记录 - GET /api/transactions/student/{studentId}
     * 查询指定学员的所有流水记录
     */
    @GetMapping("/student/{studentId}")
    public R<List<Transaction>> getStudentTransactions(@PathVariable Integer studentId) {
        log.debug("查询学员流水记录，学员ID: {}", studentId);
        List<Transaction> transactions = transactionService.getStudentTransactions(studentId);
        return R.success(transactions);
    }
    
    /**
     * 查询最近流水记录 - GET /api/transactions/student/{studentId}/recent
     * 查询学员最近的流水记录
     */
    @GetMapping("/student/{studentId}/recent")
    public R<List<Transaction>> getRecentTransactions(@PathVariable Integer studentId, 
                                                     @RequestParam(defaultValue = "10") Integer limit) {
        log.debug("查询最近流水记录，学员ID: {}, 限制数量: {}", studentId, limit);
        List<Transaction> transactions = transactionService.getRecentTransactions(studentId, limit);
        return R.success(transactions);
    }
    
    /**
     * 查询学员余额 - GET /api/transactions/student/{studentId}/balance
     * 计算并返回学员当前账户余额
     */
    @GetMapping("/student/{studentId}/balance")
    public R<BigDecimal> getStudentBalance(@PathVariable Integer studentId) {
        log.debug("查询学员余额，学员ID: {}", studentId);
        BigDecimal balance = transactionService.calculateStudentBalance(studentId);
        return R.success(balance);
    }
    
    /**
     * 检查余额是否足够 - GET /api/transactions/check-balance
     * 检查当前学员余额是否足够支付指定金额
     */
    @GetMapping("/check-balance")
    public R<Boolean> checkBalance(@RequestParam BigDecimal amount) {
        log.debug("检查余额，所需金额: {}", amount);
        boolean sufficient = transactionService.checkBalance(null, amount); // null表示当前用户
        return R.success(sufficient);
    }
    
    /**
     * 统计流水总额 - GET /api/transactions/student/{studentId}/sum
     * 统计学员指定类型的流水总额
     */
    @GetMapping("/student/{studentId}/sum")
    public R<BigDecimal> sumTransactionsByType(@PathVariable Integer studentId, @RequestParam Integer type) {
        log.debug("统计流水总额，学员ID: {}, 类型: {}", studentId, type);
        BigDecimal sum = transactionService.sumTransactionsByType(studentId, type);
        return R.success(sum);
    }
    
    /**
     * 删除流水记录 - DELETE /api/transactions/{id}
     * 删除指定的流水记录（仅限充值记录）
     */
    @DeleteMapping("/{id}")
    public R<Void> deleteTransaction(@PathVariable Integer id) {
        log.debug("删除流水记录，ID: {}", id);
        transactionService.deleteTransaction(id);
        return R.success();
    }
    
    /**
     * 获取我的流水记录 - POST /api/transactions/my-transactions
     * 获取当前用户的流水记录（分页查询）
     */
    @PostMapping("/my-transactions")
    public R<PageInfo<Transaction>> getMyTransactions(@RequestBody PageDTO pageRequest) {
        log.debug("查询我的流水记录，参数: {}", pageRequest);
        PageInfo<Transaction> pageInfo = transactionService.getMyTransactions(pageRequest);
        return R.success(pageInfo);
    }
    
    /**
     * 获取我的统计数据 - GET /api/transactions/my-statistics
     * 获取当前用户的流水统计数据
     */
    @GetMapping("/my-statistics")
    public R<Object> getMyStatistics() {
        log.debug("查询我的流水统计数据");
        Object statistics = transactionService.getMyStatistics();
        return R.success(statistics);
    }
}
