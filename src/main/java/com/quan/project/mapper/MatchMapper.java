package com.quan.project.mapper;

import com.quan.project.entity.Match;
import com.quan.project.vo.MatchVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 比赛对阵Mapper接口
 */
@Mapper
public interface MatchMapper {
    
    /**
     * 插入对阵记录
     */
    int insert(Match match);
    
    /**
     * 批量插入对阵记录
     */
    int insertBatch(@Param("matches") List<Match> matches);
    
    /**
     * 根据ID查询对阵
     */
    Match selectById(Integer id);
    
    /**
     * 查询比赛的所有对阵（包含选手姓名等详细信息）
     */
    List<MatchVO> selectByCompetitionAndGroup(@Param("competitionId") Integer competitionId, 
                                              @Param("groupLevel") Integer groupLevel);
    
    /**
     * 查询指定轮次的对阵
     */
    List<Match> selectByRound(@Param("competitionId") Integer competitionId, 
                              @Param("groupLevel") Integer groupLevel, 
                              @Param("roundNumber") Integer roundNumber);
    
    /**
     * 更新比赛结果
     */
    int updateResult(@Param("id") Integer id, 
                     @Param("winnerId") Integer winnerId, 
                     @Param("score") String score);
    
    /**
     * 获取最大轮次
     */
    Integer selectMaxRound(@Param("competitionId") Integer competitionId, 
                           @Param("groupLevel") Integer groupLevel);
    
    /**
     * 统计指定轮次已完成的比赛数量
     */
    Integer countCompletedMatches(@Param("competitionId") Integer competitionId, 
                                  @Param("groupLevel") Integer groupLevel, 
                                  @Param("roundNumber") Integer roundNumber);
    
    /**
     * 统计指定轮次的总比赛数量
     */
    Integer countTotalMatches(@Param("competitionId") Integer competitionId, 
                              @Param("groupLevel") Integer groupLevel, 
                              @Param("roundNumber") Integer roundNumber);
    
    /**
     * 删除比赛的所有对阵记录
     */
    int deleteByCompetition(@Param("competitionId") Integer competitionId, 
                            @Param("groupLevel") Integer groupLevel);
}
