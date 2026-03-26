package com.quan.project.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

/**
 * 系统日志实体类
 * 对应数据库表：system_logs
 */
public class SystemLog {
    
    /**
     * 主键ID
     */
    private Integer id;
    
    /**
     * 操作用户ID
     */
    private Integer userId;
    
    /**
     * 操作类型
     */
    private String action;
    
    /**
     * 详情
     */
    private String details;
    
    /**
     * 日志时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime logTime;
    
    // 无参构造方法
    public SystemLog() {}
    
    // 全参构造方法
    public SystemLog(Integer id, Integer userId, String action, String details, LocalDateTime logTime) {
        this.id = id;
        this.userId = userId;
        this.action = action;
        this.details = details;
        this.logTime = logTime;
    }
    
    // 便捷构造方法
    public SystemLog(Integer userId, String action, String details) {
        this.userId = userId;
        this.action = action;
        this.details = details;
    }
    
    // Getter和Setter方法
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Integer getUserId() {
        return userId;
    }
    
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    
    public String getAction() {
        return action;
    }
    
    public void setAction(String action) {
        this.action = action;
    }
    
    public String getDetails() {
        return details;
    }
    
    public void setDetails(String details) {
        this.details = details;
    }
    
    public LocalDateTime getLogTime() {
        return logTime;
    }
    
    public void setLogTime(LocalDateTime logTime) {
        this.logTime = logTime;
    }
    
    @Override
    public String toString() {
        return "SystemLog{" +
                "id=" + id +
                ", userId=" + userId +
                ", action='" + action + '\'' +
                ", details='" + details + '\'' +
                ", logTime=" + logTime +
                '}';
    }
}
