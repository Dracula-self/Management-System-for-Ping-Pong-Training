package com.quan.project.dto;

/**
 * 比赛报名数据传输对象
 */
public class CompetitionRegistrationDTO {
    
    /**
     * 比赛ID
     */
    private Integer competitionId;
    
    /**
     * 参赛组别: 1-甲组, 2-乙组, 3-丙组
     */
    private Integer groupLevel;
    
    // 无参构造方法
    public CompetitionRegistrationDTO() {}
    
    // 全参构造方法
    public CompetitionRegistrationDTO(Integer competitionId, Integer groupLevel) {
        this.competitionId = competitionId;
        this.groupLevel = groupLevel;
    }
    
    // Getter和Setter方法
    public Integer getCompetitionId() {
        return competitionId;
    }
    
    public void setCompetitionId(Integer competitionId) {
        this.competitionId = competitionId;
    }
    
    public Integer getGroupLevel() {
        return groupLevel;
    }
    
    public void setGroupLevel(Integer groupLevel) {
        this.groupLevel = groupLevel;
    }
    
    @Override
    public String toString() {
        return "CompetitionRegistrationDTO{" +
                "competitionId=" + competitionId +
                ", groupLevel=" + groupLevel +
                '}';
    }
}
