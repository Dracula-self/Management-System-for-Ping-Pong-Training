package com.quan.project.mapper;

import com.quan.project.dto.PageDTO;
import com.quan.project.entity.Competition;
import com.quan.project.vo.CompetitionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 比赛数据访问层
 */
@Mapper
public interface CompetitionMapper {
    
    /**
     * 插入比赛
     */
    int insert(Competition competition);
    
    /**
     * 根据ID删除比赛
     */
    int deleteById(Integer id);
    
    /**
     * 更新比赛信息
     */
    int update(Competition competition);
    
    /**
     * 更新比赛状态
     */
    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);
    
    /**
     * 根据ID查询比赛
     */
    Competition selectById(Integer id);
    
    /**
     * 分页查询比赛（支持动态条件，包含报名人数）
     */
    List<CompetitionVO> selectPageByConditions(@Param("request") PageDTO pageRequest);
    
    /**
     * 查询所有比赛
     */
    List<Competition> selectAll();
    
    /**
     * 查询当前可报名的比赛
     */
    List<Competition> selectAvailableCompetitions();
    
    /**
     * 查询进行中的比赛
     */
    List<Competition> selectInProgressCompetitions();
}
