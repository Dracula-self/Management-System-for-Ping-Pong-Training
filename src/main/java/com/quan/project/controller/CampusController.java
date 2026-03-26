package com.quan.project.controller;

import com.github.pagehelper.PageInfo;
import com.quan.project.common.R;
import com.quan.project.dto.PageDTO;
import com.quan.project.entity.Campus;
import com.quan.project.service.CampusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 校区管理控制器
 * 基础路径: /api/campus
 */
@RestController
@RequestMapping("/api/campus")
public class CampusController {
    
    private static final Logger log = LoggerFactory.getLogger(CampusController.class);
    
    @Autowired
    private CampusService campusService;
    
    /**
     * 2.1 校区分页查询 - POST /api/campus/search
     * 分页查询校区列表
     */
    @PostMapping("/search")
    public R<PageInfo<Campus>> search(@RequestBody PageDTO pageRequest) {
        log.debug("分页查询校区，参数: {}", pageRequest);
        PageInfo<Campus> pageInfo = campusService.getCampusPage(pageRequest);
        return R.success(pageInfo);
    }
    
    /**
     * 2.2 创建校区 - POST /api/campus
     * 创建新校区
     */
    @PostMapping
    public R<Campus> create(@RequestBody Campus campus) {
        log.debug("创建校区，参数: {}", campus);
        Campus createdCampus = campusService.createCampus(campus);
        return R.success(createdCampus);
    }
    
    /**
     * 2.3 更新校区信息 - PUT /api/campus/{id}
     * 更新指定校区信息
     */
    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Integer id, @RequestBody Campus campus) {
        log.debug("更新校区，ID: {}, 参数: {}", id, campus);
        campus.setId(id);
        campusService.updateCampus(campus);
        return R.success();
    }
    
    /**
     * 2.4 删除校区 - DELETE /api/campus/{id}
     * 删除指定校区
     */
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Integer id) {
        log.debug("删除校区，ID: {}", id);
        campusService.deleteCampus(id);
        return R.success();
    }
    
    /**
     * 2.5 获取校区详情 - GET /api/campus/{id}
     * 根据ID获取校区信息
     */
    @GetMapping("/{id}")
    public R<Campus> getById(@PathVariable Integer id) {
        log.debug("查询校区详情，ID: {}", id);
        Campus campus = campusService.getCampusById(id);
        return R.success(campus);
    }
    
    /**
     * 获取所有校区列表 - GET /api/campus/all
     * 用于下拉选择等场景
     */
    @GetMapping("/all")
    public R<List<Campus>> getAll() {
        log.debug("查询所有校区");
        List<Campus> campuses = campusService.getAllCampuses();
        return R.success(campuses);
    }
}
