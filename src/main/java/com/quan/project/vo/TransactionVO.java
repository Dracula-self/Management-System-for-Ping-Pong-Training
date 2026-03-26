package com.quan.project.vo;

import com.quan.project.entity.Transaction;

/**
 * 流水视图对象，包含关联的学员信息
 */
public class TransactionVO extends Transaction {
    
    /**
     * 学员姓名
     */
    private String studentName;
    
    /**
     * 学员用户名
     */
    private String studentUsername;
    
    // Getter and Setter methods
    public String getStudentName() {
        return studentName;
    }
    
    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
    
    public String getStudentUsername() {
        return studentUsername;
    }
    
    public void setStudentUsername(String studentUsername) {
        this.studentUsername = studentUsername;
    }
    
    @Override
    public String toString() {
        return "TransactionVO{" +
                "id=" + getId() +
                ", studentId=" + getStudentId() +
                ", type=" + getType() +
                ", amount=" + getAmount() +
                ", notes='" + getNotes() + '\'' +
                ", transactionTime=" + getTransactionTime() +
                ", studentName='" + studentName + '\'' +
                ", studentUsername='" + studentUsername + '\'' +
                '}';
    }
}
