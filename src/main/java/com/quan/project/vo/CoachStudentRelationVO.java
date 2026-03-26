package com.quan.project.vo;

import com.quan.project.entity.CoachStudentRelation;

/**
 * 师生关系视图对象，包含关联的用户姓名信息
 */
public class CoachStudentRelationVO extends CoachStudentRelation {
    
    /**
     * 教练姓名（真实姓名或用户名）
     */
    private String coachName;
    
    /**
     * 学员姓名（真实姓名或用户名）
     */
    private String studentName;
    
    /**
     * 教练级别
     */
    private Integer coachLevel;
    
    /**
     * 教练成绩
     */
    private String achievements;
    
    /**
     * 校区名称
     */
    private String campusName;
    
    // Getter和Setter方法
    public String getCoachName() {
        return coachName;
    }
    
    public void setCoachName(String coachName) {
        this.coachName = coachName;
    }
    
    public String getStudentName() {
        return studentName;
    }
    
    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
    
    public Integer getCoachLevel() {
        return coachLevel;
    }
    
    public void setCoachLevel(Integer coachLevel) {
        this.coachLevel = coachLevel;
    }
    
    public String getAchievements() {
        return achievements;
    }
    
    public void setAchievements(String achievements) {
        this.achievements = achievements;
    }
    
    public String getCampusName() {
        return campusName;
    }
    
    public void setCampusName(String campusName) {
        this.campusName = campusName;
    }
    
    @Override
    public String toString() {
        return "CoachStudentRelationVO{" +
                "id=" + getId() +
                ", coachId=" + getCoachId() +
                ", studentId=" + getStudentId() +
                ", coachName='" + coachName + '\'' +
                ", studentName='" + studentName + '\'' +
                ", coachLevel=" + coachLevel +
                ", achievements='" + achievements + '\'' +
                ", campusName='" + campusName + '\'' +
                ", status=" + getStatus() +
                ", createTime=" + getCreateTime() +
                '}';
    }
}
