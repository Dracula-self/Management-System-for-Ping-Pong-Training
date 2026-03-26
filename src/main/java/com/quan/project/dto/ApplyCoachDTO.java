package com.quan.project.dto;

/**
 * 申请教练请求对象
 */
public class ApplyCoachDTO {
    
    /**
     * 教练ID
     */
    private Integer coachId;
    
    public ApplyCoachDTO() {}
    
    public ApplyCoachDTO(Integer coachId) {
        this.coachId = coachId;
    }
    
    public Integer getCoachId() {
        return coachId;
    }
    
    public void setCoachId(Integer coachId) {
        this.coachId = coachId;
    }
    
    @Override
    public String toString() {
        return "ApplyCoachDTO{" +
                "coachId=" + coachId +
                '}';
    }
}
