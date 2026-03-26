package com.quan.project.common;

/**
 * 用户上下文
 * 基于ThreadLocal实现，用于在当前线程中存储和获取当前登录用户信息
 * 
 * @author quan
 */
public class UserContext {
    
    /**
     * 存储当前用户ID的ThreadLocal
     */
    private static final ThreadLocal<Integer> USER_ID = new ThreadLocal<>();
    
    /**
     * 存储当前用户名的ThreadLocal
     */
    private static final ThreadLocal<String> USERNAME = new ThreadLocal<>();
    
    /**
     * 存储当前用户角色的ThreadLocal
     */
    private static final ThreadLocal<Integer> USER_ROLE = new ThreadLocal<>();
    
    /**
     * 设置当前用户ID
     */
    public static void setUserId(Integer userId) {
        USER_ID.set(userId);
    }
    
    /**
     * 获取当前用户ID
     */
    public static Integer getUserId() {
        return USER_ID.get();
    }
    
    /**
     * 设置当前用户名
     */
    public static void setUsername(String username) {
        USERNAME.set(username);
    }
    
    /**
     * 获取当前用户名
     */
    public static String getUsername() {
        return USERNAME.get();
    }
    
    /**
     * 设置当前用户角色
     */
    public static void setUserRole(Integer userRole) {
        USER_ROLE.set(userRole);
    }
    
    /**
     * 获取当前用户角色
     */
    public static Integer getUserRole() {
        return USER_ROLE.get();
    }
    
    /**
     * 清除当前线程中存储的用户信息
     * 在请求结束时调用，防止内存泄漏
     */
    public static void clear() {
        USER_ID.remove();
        USERNAME.remove();
        USER_ROLE.remove();
    }
} 