package com.quan.project.vo;

import java.io.Serializable;

/**
 * 验证码响应视图对象
 */
public class CaptchaVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 验证码图片（Base64格式）
     */
    private String captchaImage;
    
    /**
     * 验证码令牌（加密后）
     */
    private String captchaToken;
    
    /**
     * 时间戳
     */
    private Long timestamp;
    
    public CaptchaVO() {}
    
    public CaptchaVO(String captchaImage, String captchaToken, Long timestamp) {
        this.captchaImage = captchaImage;
        this.captchaToken = captchaToken;
        this.timestamp = timestamp;
    }
    
    public String getCaptchaImage() {
        return captchaImage;
    }
    
    public void setCaptchaImage(String captchaImage) {
        this.captchaImage = captchaImage;
    }
    
    public String getCaptchaToken() {
        return captchaToken;
    }
    
    public void setCaptchaToken(String captchaToken) {
        this.captchaToken = captchaToken;
    }
    
    public Long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    public String toString() {
        return "CaptchaVO{" +
                "captchaImage='" + (captchaImage != null ? captchaImage.substring(0, 50) + "..." : null) + '\'' +
                ", captchaToken='" + captchaToken + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
} 