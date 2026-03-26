package com.quan.project.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课程预约实体类
 * 对应数据库表：appointments
 */
public class Appointment {
    
    /**
     * 主键ID
     */
    private Integer id;
    
    /**
     * 教练ID
     */
    private Integer coachId;
    
    /**
     * 学员ID
     */
    private Integer studentId;
    
    /**
     * 球台ID
     */
    private Integer tableId;
    
    /**
     * 课程开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    
    /**
     * 课程结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
    
    /**
     * 课程费用
     */
    private BigDecimal cost;
    
    /**
     * 状态: 0-待教练确认, 1-已预约, 2-已完成, 3-已取消
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    // 状态常量
    public static final int STATUS_PENDING = 0;    // 待教练确认
    public static final int STATUS_CONFIRMED = 1;  // 已预约
    public static final int STATUS_COMPLETED = 2;  // 已完成
    public static final int STATUS_CANCELLED = 3;  // 已取消
    
    // 无参构造方法
    public Appointment() {}
    
    // 全参构造方法
    public Appointment(Integer id, Integer coachId, Integer studentId, Integer tableId,
                      LocalDateTime startTime, LocalDateTime endTime, BigDecimal cost,
                      Integer status, LocalDateTime createTime) {
        this.id = id;
        this.coachId = coachId;
        this.studentId = studentId;
        this.tableId = tableId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.cost = cost;
        this.status = status;
        this.createTime = createTime;
    }
    
    /**
     * 获取状态文本描述
     */
    public String getStatusText() {
        switch (this.status) {
            case STATUS_PENDING:
                return "待教练确认";
            case STATUS_CONFIRMED:
                return "已预约";
            case STATUS_COMPLETED:
                return "已完成";
            case STATUS_CANCELLED:
                return "已取消";
            default:
                return "未知状态";
        }
    }
    
    // Getter和Setter方法
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Integer getCoachId() {
        return coachId;
    }
    
    public void setCoachId(Integer coachId) {
        this.coachId = coachId;
    }
    
    public Integer getStudentId() {
        return studentId;
    }
    
    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }
    
    public Integer getTableId() {
        return tableId;
    }
    
    public void setTableId(Integer tableId) {
        this.tableId = tableId;
    }
    
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    
    public LocalDateTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
    
    public BigDecimal getCost() {
        return cost;
    }
    
    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    
    @Override
    public String toString() {
        return "Appointment{" +
                "id=" + id +
                ", coachId=" + coachId +
                ", studentId=" + studentId +
                ", tableId=" + tableId +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", cost=" + cost +
                ", status=" + status +
                ", createTime=" + createTime +
                '}';
    }
}
