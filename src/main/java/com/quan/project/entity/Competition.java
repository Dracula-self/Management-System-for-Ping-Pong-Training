package com.quan.project.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

/**
 * 比赛实体类
 * 对应数据库表：competitions
 */
public class Competition {
    
    /**
     * 主键ID
     */
    private Integer id;
    
    /**
     * 比赛名称
     */
    private String name;
    
    /**
     * 比赛日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate competitionDate;
    
    /**
     * 状态: 0-报名中, 1-报名结束, 2-进行中, 3-已完成
     */
    private Integer status;
    
    // 状态常量
    public static final int STATUS_REGISTRATION = 0;        // 报名中
    public static final int STATUS_REGISTRATION_CLOSED = 1; // 报名结束
    public static final int STATUS_IN_PROGRESS = 2;         // 进行中
    public static final int STATUS_COMPLETED = 3;           // 已完成
    
    // 无参构造方法
    public Competition() {}
    
    // 全参构造方法
    public Competition(Integer id, String name, LocalDate competitionDate, Integer status) {
        this.id = id;
        this.name = name;
        this.competitionDate = competitionDate;
        this.status = status;
    }
    
    // 便捷构造方法
    public Competition(String name, LocalDate competitionDate) {
        this.name = name;
        this.competitionDate = competitionDate;
        this.status = STATUS_REGISTRATION; // 默认报名中状态
    }
    
    /**
     * 获取状态文本描述
     */
    public String getStatusText() {
        switch (this.status) {
            case STATUS_REGISTRATION:
                return "报名中";
            case STATUS_REGISTRATION_CLOSED:
                return "报名结束";
            case STATUS_IN_PROGRESS:
                return "进行中";
            case STATUS_COMPLETED:
                return "已完成";
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
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public LocalDate getCompetitionDate() {
        return competitionDate;
    }
    
    public void setCompetitionDate(LocalDate competitionDate) {
        this.competitionDate = competitionDate;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
        return "Competition{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", competitionDate=" + competitionDate +
                ", status=" + status +
                '}';
    }
}
