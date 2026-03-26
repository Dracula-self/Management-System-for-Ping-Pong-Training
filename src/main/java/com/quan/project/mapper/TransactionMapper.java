package com.quan.project.mapper;

import com.quan.project.dto.PageDTO;
import com.quan.project.entity.Transaction;
import com.quan.project.vo.TransactionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 账户流水数据访问层
 */
@Mapper
public interface TransactionMapper {
    
    /**
     * 插入流水记录
     */
    int insert(Transaction transaction);
    
    /**
     * 根据ID删除流水记录
     */
    int deleteById(Integer id);
    
    /**
     * 根据ID查询流水记录
     */
    Transaction selectById(Integer id);
    
    /**
     * 分页查询流水记录（支持动态条件）
     */
    List<Transaction> selectPageByConditions(@Param("request") PageDTO pageRequest);
    
    /**
     * 分页查询流水记录（包含学员姓名）
     */
    List<TransactionVO> selectPageWithInfo(@Param("request") PageDTO pageRequest);
    
    /**
     * 根据学员ID查询流水记录
     */
    List<Transaction> selectByStudentId(Integer studentId);
    
    /**
     * 根据学员ID和类型查询流水记录
     */
    List<Transaction> selectByStudentIdAndType(@Param("studentId") Integer studentId, @Param("type") Integer type);
    
    /**
     * 计算学员账户余额
     */
    BigDecimal calculateBalance(Integer studentId);
    
    /**
     * 查询学员最近的流水记录
     */
    List<Transaction> selectRecentByStudentId(@Param("studentId") Integer studentId, @Param("limit") Integer limit);
    
    /**
     * 统计学员指定类型的流水总金额
     */
    BigDecimal sumAmountByStudentIdAndType(@Param("studentId") Integer studentId, @Param("type") Integer type);
}
