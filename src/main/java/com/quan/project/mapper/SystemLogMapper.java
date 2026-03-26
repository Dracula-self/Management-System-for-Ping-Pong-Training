package com.quan.project.mapper;

import com.quan.project.dto.PageDTO;
import com.quan.project.entity.SystemLog;
import com.quan.project.vo.SystemLogVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统日志数据访问层
 */
@Mapper
public interface SystemLogMapper {
    
    /**
     * 插入系统日志
     */
    int insert(SystemLog log);
    
    /**
     * 根据ID删除系统日志
     */
    int deleteById(Integer id);
    
    /**
     * 根据ID查询系统日志
     */
    SystemLog selectById(Integer id);
    
    /**
     * 分页查询系统日志（支持动态条件）
     */
    List<SystemLog> selectPageByConditions(@Param("request") PageDTO pageRequest);
    
    /**
     * 分页查询系统日志VO（包含用户信息）
     */
    List<SystemLogVO> selectPageVOByConditions(@Param("request") PageDTO pageRequest);
    
    /**
     * 根据用户ID查询操作日志
     */
    List<SystemLog> selectByUserId(Integer userId);
    
    /**
     * 删除过期日志（超过指定天数）
     */
    int deleteExpiredLogs(@Param("days") Integer days);
    
    /**
     * 统计用户操作次数
     */
    int countByUserId(Integer userId);
}
