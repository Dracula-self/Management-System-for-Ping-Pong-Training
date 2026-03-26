package com.quan.project.service;

import com.github.pagehelper.PageInfo;
import com.quan.project.dto.PageDTO;
import com.quan.project.entity.User;

import java.util.List;

/**
 * 用户业务逻辑接口
 */
public interface UserService {
    
    /**
     * 查询所有用户
     */
    List<User> getAllUsers();

    /**
     * 分页查询用户列表
     */
    PageInfo<User> getUserPage(PageDTO pageRequest);

    /**
     * 分页搜索用户
     */
    PageInfo<User> searchUsersPage(String username, PageDTO pageRequest);
    
    /**
     * 通用分页查询用户
     * 支持多种查询条件，根据PageRequestDTO中的params参数进行动态查询
     */
    PageInfo<User> queryUsersPage(PageDTO pageRequest);
    
    /**
     * 根据用户名查询用户
     */
    User getUserByUsername(String username);
    
    /**
     * 根据邮箱查询用户
     */
    User getUserByEmail(String email);
    
    /**
     * 根据手机号查询用户
     */
    User getUserByPhone(String phone);
    
    /**
     * 搜索用户（根据用户名模糊查询）
     */
    List<User> searchUsers(String username);
    
    /**
     * 根据ID查询用户
     */
    User getUserById(Integer id);
    
    /**
     * 新增用户
     */
    void addUser(User user);
    
    /**
     * 更新用户信息
     */
    void updateUser(User user);
    
    /**
     * 删除用户
     */
    void deleteUser(Integer id);
    
    /**
     * 更新用户密码
     */
    void updateUserPassword(Integer id, String newPassword);
    
    /**
     * 用户登录验证
     */
    User login(String username, String password);
    
    /**
     * 根据角色查询用户
     */
    List<User> getUsersByRole(Integer userRole);
    
    /**
     * 根据状态查询用户
     */
    List<User> getUsersByStatus(Integer userStatus);
    
    /**
     * 更新用户状态
     */
    void updateUserStatus(Integer id, Integer userStatus);
    
    /**
     * 更新用户角色
     */
    void updateUserRole(Integer id, Integer userRole);
    
    /**
     * 更新用户头像
     */
    void updateUserAvatar(Integer id, String avatarUrl);
    
    /**
     * 修改密码
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    void changePassword(Integer userId, String oldPassword, String newPassword);
    
    /**
     * 查询教练列表（学员查询教练用）
     * 支持按姓名、性别、年龄搜索
     */
    PageInfo<User> searchCoaches(PageDTO pageRequest);
} 