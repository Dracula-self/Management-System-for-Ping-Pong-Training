package com.quan.project.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

/**
 * 系统消息实体类
 * 对应数据库表：system_messages
 */
public class SystemMessage {
    
    /**
     * 主键ID
     */
    private Integer id;
    
    /**
     * 消息标题
     */
    private String title;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 消息类型: 1-系统通知, 2-更新公告, 3-活动消息
     */
    private Integer type;
    
    /**
     * 状态: 1-草稿, 2-已发布, 3-已归档
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    // 消息类型常量
    public static final int TYPE_SYSTEM_NOTICE = 1;   // 系统通知
    public static final int TYPE_UPDATE_NOTICE = 2;   // 更新公告
    public static final int TYPE_ACTIVITY_NOTICE = 3; // 活动消息
    
    // 状态常量
    public static final int STATUS_DRAFT = 1;      // 草稿
    public static final int STATUS_PUBLISHED = 2;  // 已发布
    public static final int STATUS_ARCHIVED = 3;   // 已归档
    
    // 无参构造方法
    public SystemMessage() {}
    
    // 全参构造方法
    public SystemMessage(Integer id, String title, String content, Integer type, Integer status, LocalDateTime createTime) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.type = type;
        this.status = status;
        this.createTime = createTime;
    }
    
    // 便捷构造方法
    public SystemMessage(String title, String content, Integer type) {
        this.title = title;
        this.content = content;
        this.type = type;
        this.status = STATUS_PUBLISHED; // 默认已发布状态
    }
    
    
    // Getter和Setter方法
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public Integer getType() {
        return type;
    }
    
    public void setType(Integer type) {
        this.type = type;
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
        return "SystemMessage{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", type=" + type +
                ", status=" + status +
                ", createTime=" + createTime +
                '}';
    }
}
