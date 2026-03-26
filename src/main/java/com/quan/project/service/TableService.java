package com.quan.project.service;

import com.github.pagehelper.PageInfo;
import com.quan.project.dto.PageDTO;
import com.quan.project.entity.Table;
import com.quan.project.vo.TableVO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 球台服务接口
 */
public interface TableService {
    
    /**
     * 创建球台
     */
    Table createTable(Table table);
    
    /**
     * 删除球台
     */
    void deleteTable(Integer id);
    
    /**
     * 更新球台信息
     */
    void updateTable(Table table);
    
    /**
     * 根据ID查询球台
     */
    Table getTableById(Integer id);
    
    /**
     * 分页查询球台
     */
    PageInfo<Table> getTablePage(PageDTO pageRequest);
    
    /**
     * 分页查询球台（包含校区名称、状态、当前预约信息）
     */
    PageInfo<TableVO> getTablePageWithInfo(PageDTO pageRequest);
    
    /**
     * 根据校区ID查询球台
     */
    List<Table> getTablesByCampusId(Integer campusId);
    
    /**
     * 查询指定时间段的可用球台
     */
    List<Table> getAvailableTables(Integer campusId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 统计校区球台数量
     */
    int countTablesByCampusId(Integer campusId);
}
