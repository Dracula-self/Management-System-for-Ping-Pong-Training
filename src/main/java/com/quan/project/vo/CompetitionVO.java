package com.quan.project.vo;

import com.quan.project.entity.Competition;

/**
 * 比赛视图对象
 * 继承基础实体，添加统计信息
 */
public class CompetitionVO extends Competition {
    
    /**
     * 报名人数
     */
    private Integer participantCount;
    
    // 无参构造方法
    public CompetitionVO() {
        super();
    }
    
    // Getter和Setter方法
    public Integer getParticipantCount() {
        return participantCount;
    }
    
    public void setParticipantCount(Integer participantCount) {
        this.participantCount = participantCount;
    }
    
    @Override
    public String toString() {
        return "CompetitionVO{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", competitionDate=" + getCompetitionDate() +
                ", status=" + getStatus() +
                ", participantCount=" + participantCount +
                '}';
    }
}
