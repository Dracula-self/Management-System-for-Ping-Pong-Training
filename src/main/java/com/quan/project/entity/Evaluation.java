package com.quan.project.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

/**
 * 训练评价实体类
 * 对应数据库表：evaluations
 */
public class Evaluation {
    
    /**
     * 主键ID
     */
    private Integer id;
    
    /**
     * 关联的课程预约ID
     */
    private Integer appointmentId;
    
    /**
     * 学员评价: 收获和教训
     */
    private String studentFeedback;
    
    /**
     * 教练评价: 表现和建议
     */
    private String coachFeedback;
    
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    // 无参构造方法
    public Evaluation() {}
    
    // 全参构造方法
    public Evaluation(Integer id, Integer appointmentId, String studentFeedback, 
                     String coachFeedback, LocalDateTime createTime) {
        this.id = id;
        this.appointmentId = appointmentId;
        this.studentFeedback = studentFeedback;
        this.coachFeedback = coachFeedback;
        this.createTime = createTime;
    }
    
    // 便捷构造方法
    public Evaluation(Integer appointmentId, String studentFeedback, String coachFeedback) {
        this.appointmentId = appointmentId;
        this.studentFeedback = studentFeedback;
        this.coachFeedback = coachFeedback;
    }
    
    // Getter和Setter方法
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Integer getAppointmentId() {
        return appointmentId;
    }
    
    public void setAppointmentId(Integer appointmentId) {
        this.appointmentId = appointmentId;
    }
    
    public String getStudentFeedback() {
        return studentFeedback;
    }
    
    public void setStudentFeedback(String studentFeedback) {
        this.studentFeedback = studentFeedback;
    }
    
    public String getCoachFeedback() {
        return coachFeedback;
    }
    
    public void setCoachFeedback(String coachFeedback) {
        this.coachFeedback = coachFeedback;
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    
    @Override
    public String toString() {
        return "Evaluation{" +
                "id=" + id +
                ", appointmentId=" + appointmentId +
                ", studentFeedback='" + studentFeedback + '\'' +
                ", coachFeedback='" + coachFeedback + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
