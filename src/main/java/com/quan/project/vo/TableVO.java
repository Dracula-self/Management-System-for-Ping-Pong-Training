package com.quan.project.vo;

import com.quan.project.entity.Table;

/**
 * 球台视图对象，包含关联的校区和预约信息
 */
public class TableVO extends Table {
    
    /**
     * 校区名称
     */
    private String campusName;
    
    /**
     * 球台状态: available-空闲, busy-使用中, maintenance-维护中
     */
    private String status;
    
    /**
     * 当前预约信息
     */
    private CurrentAppointmentInfo currentAppointment;
    
    /**
     * 当前预约信息内部类
     */
    public static class CurrentAppointmentInfo {
        private Integer appointmentId;
        private String studentName;
        private String coachName;
        private String startTime;
        private String endTime;
        private String cost;
        
        public CurrentAppointmentInfo() {}
        
        public CurrentAppointmentInfo(Integer appointmentId, String studentName, String coachName, 
                                    String startTime, String endTime, String cost) {
            this.appointmentId = appointmentId;
            this.studentName = studentName;
            this.coachName = coachName;
            this.startTime = startTime;
            this.endTime = endTime;
            this.cost = cost;
        }
        
        // Getter and Setter methods
        public Integer getAppointmentId() {
            return appointmentId;
        }
        
        public void setAppointmentId(Integer appointmentId) {
            this.appointmentId = appointmentId;
        }
        
        public String getStudentName() {
            return studentName;
        }
        
        public void setStudentName(String studentName) {
            this.studentName = studentName;
        }
        
        public String getCoachName() {
            return coachName;
        }
        
        public void setCoachName(String coachName) {
            this.coachName = coachName;
        }
        
        public String getStartTime() {
            return startTime;
        }
        
        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }
        
        public String getEndTime() {
            return endTime;
        }
        
        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }
        
        public String getCost() {
            return cost;
        }
        
        public void setCost(String cost) {
            this.cost = cost;
        }
        
        @Override
        public String toString() {
            return "CurrentAppointmentInfo{" +
                    "appointmentId=" + appointmentId +
                    ", studentName='" + studentName + '\'' +
                    ", coachName='" + coachName + '\'' +
                    ", startTime='" + startTime + '\'' +
                    ", endTime='" + endTime + '\'' +
                    ", cost='" + cost + '\'' +
                    '}';
        }
    }
    
    // Getter and Setter methods
    public String getCampusName() {
        return campusName;
    }
    
    public void setCampusName(String campusName) {
        this.campusName = campusName;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public CurrentAppointmentInfo getCurrentAppointment() {
        return currentAppointment;
    }
    
    public void setCurrentAppointment(CurrentAppointmentInfo currentAppointment) {
        this.currentAppointment = currentAppointment;
    }
    
    @Override
    public String toString() {
        return "TableVO{" +
                "id=" + getId() +
                ", campusId=" + getCampusId() +
                ", tableNumber='" + getTableNumber() + '\'' +
                ", campusName='" + campusName + '\'' +
                ", status='" + status + '\'' +
                ", currentAppointment=" + currentAppointment +
                '}';
    }
}
