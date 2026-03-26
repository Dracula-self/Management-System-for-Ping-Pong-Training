package com.quan.project.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.quan.project.common.CurrentUser;
import com.quan.project.dto.PageDTO;
import com.quan.project.entity.Table;
import com.quan.project.entity.User;
import com.quan.project.exception.BusinessException;
import com.quan.project.mapper.CampusMapper;
import com.quan.project.mapper.TableMapper;
import com.quan.project.mapper.UserMapper;
import com.quan.project.service.TableService;
import com.quan.project.vo.TableVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 球台服务实现类
 */
@Service
public class TableServiceImpl implements TableService {
    
    private static final Logger log = LoggerFactory.getLogger(TableServiceImpl.class);
    
    @Autowired
    private TableMapper tableMapper;
    
    @Autowired
    private CampusMapper campusMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public Table createTable(Table table) {
        try {
            // 验证校区存在
            if (campusMapper.selectById(table.getCampusId()) == null) {
                throw new BusinessException("校区不存在");
            }
            
            // 检查球台编号是否重复
            Table existingTable = tableMapper.selectByCampusIdAndTableNumber(
                table.getCampusId(), table.getTableNumber());
            if (existingTable != null) {
                throw new BusinessException("该校区已存在相同编号的球台");
            }
            
            int result = tableMapper.insert(table);
            if (result <= 0) {
                throw new BusinessException("创建球台失败");
            }
            
            log.info("创建球台成功，球台ID: {}, 校区ID: {}, 编号: {}, 创建者: {}", 
                table.getId(), table.getCampusId(), table.getTableNumber(), CurrentUser.getCurrentUserId());
            return table;
            
        } catch (Exception e) {
            log.error("创建球台失败", e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("创建球台失败");
        }
    }
    
    @Override
    public void deleteTable(Integer id) {
        try {
            // 检查球台是否存在
            Table table = tableMapper.selectById(id);
            if (table == null) {
                throw new BusinessException("球台不存在");
            }
            
            // TODO: 检查球台是否有预约记录
            
            int result = tableMapper.deleteById(id);
            if (result <= 0) {
                throw new BusinessException("删除球台失败");
            }
            
            log.info("删除球台成功，球台ID: {}, 操作者: {}", id, CurrentUser.getCurrentUserId());
        } catch (Exception e) {
            log.error("删除球台失败，球台ID: {}", id, e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("删除球台失败");
        }
    }
    
    @Override
    public void updateTable(Table table) {
        try {
            // 检查球台是否存在
            Table existingTable = tableMapper.selectById(table.getId());
            if (existingTable == null) {
                throw new BusinessException("球台不存在");
            }
            
            // 如果修改了球台编号，检查新编号是否重复
            if (!existingTable.getTableNumber().equals(table.getTableNumber())) {
                Table duplicateTable = tableMapper.selectByCampusIdAndTableNumber(
                    table.getCampusId(), table.getTableNumber());
                if (duplicateTable != null && !duplicateTable.getId().equals(table.getId())) {
                    throw new BusinessException("该校区已存在相同编号的球台");
                }
            }
            
            int result = tableMapper.update(table);
            if (result <= 0) {
                throw new BusinessException("更新球台失败");
            }
            
            log.info("更新球台成功，球台ID: {}, 操作者: {}", table.getId(), CurrentUser.getCurrentUserId());
        } catch (Exception e) {
            log.error("更新球台失败，球台ID: {}", table.getId(), e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("更新球台失败");
        }
    }
    
    @Override
    public Table getTableById(Integer id) {
        try {
            Table table = tableMapper.selectById(id);
            if (table == null) {
                throw new BusinessException("球台不存在");
            }
            return table;
        } catch (Exception e) {
            log.error("查询球台失败，球台ID: {}", id, e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("查询球台失败");
        }
    }
    
    @Override
    public PageInfo<Table> getTablePage(PageDTO pageRequest) {
        try {
            // 根据当前用户角色进行数据过滤
            Integer currentUserId = CurrentUser.getCurrentUserId();
            Integer currentUserRole = CurrentUser.getCurrentUserRole();
            
            if (currentUserRole == 2) { // 校区管理员
                // 获取当前用户的校区信息
                User currentUser = userMapper.selectById(currentUserId);
                if (currentUser != null && currentUser.getCampusId() != null) {
                    // 校区管理员只能查看本校区的球台
                    pageRequest.addParam("campusId", currentUser.getCampusId());
                }
            }
            // 超级管理员(1)可以查看所有球台，不需要额外过滤
            
            PageHelper.startPage(pageRequest.getPageNum(), pageRequest.getPageSize());
            List<Table> tables = tableMapper.selectPageByConditions(pageRequest);
            return new PageInfo<>(tables);
        } catch (Exception e) {
            log.error("分页查询球台失败", e);
            throw new BusinessException("分页查询球台失败");
        }
    }
    
    @Override
    public PageInfo<TableVO> getTablePageWithInfo(PageDTO pageRequest) {
        try {
            // 根据当前用户角色进行数据过滤
            Integer currentUserId = CurrentUser.getCurrentUserId();
            Integer currentUserRole = CurrentUser.getCurrentUserRole();
            
            if (currentUserRole == 2) { // 校区管理员
                // 获取当前用户的校区信息
                User currentUser = userMapper.selectById(currentUserId);
                if (currentUser != null && currentUser.getCampusId() != null) {
                    // 校区管理员只能查看本校区的球台
                    pageRequest.addParam("campusId", currentUser.getCampusId());
                }
            }
            // 超级管理员(1)可以查看所有球台，不需要额外过滤
            
            PageHelper.startPage(pageRequest.getPageNum(), pageRequest.getPageSize());
            List<TableVO> tables = tableMapper.selectPageWithInfo(pageRequest);
            return new PageInfo<>(tables);
        } catch (Exception e) {
            log.error("分页查询球台（含详细信息）失败", e);
            throw new BusinessException("分页查询球台失败");
        }
    }
    
    @Override
    public List<Table> getTablesByCampusId(Integer campusId) {
        try {
            return tableMapper.selectByCampusId(campusId);
        } catch (Exception e) {
            log.error("查询校区球台失败，校区ID: {}", campusId, e);
            throw new BusinessException("查询校区球台失败");
        }
    }
    
    @Override
    public List<Table> getAvailableTables(Integer campusId, LocalDateTime startTime, LocalDateTime endTime) {
        try {
            return tableMapper.selectAvailableTablesByCampusAndTime(campusId, startTime, endTime);
        } catch (Exception e) {
            log.error("查询可用球台失败，校区ID: {}", campusId, e);
            throw new BusinessException("查询可用球台失败");
        }
    }
    
    @Override
    public int countTablesByCampusId(Integer campusId) {
        try {
            return tableMapper.countByCampusId(campusId);
        } catch (Exception e) {
            log.error("统计校区球台数量失败，校区ID: {}", campusId, e);
            return 0;
        }
    }
}
