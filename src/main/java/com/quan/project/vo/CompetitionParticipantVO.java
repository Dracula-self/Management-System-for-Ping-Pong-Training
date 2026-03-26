package com.quan.project.vo;

import com.quan.project.entity.CompetitionParticipant;

/**
 * 比赛参赛者视图对象
 * 继承基础实体，添加学员相关信息
 */
public class CompetitionParticipantVO extends CompetitionParticipant {
    
    /**
     * 学员姓名
     */
    private String studentName;
    
    /**
     * 学员用户名
     */
    private String studentUsername;
    
    /**
     * 校区名称
     */
    private String campusName;
    
    /**
     * 学员手机号
     */
    private String studentPhone;
    
    /**
     * 比赛名称
     */
    private String competitionName;
    
    /**
     * 比赛日期
     */
    private String competitionDate;
    
    /**
     * 比赛状态
     */
    private Integer competitionStatus;
    
    // 无参构造方法
    public CompetitionParticipantVO() {
        super();
    }
    
    // Getter和Setter方法
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
    
    public String getCampusName() {
        return campusName;
    }
    
    public void setCampusName(String campusName) {
        this.campusName = campusName;
    }
    
    public String getStudentPhone() {
        return studentPhone;
    }
    
    public void setStudentPhone(String studentPhone) {
        this.studentPhone = studentPhone;
    }
    
    public String getCompetitionName() {
        return competitionName;
    }
    
    public void setCompetitionName(String competitionName) {
        this.competitionName = competitionName;
    }
    
    public String getCompetitionDate() {
        return competitionDate;
    }
    
    public void setCompetitionDate(String competitionDate) {
        this.competitionDate = competitionDate;
    }
    
    public Integer getCompetitionStatus() {
        return competitionStatus;
    }
    
    public void setCompetitionStatus(Integer competitionStatus) {
        this.competitionStatus = competitionStatus;
    }
    
    @Override
    public String toString() {
        return "CompetitionParticipantVO{" +
                "id=" + getId() +
                ", competitionId=" + getCompetitionId() +
                ", studentId=" + getStudentId() +
                ", groupLevel=" + getGroupLevel() +
                ", studentName='" + studentName + '\'' +
                ", studentUsername='" + studentUsername + '\'' +
                ", campusName='" + campusName + '\'' +
                ", studentPhone='" + studentPhone + '\'' +
                ", competitionName='" + competitionName + '\'' +
                ", competitionDate='" + competitionDate + '\'' +
                ", competitionStatus=" + competitionStatus +
                '}';
    }
}
