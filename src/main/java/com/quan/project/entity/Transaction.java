package com.quan.project.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 账户流水实体类
 * 对应数据库表：transactions
 */
public class Transaction {
    
    /**
     * 主键ID
     */
    private Integer id;
    
    /**
     * 学员ID
     */
    private Integer studentId;
    
    /**
     * 流水类型: 1-充值, 2-课程消费, 3-课程退款, 4-比赛报名费
     */
    private Integer type;
    
    /**
     * 金额 (正数表示增加, 负数表示减少)
     */
    private BigDecimal amount;
    
    /**
     * 备注
     */
    private String notes;
    
    /**
     * 流水时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime transactionTime;
    
    // 流水类型常量
    public static final int TYPE_RECHARGE = 1;          // 充值
    public static final int TYPE_COURSE_PAYMENT = 2;    // 课程消费
    public static final int TYPE_COURSE_REFUND = 3;     // 课程退款
    public static final int TYPE_COMPETITION_FEE = 4;   // 比赛报名费
    
    // 无参构造方法
    public Transaction() {}
    
    // 全参构造方法
    public Transaction(Integer id, Integer studentId, Integer type, BigDecimal amount,
                      String notes, LocalDateTime transactionTime) {
        this.id = id;
        this.studentId = studentId;
        this.type = type;
        this.amount = amount;
        this.notes = notes;
        this.transactionTime = transactionTime;
    }
    
    // 便捷构造方法
    public Transaction(Integer studentId, Integer type, BigDecimal amount, String notes) {
        this.studentId = studentId;
        this.type = type;
        this.amount = amount;
        this.notes = notes;
    }
    
    /**
     * 获取流水类型文本描述
     */
    public String getTypeText() {
        switch (this.type) {
            case TYPE_RECHARGE:
                return "充值";
            case TYPE_COURSE_PAYMENT:
                return "课程消费";
            case TYPE_COURSE_REFUND:
                return "课程退款";
            case TYPE_COMPETITION_FEE:
                return "比赛报名费";
            default:
                return "未知类型";
        }
    }
    
    // Getter和Setter方法
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Integer getStudentId() {
        return studentId;
    }
    
    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }
    
    public Integer getType() {
        return type;
    }
    
    public void setType(Integer type) {
        this.type = type;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public LocalDateTime getTransactionTime() {
        return transactionTime;
    }
    
    public void setTransactionTime(LocalDateTime transactionTime) {
        this.transactionTime = transactionTime;
    }
    
    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", studentId=" + studentId +
                ", type=" + type +
                ", amount=" + amount +
                ", notes='" + notes + '\'' +
                ", transactionTime=" + transactionTime +
                '}';
    }
}
