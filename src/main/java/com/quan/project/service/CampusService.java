package com.quan.project.service;

import com.github.pagehelper.PageInfo;
import com.quan.project.dto.PageDTO;
import com.quan.project.entity.Campus;

import java.util.List;

/**
 * 校区服务接口
 */
public interface CampusService {
    
    /**
     * 创建校区
     */
    Campus createCampus(Campus campus);
    
    /**
     * 根据ID删除校区
     */
    void deleteCampus(Integer id);
    
    /**
     * 更新校区信息
     */
    void updateCampus(Campus campus);
    
    /**
     * 根据ID查询校区
     */
    Campus getCampusById(Integer id);
    
    /**
     * 查询所有校区
     */
    List<Campus> getAllCampuses();
    
    /**
     * 分页查询校区
     */
    PageInfo<Campus> getCampusPage(PageDTO pageRequest);
    
    /**
     * 根据管理员ID查询校区
     */
    Campus getCampusByManagerId(Integer managerId);
    
    /**
     * 根据校区名称查询校区
     */
    Campus getCampusByName(String name);
}
