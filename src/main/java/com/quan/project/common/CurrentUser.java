package com.quan.project.common;

/**
 * 当前登录用户信息
 * 用于封装当前登录用户的基本信息
 * 
 * @author quan
 */
public class CurrentUser {
    
    /**
     * 用户ID
     */
    private Integer userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 用户角色
     */
    private Integer userRole;
    
    /**
     * 构造方法
     */
    public CurrentUser(Integer userId, String username, Integer userRole) {
        this.userId = userId;
        this.username = username;
        this.userRole = userRole;
    }
    
    /**
     * 获取当前登录用户信息
     * 如果未登录，返回null
     */
    public static CurrentUser get() {
        Integer userId = UserContext.getUserId();
        String username = UserContext.getUsername();
        Integer userRole = UserContext.getUserRole();
        
        if (userId == null) {
            return null;
        }
        
        return new CurrentUser(userId, username, userRole);
    }
    
    /**
     * 获取当前登录用户ID
     * 如果未登录，返回null
     */
    public static Integer getCurrentUserId() {
        return UserContext.getUserId();
    }
    
    /**
     * 获取当前登录用户名
     * 如果未登录，返回null
     */
    public static String getCurrentUsername() {
        return UserContext.getUsername();
    }
    
    /**
     * 获取当前登录用户角色
     * 如果未登录，返回null
     */
    public static Integer getCurrentUserRole() {
        return UserContext.getUserRole();
    }
    
    /**
     * 判断当前是否已登录
     */
    public static boolean isAuthenticated() {
        return UserContext.getUserId() != null;
    }
    
    /**
     * 判断当前用户是否为管理员
     */
    public static boolean isAdmin() {
        Integer role = UserContext.getUserRole();
        // 假设1是管理员角色
        return role != null && role == 1;
    }
    
    // Getter方法
    public Integer getUserId() {
        return userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public Integer getUserRole() {
        return userRole;
    }
    
    @Override
    public String toString() {
        return "CurrentUser{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", userRole=" + userRole +
                '}';
    }
} 