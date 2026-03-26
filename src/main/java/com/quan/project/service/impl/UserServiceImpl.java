package com.quan.project.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.quan.project.common.CurrentUser;
import com.quan.project.dto.PageDTO;
import com.quan.project.entity.User;
import com.quan.project.exception.BusinessException;
import com.quan.project.mapper.UserMapper;
import com.quan.project.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户业务逻辑实现类
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {
    
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public List<User> getAllUsers() {
        try {
            return userMapper.selectAll();
        } catch (Exception e) {
            log.error("查询所有用户失败", e);
            throw new BusinessException("查询用户列表失败");
        }
    }
    
    @Override
    public PageInfo<User> getUserPage(PageDTO pageRequest) {
        try {
            // 设置分页参数
            PageHelper.startPage(pageRequest.getPageNum(), pageRequest.getPageSize());
            
            // 执行查询
            List<User> users = userMapper.selectPage();
            
            // 直接返回PageInfo
            return new PageInfo<>(users);
        } catch (Exception e) {
            log.error("分页查询用户失败", e);
            throw new BusinessException("分页查询用户失败");
        }
    }
    
    @Override
    public PageInfo<User> searchUsersPage(String username, PageDTO pageRequest) {
        try {
            if (!StringUtils.hasText(username)) {
                return getUserPage(pageRequest);
            }
            
            String searchKeyword = username.trim();
            
            // 设置分页参数
            PageHelper.startPage(pageRequest.getPageNum(), pageRequest.getPageSize());
            
            // 执行搜索查询
            List<User> users = userMapper.selectByUsernameLikePage(searchKeyword);
            
            // 直接返回PageInfo
            return new PageInfo<>(users);
        } catch (Exception e) {
            log.error("分页搜索用户失败，用户名: {}", username, e);
            throw new BusinessException("分页搜索用户失败");
        }
    }
    
    @Override
    public PageInfo<User> queryUsersPage(PageDTO pageRequest) {
        try {
            // 根据当前用户角色进行数据过滤
            Integer currentUserId = CurrentUser.getCurrentUserId();
            Integer currentUserRole = CurrentUser.getCurrentUserRole();
            
            if (currentUserRole == 2) { // 校区管理员
                // 获取当前用户的校区信息
                User currentUser = getUserById(currentUserId);
                if (currentUser.getCampusId() != null) {
                    // 校区管理员只能查看本校区的用户（教练和学员）
                    pageRequest.addParam("campusId", currentUser.getCampusId());
                    
                    // 如果没有指定角色，则只查看教练和学员
                    Object userRole = pageRequest.getParams() != null ? pageRequest.getParams().get("userRole") : null;
                    if (userRole == null) {
                        // 不限制角色时，校区管理员可以看到本校区的所有角色用户
                    }
                }
            }
            // 超级管理员(1)可以查看所有用户，不需要额外过滤
            
            // 设置分页参数
            PageHelper.startPage(pageRequest.getPageNum(), pageRequest.getPageSize());
            
            // 执行动态条件查询
            List<User> users = userMapper.selectPageByConditions(pageRequest);
            
            // 直接返回PageInfo
            return new PageInfo<>(users);
        } catch (Exception e) {
            log.error("通用分页查询用户失败", e);
            throw new BusinessException("分页查询用户失败");
        }
    }
    
    @Override
    public User getUserById(Integer id) {
        if (id == null || id <= 0) {
            throw new BusinessException("用户ID不能为空");
        }
        
        try {
            User user = userMapper.selectById(id);
            if (user == null) {
                throw new BusinessException("用户不存在");
            }
            return user;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("根据ID查询用户失败，ID: {}", id, e);
            throw new BusinessException("查询用户失败");
        }
    }
    
    @Override
    public User getUserByUsername(String username) {
        if (!StringUtils.hasText(username)) {
            throw new BusinessException("用户名不能为空");
        }
        
        try {
            return userMapper.selectByUsername(username.trim());
        } catch (Exception e) {
            log.error("根据用户名查询用户失败，用户名: {}", username, e);
            throw new BusinessException("查询用户失败");
        }
    }
    
    @Override
    public User getUserByEmail(String email) {
        if (!StringUtils.hasText(email)) {
            throw new BusinessException("邮箱不能为空");
        }
        
        try {
            return userMapper.selectByEmail(email.trim());
        } catch (Exception e) {
            log.error("根据邮箱查询用户失败，邮箱: {}", email, e);
            throw new BusinessException("查询用户失败");
        }
    }
    
    @Override
    public User getUserByPhone(String phone) {
        if (!StringUtils.hasText(phone)) {
            throw new BusinessException("手机号不能为空");
        }
        
        try {
            return userMapper.selectByPhone(phone.trim());
        } catch (Exception e) {
            log.error("根据手机号查询用户失败，手机号: {}", phone, e);
            throw new BusinessException("查询用户失败");
        }
    }
    
    @Override
    public List<User> searchUsers(String username) {
        if (!StringUtils.hasText(username)) {
            return getAllUsers();
        }
        
        try {
            return userMapper.selectByUsernameLike(username.trim());
        } catch (Exception e) {
            log.error("搜索用户失败，用户名: {}", username, e);
            throw new BusinessException("搜索用户失败");
        }
    }
    
    @Override
    public void addUser(User user) {
        // 参数验证
        validateUser(user, false);
        
        // 检查用户名是否重复
        if (userMapper.countByUsername(user.getUsername()) > 0) {
            throw new BusinessException("用户名已存在，不能重复添加");
        }
        
        // 检查邮箱是否重复
        if (StringUtils.hasText(user.getEmail()) && userMapper.countByEmail(user.getEmail()) > 0) {
            throw new BusinessException("邮箱已存在，不能重复添加");
        }
        
        // 检查手机号是否重复
        if (StringUtils.hasText(user.getPhone()) && userMapper.countByPhone(user.getPhone()) > 0) {
            throw new BusinessException("手机号已存在，不能重复添加");
        }
        
        // 设置默认值
        if (user.getCreateTime() == null) {
            user.setCreateTime(LocalDateTime.now());
        }
        if (user.getUserRole() == null) {
            user.setUserRole(User.ROLE_STUDENT);  // 默认为学员角色
        }
        if (user.getUserStatus() == null) {
            user.setUserStatus(User.STATUS_ACTIVE);
        }
        // 如果是学员，初始化余额为0
        if (user.getUserRole() == User.ROLE_STUDENT && user.getBalance() == null) {
            user.setBalance(java.math.BigDecimal.ZERO);
        }
        
        try {
            int result = userMapper.insert(user);
            if (result <= 0) {
                throw new BusinessException("添加用户失败");
            }
            log.info("成功添加用户，ID: {}, 用户名: {}", user.getId(), user.getUsername());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("添加用户失败", e);
            throw new BusinessException("添加用户失败");
        }
    }
    
    @Override
    public void updateUser(User user) {
        if (user.getId() == null || user.getId() <= 0) {
            throw new BusinessException("用户ID不能为空");
        }
        
        // 检查用户是否存在
        getUserById(user.getId());
        
        // 参数验证
        validateUser(user, true);
        
        // 检查用户名是否重复（排除当前用户）
        if (userMapper.countByUsernameExcludeId(user.getUsername(), user.getId()) > 0) {
            throw new BusinessException("用户名已存在，不能重复");
        }
        
        // 检查邮箱是否重复（排除当前用户）
        if (StringUtils.hasText(user.getEmail()) && 
            userMapper.countByEmailExcludeId(user.getEmail(), user.getId()) > 0) {
            throw new BusinessException("邮箱已存在，不能重复");
        }
        
        // 检查手机号是否重复（排除当前用户）
        if (StringUtils.hasText(user.getPhone()) && 
            userMapper.countByPhoneExcludeId(user.getPhone(), user.getId()) > 0) {
            throw new BusinessException("手机号已存在，不能重复");
        }
        
        // 设置更新时间
        user.setUpdateTime(LocalDateTime.now());
        
        try {
            int result = userMapper.update(user);
            if (result <= 0) {
                throw new BusinessException("更新用户失败");
            }
            log.info("成功更新用户，ID: {}, 用户名: {}", user.getId(), user.getUsername());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新用户失败，ID: {}", user.getId(), e);
            throw new BusinessException("更新用户失败");
        }
    }
    
    @Override
    public void deleteUser(Integer id) {
        if (id == null || id <= 0) {
            throw new BusinessException("用户ID不能为空");
        }
        
        // 检查用户是否存在
        User user = getUserById(id);
        
        try {
            int result = userMapper.deleteById(id);
            if (result <= 0) {
                throw new BusinessException("删除用户失败");
            }
            log.info("成功删除用户，ID: {}, 用户名: {}", id, user.getUsername());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除用户失败，ID: {}", id, e);
            throw new BusinessException("删除用户失败");
        }
    }
    
    @Override
    public void updateUserPassword(Integer id, String newPassword) {
        if (id == null || id <= 0) {
            throw new BusinessException("用户ID不能为空");
        }
        if (!StringUtils.hasText(newPassword)) {
            throw new BusinessException("新密码不能为空");
        }
        if (newPassword.trim().length() < 6) {
            throw new BusinessException("密码长度不能少于6位");
        }
        
        // 检查用户是否存在
        getUserById(id);
        
        try {
            int result = userMapper.updatePassword(id, newPassword.trim());
            if (result <= 0) {
                throw new BusinessException("更新密码失败");
            }
            log.info("成功更新用户密码，ID: {}", id);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新用户密码失败，ID: {}", id, e);
            throw new BusinessException("更新密码失败");
        }
    }
    
    @Override
    public User login(String username, String password) {
        if (!StringUtils.hasText(username)) {
            throw new BusinessException("用户名不能为空");
        }
        if (!StringUtils.hasText(password)) {
            throw new BusinessException("密码不能为空");
        }
        
        try {
            User user = userMapper.login(username.trim(), password.trim());
            if (user == null) {
                throw new BusinessException("用户名或密码错误");
            }
            
            log.info("用户登录成功，用户名: {}", username);
            return user;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("用户登录失败，用户名: {}", username, e);
            throw new BusinessException("登录失败");
        }
    }
    
    /**
     * 验证用户信息
     * @param user 用户信息
     * @param isUpdate 是否是更新操作
     */
    private void validateUser(User user, boolean isUpdate) {
        if (user == null) {
            throw new BusinessException("用户信息不能为空");
        }
        
        if (!StringUtils.hasText(user.getUsername())) {
            throw new BusinessException("用户名不能为空");
        }
        
        if (user.getUsername().trim().length() < 3 || user.getUsername().trim().length() > 50) {
            throw new BusinessException("用户名长度必须在3-50个字符之间");
        }
        
        // 添加用户时，密码必须填写；更新用户时，密码可以为空
        if (!isUpdate) {
            if (!StringUtils.hasText(user.getPassword())) {
                throw new BusinessException("密码不能为空");
            }
            
            if (user.getPassword().trim().length() < 6) {
                throw new BusinessException("密码长度不能少于6位");
            }
        } else {
            // 更新用户时，如果提供了密码，则验证密码长度
            if (StringUtils.hasText(user.getPassword()) && user.getPassword().trim().length() < 6) {
                throw new BusinessException("密码长度不能少于6位");
            }
        }
        
        if (StringUtils.hasText(user.getEmail())) {
            String email = user.getEmail().trim();
            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                throw new BusinessException("邮箱格式不正确");
            }
        }
        
        if (StringUtils.hasText(user.getPhone())) {
            String phone = user.getPhone().trim();
            if (!phone.matches("^1[3-9]\\d{9}$")) {
                throw new BusinessException("手机号格式不正确");
            }
        }
    }
    
    @Override
    public List<User> getUsersByRole(Integer userRole) {
        if (userRole == null) {
            throw new BusinessException("用户角色不能为空");
        }
        
        try {
            return userMapper.selectByRole(userRole);
        } catch (Exception e) {
            log.error("根据角色查询用户失败，角色: {}", userRole, e);
            throw new BusinessException("查询用户失败");
        }
    }
    
    @Override
    public List<User> getUsersByStatus(Integer userStatus) {
        if (userStatus == null) {
            throw new BusinessException("用户状态不能为空");
        }
        
        try {
            return userMapper.selectByStatus(userStatus);
        } catch (Exception e) {
            log.error("根据状态查询用户失败，状态: {}", userStatus, e);
            throw new BusinessException("查询用户失败");
        }
    }
    
    @Override
    public void updateUserStatus(Integer id, Integer userStatus) {
        if (id == null || id <= 0) {
            throw new BusinessException("用户ID不能为空");
        }
        if (userStatus == null) {
            throw new BusinessException("用户状态不能为空");
        }
        if (userStatus != User.STATUS_PENDING && userStatus != User.STATUS_ACTIVE && userStatus != User.STATUS_DISABLED) {
            throw new BusinessException("用户状态只能是待审核(0)、正常(1)或禁用(2)");
        }
        
        // 检查用户是否存在
        getUserById(id);
        
        try {
            int result = userMapper.updateStatus(id, userStatus);
            if (result <= 0) {
                throw new BusinessException("更新用户状态失败");
            }
            log.info("成功更新用户状态，ID: {}, 状态: {}", id, userStatus);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新用户状态失败，ID: {}", id, e);
            throw new BusinessException("更新用户状态失败");
        }
    }
    
    @Override
    public void updateUserRole(Integer id, Integer userRole) {
        if (id == null || id <= 0) {
            throw new BusinessException("用户ID不能为空");
        }
        if (userRole == null) {
            throw new BusinessException("用户角色不能为空");
        }
        if (userRole != User.ROLE_SUPER_ADMIN && userRole != User.ROLE_CAMPUS_ADMIN && 
            userRole != User.ROLE_COACH && userRole != User.ROLE_STUDENT) {
            throw new BusinessException("用户角色只能是超级管理员(1)、校区管理员(2)、教练(3)或学员(4)");
        }
        
        // 检查用户是否存在
        getUserById(id);
        
        try {
            int result = userMapper.updateRole(id, userRole);
            if (result <= 0) {
                throw new BusinessException("更新用户角色失败");
            }
            log.info("成功更新用户角色，ID: {}, 角色: {}", id, userRole);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新用户角色失败，ID: {}", id, e);
            throw new BusinessException("更新用户角色失败");
        }
    }
    
    @Override
    public void updateUserAvatar(Integer id, String avatarUrl) {
        if (id == null || id <= 0) {
            throw new BusinessException("用户ID不能为空");
        }
        if (!StringUtils.hasText(avatarUrl)) {
            throw new BusinessException("头像URL不能为空");
        }
        
        // 检查用户是否存在
        getUserById(id);
        
        try {
            int result = userMapper.updateAvatar(id, avatarUrl.trim());
            if (result <= 0) {
                throw new BusinessException("更新用户头像失败");
            }
            log.info("成功更新用户头像，ID: {}, 头像URL: {}", id, avatarUrl);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新用户头像失败，ID: {}", id, e);
            throw new BusinessException("更新用户头像失败");
        }
    }
    
    @Override
    public void changePassword(Integer userId, String oldPassword, String newPassword) {
        // 参数验证
        if (userId == null || userId <= 0) {
            throw new BusinessException("用户ID不能为空");
        }
        if (!StringUtils.hasText(oldPassword)) {
            throw new BusinessException("原密码不能为空");
        }
        if (!StringUtils.hasText(newPassword)) {
            throw new BusinessException("新密码不能为空");
        }
        if (newPassword.trim().length() < 6) {
            throw new BusinessException("新密码长度不能少于6位");
        }
        
        try {
            // 检查用户是否存在
            User user = getUserById(userId);
            
            // 验证原密码是否正确
            User verifyUser = userMapper.login(user.getUsername(), oldPassword.trim());
            if (verifyUser == null) {
                throw new BusinessException("原密码不正确");
            }
            
            // 更新密码
            int result = userMapper.updatePassword(userId, newPassword.trim());
            if (result <= 0) {
                throw new BusinessException("修改密码失败");
            }
            
            log.info("成功修改用户密码，ID: {}", userId);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("修改密码失败，用户ID: {}", userId, e);
            throw new BusinessException("修改密码失败");
        }
    }
    
    @Override
    public PageInfo<User> searchCoaches(PageDTO pageRequest) {
        try {
            // 确保只查询教练角色的用户
            pageRequest.addParam("userRole", User.ROLE_COACH);
            pageRequest.addParam("userStatus", User.STATUS_ACTIVE); // 只查询正常状态的教练
            
            // 设置分页参数
            PageHelper.startPage(pageRequest.getPageNum(), pageRequest.getPageSize());
            
            // 执行动态条件查询
            List<User> coaches = userMapper.selectPageByConditions(pageRequest);
            
            // 直接返回PageInfo
            return new PageInfo<>(coaches);
        } catch (Exception e) {
            log.error("查询教练列表失败", e);
            throw new BusinessException("查询教练列表失败");
        }
    }
} 