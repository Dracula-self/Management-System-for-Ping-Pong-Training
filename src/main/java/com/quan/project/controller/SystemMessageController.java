package com.quan.project.controller;

import com.github.pagehelper.PageInfo;
import com.quan.project.common.CurrentUser;
import com.quan.project.common.R;
import com.quan.project.dto.PageDTO;
import com.quan.project.entity.SystemMessage;
import com.quan.project.service.SystemLogService;
import com.quan.project.service.SystemMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统消息控制器
 */
@RestController
@RequestMapping("/api/system-messages")
public class SystemMessageController {
    
    private static final Logger log = LoggerFactory.getLogger(SystemMessageController.class);
    
    @Autowired
    private SystemMessageService systemMessageService;
    
    @Autowired
    private SystemLogService systemLogService;
    
    /**
     * 创建系统消息
     */
    @PostMapping
    public R<SystemMessage> create(@RequestBody SystemMessage message) {
        try {
            SystemMessage result = systemMessageService.create(message);
            
            // 记录创建系统消息日志
            try {
                Integer currentUserId = CurrentUser.getCurrentUserId();
                systemLogService.createLog(currentUserId, "创建系统消息", 
                    String.format("创建系统消息【%s】，类型：%d", result.getTitle(), result.getType()));
            } catch (Exception logEx) {
                // 日志记录失败不影响主业务
            }
            
            return R.success(result);
        } catch (Exception e) {
            log.error("创建系统消息失败", e);
            return R.error("创建消息失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新系统消息
     */
    @PutMapping("/{id}")
    public R<SystemMessage> update(@PathVariable Integer id, @RequestBody SystemMessage message) {
        try {
            message.setId(id);
            SystemMessage result = systemMessageService.update(message);
            return R.success(result);
        } catch (Exception e) {
            log.error("更新系统消息失败，ID: {}", id, e);
            return R.error("更新消息失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除系统消息
     */
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Integer id) {
        try {
            systemMessageService.deleteById(id);
            return R.success();
        } catch (Exception e) {
            log.error("删除系统消息失败，ID: {}", id, e);
            return R.error("删除消息失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据ID查询系统消息
     */
    @GetMapping("/{id}")
    public R<SystemMessage> getById(@PathVariable Integer id) {
        try {
            SystemMessage message = systemMessageService.getById(id);
            return R.success(message);
        } catch (Exception e) {
            log.error("查询系统消息失败，ID: {}", id, e);
            return R.error("查询消息失败：" + e.getMessage());
        }
    }
    
    /**
     * 分页查询系统消息
     */
    @PostMapping("/search")
    public R<PageInfo<SystemMessage>> search(@RequestBody PageDTO pageRequest) {
        try {
            PageInfo<SystemMessage> result = systemMessageService.search(pageRequest);
            return R.success(result);
        } catch (Exception e) {
            log.error("分页查询系统消息失败", e);
            return R.error("查询消息列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 查询已发布的消息（前台用户查看）
     */
    @GetMapping("/published")
    public R<List<SystemMessage>> getPublishedMessages() {
        try {
            List<SystemMessage> messages = systemMessageService.getPublishedMessages();
            return R.success(messages);
        } catch (Exception e) {
            log.error("查询已发布消息失败", e);
            return R.error("查询已发布消息失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据类型查询消息
     */
    @GetMapping("/type/{type}")
    public R<List<SystemMessage>> getMessagesByType(@PathVariable Integer type) {
        try {
            List<SystemMessage> messages = systemMessageService.getMessagesByType(type);
            return R.success(messages);
        } catch (Exception e) {
            log.error("根据类型查询消息失败，类型: {}", type, e);
            return R.error("查询消息失败：" + e.getMessage());
        }
    }
    
    /**
     * 发布消息
     */
    @PutMapping("/{id}/publish")
    public R<Void> publish(@PathVariable Integer id) {
        try {
            systemMessageService.publishMessage(id);
            return R.success();
        } catch (Exception e) {
            log.error("发布消息失败，ID: {}", id, e);
            return R.error("发布消息失败：" + e.getMessage());
        }
    }
    
    /**
     * 归档消息
     */
    @PutMapping("/{id}/archive")
    public R<Void> archive(@PathVariable Integer id) {
        try {
            systemMessageService.archiveMessage(id);
            return R.success();
        } catch (Exception e) {
            log.error("归档消息失败，ID: {}", id, e);
            return R.error("归档消息失败：" + e.getMessage());
        }
    }
    
    /**
     * 清理已归档的消息
     */
    @DeleteMapping("/clean")
    public R<Integer> cleanArchivedMessages(@RequestParam(defaultValue = "30") Integer days) {
        try {
            int deletedCount = systemMessageService.cleanArchivedMessages(days);
            return R.success(deletedCount);
        } catch (Exception e) {
            log.error("清理已归档消息失败", e);
            return R.error("清理消息失败：" + e.getMessage());
        }
    }
}