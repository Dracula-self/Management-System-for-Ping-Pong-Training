package com.quan.project.mapper;

import com.quan.project.dto.PageDTO;
import com.quan.project.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户数据访问接口
 */
@Mapper
public interface UserMapper {
    
    /**
     * 查询所有用户
     */
    List<User> selectAll();

    /**
     * 分页查询用户列表
     */
    List<User> selectPage();

    /**
     * 分页搜索用户（根据用户名模糊查询）
     */
    List<User> selectByUsernameLikePage(@Param("username") String username);
    
    /**
     * 通用分页查询用户（支持动态条件）
     */
    List<User> selectPageByConditions(@Param("request") PageDTO pageRequest);
    
    /**
     * 根据ID查询用户
     */
    User selectById(Integer id);
    
    /**
     * 根据用户名查询用户
     */
    User selectByUsername(String username);
    
    /**
     * 根据邮箱查询用户
     */
    User selectByEmail(String email);
    
    /**
     * 根据手机号查询用户
     */
    User selectByPhone(String phone);
    
    /**
     * 根据用户名模糊查询用户
     */
    List<User> selectByUsernameLike(String username);
    
    /**
     * 新增用户
     */
    int insert(User user);
    
    /**
     * 更新用户信息
     */
    int update(User user);
    
    /**
     * 更新用户密码
     */
    int updatePassword(@Param("id") Integer id, @Param("password") String password);
    
    /**
     * 删除用户
     */
    int deleteById(Integer id);
    
    /**
     * 检查用户名是否存在
     */
    int countByUsername(String username);
    
    /**
     * 检查用户名是否存在（排除指定ID）
     */
    int countByUsernameExcludeId(@Param("username") String username, @Param("id") Integer id);
    
    /**
     * 检查邮箱是否存在
     */
    int countByEmail(String email);
    
    /**
     * 检查邮箱是否存在（排除指定ID）
     */
    int countByEmailExcludeId(@Param("email") String email, @Param("id") Integer id);
    
    /**
     * 检查手机号是否存在
     */
    int countByPhone(String phone);
    
    /**
     * 检查手机号是否存在（排除指定ID）
     */
    int countByPhoneExcludeId(@Param("phone") String phone, @Param("id") Integer id);
    
    /**
     * 用户登录验证
     */
    User login(@Param("username") String username, @Param("password") String password);
    
    /**
     * 根据角色查询用户
     */
    List<User> selectByRole(Integer userRole);
    
    /**
     * 根据状态查询用户
     */
    List<User> selectByStatus(Integer userStatus);
    
    /**
     * 更新用户状态
     */
    int updateStatus(@Param("id") Integer id, @Param("userStatus") Integer userStatus);
    
    /**
     * 更新用户角色
     */
    int updateRole(@Param("id") Integer id, @Param("userRole") Integer userRole);
    
    /**
     * 更新用户头像
     */
    int updateAvatar(@Param("id") Integer id, @Param("avatar") String avatar);
    
    /**
     * 根据角色和校区查询用户
     */
    List<User> selectByRoleAndCampus(@Param("userRole") Integer userRole, @Param("campusId") Integer campusId);
} 