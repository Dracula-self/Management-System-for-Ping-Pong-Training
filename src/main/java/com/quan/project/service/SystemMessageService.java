package com.quan.project.service;

import com.github.pagehelper.PageInfo;
import com.quan.project.dto.PageDTO;
import com.quan.project.entity.SystemMessage;

import java.util.List;

/**
 * 系统消息服务接口
 * 简化版本：系统消息现在是面向所有人的公告
 */
public interface SystemMessageService {
    
    /**
     * 创建系统消息
     */
    SystemMessage create(SystemMessage message);
    
    /**
     * 更新系统消息
     */
    SystemMessage update(SystemMessage message);
    
    /**
     * 删除系统消息
     */
    void deleteById(Integer id);
    
    /**
     * 根据ID查询消息
     */
    SystemMessage getById(Integer id);
    
    /**
     * 分页查询消息列表
     */
    PageInfo<SystemMessage> search(PageDTO pageRequest);
    
    /**
     * 查询已发布的消息
     */
    List<SystemMessage> getPublishedMessages();
    
    /**
     * 根据类型查询消息
     */
    List<SystemMessage> getMessagesByType(Integer type);
    
    /**
     * 发布消息（将草稿状态改为已发布）
     */
    void publishMessage(Integer id);
    
    /**
     * 归档消息（将已发布状态改为已归档）
     */
    void archiveMessage(Integer id);
    
    /**
     * 清理已归档的消息
     */
    int cleanArchivedMessages(Integer days);
}