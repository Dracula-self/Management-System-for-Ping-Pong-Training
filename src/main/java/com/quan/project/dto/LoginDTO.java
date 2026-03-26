package com.quan.project.dto;

/**
 * 登录请求DTO
 */
public class LoginDTO {
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 密码
     */
    private String password;
    
    /**
     * 验证码
     */
    private String captcha;
    
    /**
     * 验证码令牌
     */
    private String captchaToken;
    
    /**
     * 获取用户名
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * 设置用户名
     */
    public void setUsername(String username) {
        this.username = username;
    }
    
    /**
     * 获取密码
     */
    public String getPassword() {
        return password;
    }
    
    /**
     * 设置密码
     */
    public void setPassword(String password) {
        this.password = password;
    }
    
    /**
     * 获取验证码
     */
    public String getCaptcha() {
        return captcha;
    }
    
    /**
     * 设置验证码
     */
    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }
    
    /**
     * 获取验证码令牌
     */
    public String getCaptchaToken() {
        return captchaToken;
    }
    
    /**
     * 设置验证码令牌
     */
    public void setCaptchaToken(String captchaToken) {
        this.captchaToken = captchaToken;
    }
} 