package com.quan.project.mapper;

import com.quan.project.dto.PageDTO;
import com.quan.project.entity.Table;
import com.quan.project.vo.TableVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 球台数据访问层
 */
@Mapper
public interface TableMapper {
    
    /**
     * 插入球台
     */
    int insert(Table table);
    
    /**
     * 根据ID删除球台
     */
    int deleteById(Integer id);
    
    /**
     * 更新球台信息
     */
    int update(Table table);
    
    /**
     * 根据ID查询球台
     */
    Table selectById(Integer id);
    
    /**
     * 查询所有球台
     */
    List<Table> selectAll();
    
    /**
     * 分页查询球台（支持动态条件）
     */
    List<Table> selectPageByConditions(@Param("request") PageDTO pageRequest);
    
    /**
     * 分页查询球台（包含校区名称、状态、当前预约信息）
     */
    List<TableVO> selectPageWithInfo(@Param("request") PageDTO pageRequest);
    
    /**
     * 根据校区ID查询球台
     */
    List<Table> selectByCampusId(Integer campusId);
    
    /**
     * 根据校区ID和球台编号查询球台
     */
    Table selectByCampusIdAndTableNumber(@Param("campusId") Integer campusId, @Param("tableNumber") String tableNumber);
    
    /**
     * 查询指定校区在指定时间段可用的球台
     */
    List<Table> selectAvailableTablesByCampusAndTime(@Param("campusId") Integer campusId, 
                                                    @Param("startTime") LocalDateTime startTime, 
                                                    @Param("endTime") LocalDateTime endTime);
    
    /**
     * 统计校区球台数量
     */
    int countByCampusId(Integer campusId);
}
