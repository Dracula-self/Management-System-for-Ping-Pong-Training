package com.quan.project.vo;

import com.quan.project.entity.Appointment;

/**
 * 预约视图对象，包含关联的教练、学员、球台信息
 */
public class AppointmentVO extends Appointment {
    
    /**
     * 教练姓名
     */
    private String coachName;
    
    /**
     * 学员姓名
     */
    private String studentName;
    
    /**
     * 球台编号
     */
    private String tableNumber;
    
    /**
     * 校区名称
     */
    private String campusName;
    
    /**
     * 教练级别
     */
    private Integer coachLevel;
    
    // Getter and Setter methods
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
    
    public String getTableNumber() {
        return tableNumber;
    }
    
    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }
    
    public String getCampusName() {
        return campusName;
    }
    
    public void setCampusName(String campusName) {
        this.campusName = campusName;
    }
    
    public Integer getCoachLevel() {
        return coachLevel;
    }
    
    public void setCoachLevel(Integer coachLevel) {
        this.coachLevel = coachLevel;
    }
    
    @Override
    public String toString() {
        return "AppointmentVO{" +
                "id=" + getId() +
                ", coachId=" + getCoachId() +
                ", studentId=" + getStudentId() +
                ", tableId=" + getTableId() +
                ", startTime=" + getStartTime() +
                ", endTime=" + getEndTime() +
                ", cost=" + getCost() +
                ", status=" + getStatus() +
                ", createTime=" + getCreateTime() +
                ", coachName='" + coachName + '\'' +
                ", studentName='" + studentName + '\'' +
                ", tableNumber='" + tableNumber + '\'' +
                ", campusName='" + campusName + '\'' +
                ", coachLevel=" + coachLevel +
                '}';
    }
}
