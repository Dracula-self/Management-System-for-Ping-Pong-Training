package com.quan.project.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.quan.project.dto.PageDTO;
import com.quan.project.entity.SystemLog;
import com.quan.project.exception.BusinessException;
import com.quan.project.mapper.SystemLogMapper;
import com.quan.project.service.SystemLogService;
import com.quan.project.vo.SystemLogVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统日志服务实现类
 */
@Service
public class SystemLogServiceImpl implements SystemLogService {
    
    private static final Logger log = LoggerFactory.getLogger(SystemLogServiceImpl.class);
    
    @Autowired
    private SystemLogMapper systemLogMapper;
    
    @Override
    public void createLog(Integer userId, String action, String details) {
        try {
            SystemLog systemLog = new SystemLog();
            systemLog.setUserId(userId);
            systemLog.setAction(action);
            systemLog.setDetails(details);
            systemLog.setLogTime(LocalDateTime.now());
            
            int result = systemLogMapper.insert(systemLog);
            if (result <= 0) {
                log.warn("创建系统日志失败: userId={}, action={}", userId, action);
            } else {
                log.debug("创建系统日志成功: userId={}, action={}", userId, action);
            }
        } catch (Exception e) {
            log.error("创建系统日志异常: userId={}, action={}", userId, action, e);
        }
    }
    
    @Override
    public SystemLog create(SystemLog systemLog) {
        if (systemLog == null) {
            throw new BusinessException("系统日志对象不能为空");
        }
        
        if (systemLog.getLogTime() == null) {
            systemLog.setLogTime(LocalDateTime.now());
        }
        
        int result = systemLogMapper.insert(systemLog);
        if (result <= 0) {
            throw new BusinessException("创建系统日志失败");
        }
        
        log.info("创建系统日志成功，日志ID: {}, 用户ID: {}, 操作: {}", 
            systemLog.getId(), systemLog.getUserId(), systemLog.getAction());
        return systemLog;
    }
    
    @Override
    public void deleteById(Integer id) {
        if (id == null || id <= 0) {
            throw new BusinessException("日志ID不能为空或无效");
        }
        
        SystemLog existingLog = systemLogMapper.selectById(id);
        if (existingLog == null) {
            throw new BusinessException("系统日志不存在");
        }
        
        int result = systemLogMapper.deleteById(id);
        if (result <= 0) {
            throw new BusinessException("删除系统日志失败");
        }
        
        log.info("删除系统日志成功，日志ID: {}", id);
    }
    
    @Override
    public SystemLog getById(Integer id) {
        if (id == null || id <= 0) {
            throw new BusinessException("日志ID不能为空或无效");
        }
        
        SystemLog systemLog = systemLogMapper.selectById(id);
        if (systemLog == null) {
            throw new BusinessException("系统日志不存在");
        }
        
        return systemLog;
    }
    
    @Override
    public PageInfo<SystemLog> search(PageDTO pageDTO) {
        if (pageDTO == null) {
            throw new BusinessException("分页参数不能为空");
        }
        
        // 设置默认分页参数
        Integer pageNum = pageDTO.getPageNum() != null ? pageDTO.getPageNum() : 1;
        Integer pageSize = pageDTO.getPageSize() != null ? pageDTO.getPageSize() : 10;
        
        PageHelper.startPage(pageNum, pageSize);
        List<SystemLog> logs = systemLogMapper.selectPageByConditions(pageDTO);
        
        return new PageInfo<>(logs);
    }
    
    @Override
    public PageInfo<SystemLogVO> searchVO(PageDTO pageDTO) {
        if (pageDTO == null) {
            throw new BusinessException("分页参数不能为空");
        }
        
        // 设置默认分页参数
        Integer pageNum = pageDTO.getPageNum() != null ? pageDTO.getPageNum() : 1;
        Integer pageSize = pageDTO.getPageSize() != null ? pageDTO.getPageSize() : 10;
        
        PageHelper.startPage(pageNum, pageSize);
        List<SystemLogVO> logs = systemLogMapper.selectPageVOByConditions(pageDTO);
        
        return new PageInfo<>(logs);
    }
    
    @Override
    public List<SystemLog> getByUserId(Integer userId) {
        if (userId == null || userId <= 0) {
            throw new BusinessException("用户ID不能为空或无效");
        }
        
        return systemLogMapper.selectByUserId(userId);
    }
    
    @Override
    public int cleanExpiredLogs(Integer days) {
        if (days == null || days <= 0) {
            days = 30; // 默认清理30天前的日志
        }
        
        int deletedCount = systemLogMapper.deleteExpiredLogs(days);
        log.info("清理过期日志完成，删除了 {} 条记录", deletedCount);
        
        return deletedCount;
    }
    
    @Override
    public int countByUserId(Integer userId) {
        if (userId == null || userId <= 0) {
            throw new BusinessException("用户ID不能为空或无效");
        }
        
        return systemLogMapper.countByUserId(userId);
    }
}
