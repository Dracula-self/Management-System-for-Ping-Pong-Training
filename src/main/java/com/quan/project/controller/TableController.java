package com.quan.project.controller;

import com.github.pagehelper.PageInfo;
import com.quan.project.common.R;
import com.quan.project.dto.PageDTO;
import com.quan.project.entity.Table;
import com.quan.project.service.TableService;
import com.quan.project.vo.TableVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 球台管理控制器
 * 基础路径: /api/tables
 */
@RestController
@RequestMapping("/api/tables")
public class TableController {
    
    private static final Logger log = LoggerFactory.getLogger(TableController.class);
    
    @Autowired
    private TableService tableService;
    
    /**
     * 10.1 查询球台列表 - POST /api/tables/search
     * 查询指定校区的球台列表（包含校区名称、状态、当前预约信息）
     */
    @PostMapping("/search")
    public R<PageInfo<TableVO>> search(@RequestBody PageDTO pageRequest) {
        log.debug("分页查询球台，参数: {}", pageRequest);
        PageInfo<TableVO> pageInfo = tableService.getTablePageWithInfo(pageRequest);
        return R.success(pageInfo);
    }
    
    /**
     * 10.2 创建球台 - POST /api/tables
     * 为校区添加球台
     */
    @PostMapping
    public R<Table> create(@RequestBody Table table) {
        log.debug("创建球台，参数: {}", table);
        Table createdTable = tableService.createTable(table);
        return R.success(createdTable);
    }
    
    /**
     * 10.3 删除球台 - DELETE /api/tables/{id}
     * 删除指定球台
     */
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Integer id) {
        log.debug("删除球台，ID: {}", id);
        tableService.deleteTable(id);
        return R.success();
    }
    
    /**
     * 获取球台详情 - GET /api/tables/{id}
     * 根据ID获取球台详情
     */
    @GetMapping("/{id}")
    public R<Table> getById(@PathVariable Integer id) {
        log.debug("查询球台详情，ID: {}", id);
        Table table = tableService.getTableById(id);
        return R.success(table);
    }
    
    /**
     * 更新球台信息 - PUT /api/tables/{id}
     * 更新指定球台信息
     */
    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Integer id, @RequestBody Table table) {
        log.debug("更新球台，ID: {}, 参数: {}", id, table);
        table.setId(id);
        tableService.updateTable(table);
        return R.success();
    }
    
    /**
     * 查询校区球台 - GET /api/tables/campus/{campusId}
     * 查询指定校区的所有球台
     */
    @GetMapping("/campus/{campusId}")
    public R<List<Table>> getByCampusId(@PathVariable Integer campusId) {
        log.debug("查询校区球台，校区ID: {}", campusId);
        List<Table> tables = tableService.getTablesByCampusId(campusId);
        return R.success(tables);
    }
    
    /**
     * 查询可用球台 - GET /api/tables/available
     * 查询指定校区在指定时间段的可用球台
     */
    @GetMapping("/available")
    public R<List<Table>> getAvailableTables(@RequestParam Integer campusId, 
                                           @RequestParam LocalDateTime startTime, 
                                           @RequestParam LocalDateTime endTime) {
        log.debug("查询可用球台，校区ID: {}, 开始时间: {}, 结束时间: {}", campusId, startTime, endTime);
        List<Table> tables = tableService.getAvailableTables(campusId, startTime, endTime);
        return R.success(tables);
    }
    
    /**
     * 统计校区球台数量 - GET /api/tables/campus/{campusId}/count
     * 统计指定校区的球台总数
     */
    @GetMapping("/campus/{campusId}/count")
    public R<Integer> countByCampusId(@PathVariable Integer campusId) {
        log.debug("统计校区球台数量，校区ID: {}", campusId);
        int count = tableService.countTablesByCampusId(campusId);
        return R.success(count);
    }
}
