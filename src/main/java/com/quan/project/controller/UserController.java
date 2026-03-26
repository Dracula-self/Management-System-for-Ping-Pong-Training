package com.quan.project.controller;

import com.github.pagehelper.PageInfo;
import com.quan.project.dto.*;
import com.quan.project.common.CurrentUser;
import com.quan.project.common.R;
import com.quan.project.entity.User;
import com.quan.project.service.UserService;
import com.quan.project.service.CaptchaService;
import com.quan.project.service.SystemLogService;
import com.quan.project.utils.JwtUtil;
import com.quan.project.vo.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制器
 */
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private CaptchaService captchaService;
    
    @Autowired
    private SystemLogService systemLogService;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;
    
    /**
     * 查询用户列表
     * 支持多种查询条件：username、userRole、userStatus、email等
     */
    @PostMapping("/search")
    public R<PageInfo<User>> search(@RequestBody PageDTO pageRequest) {
        PageInfo<User> pageInfo = userService.queryUsersPage(pageRequest);
        return R.success(pageInfo);
    }
    
    /**
     * 创建用户
     */
    @PostMapping
    public R<User> create(@RequestBody User user) {
        userService.addUser(user);
        
        // 记录创建用户日志
        try {
            Integer currentUserId = CurrentUser.getCurrentUserId();
            systemLogService.createLog(currentUserId, "创建用户", 
                String.format("创建用户【%s】，角色：%d", user.getUsername(), user.getUserRole()));
        } catch (Exception e) {
            // 日志记录失败不影响主业务
        }
        
        return R.success(user);
    }
    
    /**
     * 更新用户信息
     */
    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Integer id, @RequestBody User user) {
        // 以路径参数ID为准
        user.setId(id);
        userService.updateUser(user);
        
        // 记录更新用户日志
        try {
            Integer currentUserId = CurrentUser.getCurrentUserId();
            systemLogService.createLog(currentUserId, "更新用户", 
                String.format("更新用户【%s】信息", user.getUsername()));
        } catch (Exception e) {
            // 日志记录失败不影响主业务
        }
        
        return R.success();
    }
    
    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Integer id) {
        // 获取用户信息用于日志记录
        User userToDelete = null;
        try {
            userToDelete = userService.getUserById(id);
        } catch (Exception e) {
            // 如果获取用户信息失败，继续删除操作
        }
        
        userService.deleteUser(id);
        
        // 记录删除用户日志
        try {
            Integer currentUserId = CurrentUser.getCurrentUserId();
            String username = userToDelete != null ? userToDelete.getUsername() : "ID:" + id;
            systemLogService.createLog(currentUserId, "删除用户", 
                String.format("删除用户【%s】", username));
        } catch (Exception e) {
            // 日志记录失败不影响主业务
        }
        
        return R.success();
    }
    
    /**
     * 用户登录（支持无状态验证码）
     */
    @PostMapping("/login")
    public R<LoginVO> login(@RequestBody LoginDTO loginDTO) {
        // 验证验证码
        if (!captchaService.verifyCaptcha(loginDTO.getCaptcha(), loginDTO.getCaptchaToken())) {
            return R.error("验证码错误或已过期");
        }
        
        // 验证用户登录
        User user = userService.login(loginDTO.getUsername(), loginDTO.getPassword());
        
        // 记录登录日志
        try {
            systemLogService.createLog(user.getId(), "用户登录", 
                String.format("用户【%s】成功登录系统", user.getUsername()));
        } catch (Exception e) {
            // 日志记录失败不影响主业务
        }
        
        // 生成JWT token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getUserRole());
        
        // 创建登录响应
        LoginVO loginResponse = new LoginVO(user, token, jwtExpiration);
        
        return R.success("登录成功", loginResponse);
    }
    
    /**
     * 用户注册
     * 公开接口，不需要认证，固定角色为STUDENT
     */
    @PostMapping("/register")
    public R<Void> register(@RequestBody User user) {
        // 强制设置角色为STUDENT，确保安全性
        user.setUserRole(User.ROLE_STUDENT);
        
        // 调用用户服务进行注册
        userService.addUser(user);
        
        // 记录注册日志
        try {
            systemLogService.createLog(user.getId(), "用户注册", 
                String.format("新用户【%s】注册成功", user.getUsername()));
        } catch (Exception e) {
            // 日志记录失败不影响主业务
        }
        
        return R.success();
    }
    
    /**
     * 更新用户头像
     */
    @PutMapping("/{id}/avatar")
    public R<String> updateAvatar(@PathVariable Integer id, @RequestParam String avatarUrl) {
        // 验证用户ID是否为当前登录用户或管理员
        Integer currentUserId = CurrentUser.getCurrentUserId();
        boolean isAdmin = CurrentUser.isAdmin();
        
        if (!isAdmin && !id.equals(currentUserId)) {
            return R.error("无权限修改其他用户的头像");
        }
        
        // 更新用户头像
        userService.updateUserAvatar(id, avatarUrl);
        
        return R.success("头像更新成功", avatarUrl);
    }
    
    /**
     * 更新用户状态
     */
    @PutMapping("/{id}/status")
    public R<Void> updateStatus(@PathVariable Integer id, @RequestParam Integer userStatus) {
        userService.updateUserStatus(id, userStatus);
        return R.success();
    }
    
    /**
     * 更新用户角色
     */
    @PutMapping("/{id}/role")
    public R<Void> updateRole(@PathVariable Integer id, @RequestParam Integer userRole) {
        userService.updateUserRole(id, userRole);
        return R.success();
    }

    /**
     * 根据ID获取用户信息
     */
    @GetMapping("/{id}")
    public R<User> getUserById(@PathVariable Integer id) {
        User user = userService.getUserById(id);
        return R.success(user);
    }
    
    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    public R<Void> changePassword(@RequestBody PasswordChangeDTO passwordChangeDTO) {
        // 获取当前登录用户ID
        Integer currentUserId = CurrentUser.getCurrentUserId();
        
        // 调用用户服务修改密码
        userService.changePassword(currentUserId, passwordChangeDTO.getOldPassword(), passwordChangeDTO.getNewPassword());
        
        return R.success();
    }
    
    /**
     * 1.11 查询教练列表 - POST /api/users/coaches/search
     * 学员查询教练，支持按姓名、性别、年龄搜索
     */
    @PostMapping("/coaches/search")
    public R<PageInfo<User>> searchCoaches(@RequestBody PageDTO pageRequest) {
        PageInfo<User> pageInfo = userService.searchCoaches(pageRequest);
        return R.success(pageInfo);
    }
} 