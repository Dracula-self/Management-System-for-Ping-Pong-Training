package com.quan.project.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.quan.project.dto.PageDTO;
import com.quan.project.entity.SystemMessage;
import com.quan.project.exception.BusinessException;
import com.quan.project.mapper.SystemMessageMapper;
import com.quan.project.service.SystemMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 系统消息服务实现类
 * 简化版本：系统消息现在是面向所有人的公告
 */
@Service
public class SystemMessageServiceImpl implements SystemMessageService {
    
    private static final Logger log = LoggerFactory.getLogger(SystemMessageServiceImpl.class);
    
    @Autowired
    private SystemMessageMapper messageMapper;
    
    @Override
    @Transactional
    public SystemMessage create(SystemMessage message) {
        try {
            // 验证必填字段
            if (message.getTitle() == null || message.getTitle().trim().isEmpty()) {
                throw new BusinessException("消息标题不能为空");
            }
            if (message.getContent() == null || message.getContent().trim().isEmpty()) {
                throw new BusinessException("消息内容不能为空");
            }
            if (message.getType() == null) {
                message.setType(SystemMessage.TYPE_SYSTEM_NOTICE);
            }
            if (message.getStatus() == null) {
                message.setStatus(SystemMessage.STATUS_PUBLISHED);
            }
            
            int result = messageMapper.insert(message);
            if (result <= 0) {
                throw new BusinessException("创建消息失败");
            }
            
            log.info("创建系统消息成功，消息ID: {}, 标题: {}", message.getId(), message.getTitle());
            return message;
            
        } catch (Exception e) {
            log.error("创建系统消息失败，标题: {}", message != null ? message.getTitle() : "null", e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("创建消息失败");
        }
    }
    
    @Override
    @Transactional
    public SystemMessage update(SystemMessage message) {
        try {
            if (message.getId() == null) {
                throw new BusinessException("消息ID不能为空");
            }
            
            SystemMessage existingMessage = messageMapper.selectById(message.getId());
            if (existingMessage == null) {
                throw new BusinessException("消息不存在");
            }
            
            // 验证必填字段
            if (message.getTitle() == null || message.getTitle().trim().isEmpty()) {
                throw new BusinessException("消息标题不能为空");
            }
            if (message.getContent() == null || message.getContent().trim().isEmpty()) {
                throw new BusinessException("消息内容不能为空");
            }
            
            int result = messageMapper.updateById(message);
            if (result <= 0) {
                throw new BusinessException("更新消息失败");
            }
            
            log.info("更新系统消息成功，消息ID: {}, 标题: {}", message.getId(), message.getTitle());
            return messageMapper.selectById(message.getId());
            
        } catch (Exception e) {
            log.error("更新系统消息失败，消息ID: {}", message != null ? message.getId() : "null", e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("更新消息失败");
        }
    }
    
    @Override
    @Transactional
    public void deleteById(Integer id) {
        try {
            if (id == null) {
                throw new BusinessException("消息ID不能为空");
            }
            
            SystemMessage message = messageMapper.selectById(id);
            if (message == null) {
                throw new BusinessException("消息不存在");
            }
            
            int result = messageMapper.deleteById(id);
            if (result <= 0) {
                throw new BusinessException("删除消息失败");
            }
            
            log.info("删除系统消息成功，消息ID: {}, 标题: {}", id, message.getTitle());
            
        } catch (Exception e) {
            log.error("删除系统消息失败，消息ID: {}", id, e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("删除消息失败");
        }
    }
    
    @Override
    public SystemMessage getById(Integer id) {
        try {
            if (id == null) {
                throw new BusinessException("消息ID不能为空");
            }
            
            SystemMessage message = messageMapper.selectById(id);
            if (message == null) {
                throw new BusinessException("消息不存在");
            }
            
            return message;
            
        } catch (Exception e) {
            log.error("查询系统消息失败，消息ID: {}", id, e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("查询消息失败");
        }
    }
    
    @Override
    public PageInfo<SystemMessage> search(PageDTO pageRequest) {
        try {
            PageHelper.startPage(pageRequest.getPageNum(), pageRequest.getPageSize());
            List<SystemMessage> messages = messageMapper.selectPageByConditions(pageRequest);
            return new PageInfo<>(messages);
        } catch (Exception e) {
            log.error("分页查询系统消息失败", e);
            throw new BusinessException("查询消息列表失败");
        }
    }
    
    @Override
    public List<SystemMessage> getPublishedMessages() {
        try {
            return messageMapper.selectByStatus(SystemMessage.STATUS_PUBLISHED);
        } catch (Exception e) {
            log.error("查询已发布消息失败", e);
            throw new BusinessException("查询已发布消息失败");
        }
    }
    
    @Override
    public List<SystemMessage> getMessagesByType(Integer type) {
        try {
            if (type == null) {
                throw new BusinessException("消息类型不能为空");
            }
            return messageMapper.selectByType(type);
        } catch (Exception e) {
            log.error("按类型查询消息失败，类型: {}", type, e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("按类型查询消息失败");
        }
    }
    
    @Override
    @Transactional
    public void publishMessage(Integer id) {
        try {
            if (id == null) {
                throw new BusinessException("消息ID不能为空");
            }
            
            SystemMessage message = messageMapper.selectById(id);
            if (message == null) {
                throw new BusinessException("消息不存在");
            }
            
            message.setStatus(SystemMessage.STATUS_PUBLISHED);
            int result = messageMapper.updateById(message);
            if (result <= 0) {
                throw new BusinessException("发布消息失败");
            }
            
            log.info("发布系统消息成功，消息ID: {}, 标题: {}", id, message.getTitle());
            
        } catch (Exception e) {
            log.error("发布系统消息失败，消息ID: {}", id, e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("发布消息失败");
        }
    }
    
    @Override
    @Transactional
    public void archiveMessage(Integer id) {
        try {
            if (id == null) {
                throw new BusinessException("消息ID不能为空");
            }
            
            SystemMessage message = messageMapper.selectById(id);
            if (message == null) {
                throw new BusinessException("消息不存在");
            }
            
            message.setStatus(SystemMessage.STATUS_ARCHIVED);
            int result = messageMapper.updateById(message);
            if (result <= 0) {
                throw new BusinessException("归档消息失败");
            }
            
            log.info("归档系统消息成功，消息ID: {}, 标题: {}", id, message.getTitle());
            
        } catch (Exception e) {
            log.error("归档系统消息失败，消息ID: {}", id, e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("归档消息失败");
        }
    }
    
    @Override
    @Transactional
    public int cleanArchivedMessages(Integer days) {
        try {
            int deletedCount = messageMapper.deleteArchivedMessages(days != null ? days : 30);
            log.info("清理已归档消息成功，删除数量: {}, 归档天数: {}", deletedCount, days);
            return deletedCount;
        } catch (Exception e) {
            log.error("清理已归档消息失败", e);
            return 0;
        }
    }
}