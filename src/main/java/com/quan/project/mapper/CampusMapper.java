package com.quan.project.mapper;

import com.quan.project.dto.PageDTO;
import com.quan.project.entity.Campus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 校区数据访问层
 */
@Mapper
public interface CampusMapper {
    
    /**
     * 插入校区
     */
    int insert(Campus campus);
    
    /**
     * 根据ID删除校区
     */
    int deleteById(Integer id);
    
    /**
     * 更新校区信息
     */
    int update(Campus campus);
    
    /**
     * 根据ID查询校区
     */
    Campus selectById(Integer id);
    
    /**
     * 查询所有校区
     */
    List<Campus> selectAll();
    
    /**
     * 分页查询校区（支持动态条件）
     */
    List<Campus> selectPageByConditions(@Param("request") PageDTO pageRequest);
    
    /**
     * 根据管理员ID查询校区
     */
    Campus selectByManagerId(Integer managerId);
    
    /**
     * 根据校区名称查询校区
     */
    Campus selectByName(String name);
}
