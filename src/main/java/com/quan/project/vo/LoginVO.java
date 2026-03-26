package com.quan.project.vo;

import com.quan.project.entity.User;

/**
 * 登录响应VO
 * 用于返回登录成功后的用户信息和token
 */
public class LoginVO {
    
    /**
     * 用户信息
     */
    private User user;
    
    /**
     * JWT访问令牌
     */
    private String accessToken;
    
    /**
     * 令牌过期时间(毫秒)
     */
    private Long expiresIn;
    
    /**
     * 令牌类型
     */
    private String tokenType = "Bearer";
    
    public LoginVO() {
    }
    
    public LoginVO(User user, String accessToken, Long expiresIn) {
        this.user = user;
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public String getAccessToken() {
        return accessToken;
    }
    
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    
    public Long getExpiresIn() {
        return expiresIn;
    }
    
    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }
    
    public String getTokenType() {
        return tokenType;
    }
    
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
    
    @Override
    public String toString() {
        return "LoginResponseVO{" +
                "user=" + user +
                ", accessToken='" + accessToken + '\'' +
                ", expiresIn=" + expiresIn +
                ", tokenType='" + tokenType + '\'' +
                '}';
    }
} 