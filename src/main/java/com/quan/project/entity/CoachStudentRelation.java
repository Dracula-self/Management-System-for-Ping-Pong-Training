package com.quan.project.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

/**
 * 师生关系实体类
 * 对应数据库表：coach_student_relations
 */
public class CoachStudentRelation {
    
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
     * 状态: 0-待教练确认, 1-已确认, 2-已解约
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    // 状态常量
    public static final int STATUS_PENDING = 0;     // 待教练确认
    public static final int STATUS_CONFIRMED = 1;   // 已确认
    public static final int STATUS_TERMINATED = 2;  // 已解约
    
    // 无参构造方法
    public CoachStudentRelation() {}
    
    // 全参构造方法
    public CoachStudentRelation(Integer id, Integer coachId, Integer studentId, 
                               Integer status, LocalDateTime createTime) {
        this.id = id;
        this.coachId = coachId;
        this.studentId = studentId;
        this.status = status;
        this.createTime = createTime;
    }
    
    // 便捷构造方法
    public CoachStudentRelation(Integer coachId, Integer studentId) {
        this.coachId = coachId;
        this.studentId = studentId;
        this.status = STATUS_PENDING; // 默认待确认状态
    }
    
    /**
     * 获取状态文本描述
     */
    public String getStatusText() {
        switch (this.status) {
            case STATUS_PENDING:
                return "待教练确认";
            case STATUS_CONFIRMED:
                return "已确认";
            case STATUS_TERMINATED:
                return "已解约";
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
        return "CoachStudentRelation{" +
                "id=" + id +
                ", coachId=" + coachId +
                ", studentId=" + studentId +
                ", status=" + status +
                ", createTime=" + createTime +
                '}';
    }
}
