package com.quan.project.service;

import com.github.pagehelper.PageInfo;
import com.quan.project.dto.PageDTO;
import com.quan.project.entity.SystemLog;
import com.quan.project.vo.SystemLogVO;

import java.util.List;

/**
 * 系统日志服务接口
 */
public interface SystemLogService {
    
    /**
     * 创建系统日志
     */
    void createLog(Integer userId, String action, String details);
    
    /**
     * 创建系统日志（带SystemLog对象）
     */
    SystemLog create(SystemLog log);
    
    /**
     * 根据ID删除系统日志
     */
    void deleteById(Integer id);
    
    /**
     * 根据ID获取系统日志
     */
    SystemLog getById(Integer id);
    
    /**
     * 分页搜索系统日志
     */
    PageInfo<SystemLog> search(PageDTO pageDTO);
    
    /**
     * 分页搜索系统日志VO（包含用户信息）
     */
    PageInfo<SystemLogVO> searchVO(PageDTO pageDTO);
    
    /**
     * 根据用户ID获取操作日志
     */
    List<SystemLog> getByUserId(Integer userId);
    
    /**
     * 清理过期日志
     */
    int cleanExpiredLogs(Integer days);
    
    /**
     * 统计用户操作次数
     */
    int countByUserId(Integer userId);
}
