package com.quan.project.entity;

/**
 * 比赛报名实体类
 * 对应数据库表：competition_participants
 */
public class CompetitionParticipant {
    
    /**
     * 主键ID
     */
    private Integer id;
    
    /**
     * 比赛ID
     */
    private Integer competitionId;
    
    /**
     * 学员ID
     */
    private Integer studentId;
    
    /**
     * 组别: 1-甲, 2-乙, 3-丙
     */
    private Integer groupLevel;
    
    // 组别常量
    public static final int GROUP_A = 1;  // 甲组
    public static final int GROUP_B = 2;  // 乙组
    public static final int GROUP_C = 3;  // 丙组
    
    // 无参构造方法
    public CompetitionParticipant() {}
    
    // 全参构造方法
    public CompetitionParticipant(Integer id, Integer competitionId, Integer studentId, Integer groupLevel) {
        this.id = id;
        this.competitionId = competitionId;
        this.studentId = studentId;
        this.groupLevel = groupLevel;
    }
    
    // 便捷构造方法
    public CompetitionParticipant(Integer competitionId, Integer studentId, Integer groupLevel) {
        this.competitionId = competitionId;
        this.studentId = studentId;
        this.groupLevel = groupLevel;
    }
    
    /**
     * 获取组别文本描述
     */
    public String getGroupLevelText() {
        switch (this.groupLevel) {
            case GROUP_A:
                return "甲组";
            case GROUP_B:
                return "乙组";
            case GROUP_C:
                return "丙组";
            default:
                return "未知组别";
        }
    }
    
    // Getter和Setter方法
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Integer getCompetitionId() {
        return competitionId;
    }
    
    public void setCompetitionId(Integer competitionId) {
        this.competitionId = competitionId;
    }
    
    public Integer getStudentId() {
        return studentId;
    }
    
    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }
    
    public Integer getGroupLevel() {
        return groupLevel;
    }
    
    public void setGroupLevel(Integer groupLevel) {
        this.groupLevel = groupLevel;
    }
    
    @Override
    public String toString() {
        return "CompetitionParticipant{" +
                "id=" + id +
                ", competitionId=" + competitionId +
                ", studentId=" + studentId +
                ", groupLevel=" + groupLevel +
                '}';
    }
}
