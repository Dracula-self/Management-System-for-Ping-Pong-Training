package com.quan.project.mapper;

import com.quan.project.dto.PageDTO;
import com.quan.project.entity.SystemMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统消息数据访问层
 * 简化版本：系统消息现在是面向所有人的公告
 */
@Mapper
public interface SystemMessageMapper {
    
    /**
     * 插入系统消息
     */
    int insert(SystemMessage message);
    
    /**
     * 根据ID更新系统消息
     */
    int updateById(SystemMessage message);
    
    /**
     * 根据ID删除系统消息
     */
    int deleteById(Integer id);
    
    /**
     * 根据ID查询系统消息
     */
    SystemMessage selectById(Integer id);
    
    /**
     * 分页查询系统消息（支持动态条件）
     */
    List<SystemMessage> selectPageByConditions(@Param("request") PageDTO pageRequest);
    
    /**
     * 根据状态查询消息
     */
    List<SystemMessage> selectByStatus(Integer status);
    
    /**
     * 根据类型查询消息
     */
    List<SystemMessage> selectByType(Integer type);
    
    /**
     * 删除已归档消息（超过指定天数）
     */
    int deleteArchivedMessages(@Param("days") Integer days);
}