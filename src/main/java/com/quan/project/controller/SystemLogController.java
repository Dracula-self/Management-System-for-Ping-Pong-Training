package com.quan.project.controller;

import com.github.pagehelper.PageInfo;
import com.quan.project.common.R;
import com.quan.project.dto.PageDTO;
import com.quan.project.entity.SystemLog;
import com.quan.project.service.SystemLogService;
import com.quan.project.vo.SystemLogVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统日志管理控制器
 */
@RestController
@RequestMapping("/api/system-logs")
public class SystemLogController {
    
    private static final Logger log = LoggerFactory.getLogger(SystemLogController.class);
    
    @Autowired
    private SystemLogService systemLogService;
    
    /**
     * 分页搜索系统日志（包含用户信息）
     */
    @PostMapping("/search")
    public R<PageInfo<SystemLogVO>> search(@RequestBody PageDTO pageDTO) {
        try {
            PageInfo<SystemLogVO> pageInfo = systemLogService.searchVO(pageDTO);
            return R.success(pageInfo);
        } catch (Exception e) {
            log.error("搜索系统日志失败", e);
            return R.error("搜索系统日志失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取系统日志
     */
    @GetMapping("/{id}")
    public R<SystemLog> getById(@PathVariable Integer id) {
        try {
            SystemLog systemLog = systemLogService.getById(id);
            return R.success(systemLog);
        } catch (Exception e) {
            log.error("获取系统日志失败，ID: {}", id, e);
            return R.error("获取系统日志失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除系统日志
     */
    @DeleteMapping("/{id}")
    public R<Void> deleteById(@PathVariable Integer id) {
        try {
            systemLogService.deleteById(id);
            return R.success();
        } catch (Exception e) {
            log.error("删除系统日志失败，ID: {}", id, e);
            return R.error("删除系统日志失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据用户ID获取操作日志
     */
    @GetMapping("/user/{userId}")
    public R<List<SystemLog>> getByUserId(@PathVariable Integer userId) {
        try {
            List<SystemLog> logs = systemLogService.getByUserId(userId);
            return R.success(logs);
        } catch (Exception e) {
            log.error("获取用户操作日志失败，用户ID: {}", userId, e);
            return R.error("获取用户操作日志失败: " + e.getMessage());
        }
    }
    
    /**
     * 清理过期日志
     */
    @DeleteMapping("/clean")
    public R<Integer> cleanExpiredLogs(@RequestParam(defaultValue = "30") Integer days) {
        try {
            int deletedCount = systemLogService.cleanExpiredLogs(days);
            return R.success(deletedCount);
        } catch (Exception e) {
            log.error("清理过期日志失败，天数: {}", days, e);
            return R.error("清理过期日志失败: " + e.getMessage());
        }
    }
    
    /**
     * 统计用户操作次数
     */
    @GetMapping("/count/{userId}")
    public R<Integer> countByUserId(@PathVariable Integer userId) {
        try {
            int count = systemLogService.countByUserId(userId);
            return R.success(count);
        } catch (Exception e) {
            log.error("统计用户操作次数失败，用户ID: {}", userId, e);
            return R.error("统计用户操作次数失败: " + e.getMessage());
        }
    }
}
