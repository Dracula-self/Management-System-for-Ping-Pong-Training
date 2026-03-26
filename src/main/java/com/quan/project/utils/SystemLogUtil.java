package com.quan.project.utils;

import com.quan.project.common.CurrentUser;
import com.quan.project.service.SystemLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 系统日志记录工具类
 * 简化日志记录操作
 */
@Component
public class SystemLogUtil {
    
    private static final Logger log = LoggerFactory.getLogger(SystemLogUtil.class);
    
    @Autowired
    private SystemLogService systemLogService;
    
    /**
     * 记录当前用户操作日志
     * 
     * @param action 操作类型
     * @param details 操作详情
     */
    public void logCurrentUserAction(String action, String details) {
        try {
            Integer currentUserId = getCurrentUserIdSafely();
            systemLogService.createLog(currentUserId, action, details);
        } catch (Exception e) {
            log.warn("记录用户操作日志失败: action={}, details={}", action, details, e);
        }
    }
    
    /**
     * 记录指定用户操作日志
     * 
     * @param userId 用户ID
     * @param action 操作类型
     * @param details 操作详情
     */
    public void logUserAction(Integer userId, String action, String details) {
        try {
            systemLogService.createLog(userId, action, details);
        } catch (Exception e) {
            log.warn("记录用户操作日志失败: userId={}, action={}, details={}", userId, action, details, e);
        }
    }
    
    /**
     * 异步记录当前用户操作日志
     * 
     * @param action 操作类型
     * @param details 操作详情
     */
    public void logCurrentUserActionAsync(String action, String details) {
        new Thread(() -> logCurrentUserAction(action, details)).start();
    }
    
    /**
     * 异步记录指定用户操作日志
     * 
     * @param userId 用户ID
     * @param action 操作类型
     * @param details 操作详情
     */
    public void logUserActionAsync(Integer userId, String action, String details) {
        new Thread(() -> logUserAction(userId, action, details)).start();
    }
    
    /**
     * 安全获取当前用户ID
     */
    private Integer getCurrentUserIdSafely() {
        try {
            return CurrentUser.getCurrentUserId();
        } catch (Exception e) {
            return null; // 未登录或获取失败
        }
    }
}
