package com.quan.project.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户实体类
 * 对应数据库表：users
 */
public class User implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 用户角色常量
     */
    public static final int ROLE_SUPER_ADMIN = 1;    // 超级管理员
    public static final int ROLE_CAMPUS_ADMIN = 2;   // 校区管理员
    public static final int ROLE_COACH = 3;          // 教练
    public static final int ROLE_STUDENT = 4;        // 学员
    
    /**
     * 用户状态常量
     */
    public static final int STATUS_PENDING = 0;      // 待审核
    public static final int STATUS_ACTIVE = 1;       // 正常
    public static final int STATUS_DISABLED = 2;     // 禁用
    
    /**
     * 性别常量
     */
    public static final int GENDER_MALE = 1;         // 男
    public static final int GENDER_FEMALE = 2;       // 女
    
    /**
     * 教练级别常量
     */
    public static final int COACH_LEVEL_SENIOR = 1;  // 高级
    public static final int COACH_LEVEL_MIDDLE = 2;  // 中级
    public static final int COACH_LEVEL_JUNIOR = 3;  // 初级
    
    /**
     * 用户ID
     */
    private Integer id;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 密码
     */
    private String password;
    
    /**
     * 真实姓名
     */
    private String realName;
    
    /**
     * 性别：1-男, 2-女
     */
    private Integer gender;
    
    /**
     * 年龄
     */
    private Integer age;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 电子邮箱
     */
    private String email;
    
    /**
     * 所属校区ID
     */
    private Integer campusId;
    
    /**
     * 用户角色：1-超级管理员, 2-校区管理员, 3-教练, 4-学员
     */
    private Integer userRole;
    
    /**
     * 用户状态：0-待审核, 1-正常, 2-禁用
     */
    private Integer userStatus;
    
    /**
     * 用户头像URL (主要用于教练)
     */
    private String avatar;
    
    /**
     * 过往成绩 (教练)
     */
    private String achievements;
    
    /**
     * 教练级别：1-高级, 2-中级, 3-初级
     */
    private Integer coachLevel;
    
    /**
     * 账户余额 (学员)
     */
    private BigDecimal balance;
    
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime updateTime;
    
    public User() {}
    
    public User(String username, String password, String realName, String phone, String email) {
        this.username = username;
        this.password = password;
        this.realName = realName;
        this.phone = phone;
        this.email = email;
        this.userRole = ROLE_STUDENT; // 默认为学员角色
        this.userStatus = STATUS_ACTIVE; // 默认状态
        this.balance = BigDecimal.ZERO; // 默认余额为0
        this.createTime = LocalDateTime.now();
    }
    
    // Getter and Setter methods
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getRealName() {
        return realName;
    }
    
    public void setRealName(String realName) {
        this.realName = realName;
    }
    
    public Integer getGender() {
        return gender;
    }
    
    public void setGender(Integer gender) {
        this.gender = gender;
    }
    
    public Integer getAge() {
        return age;
    }
    
    public void setAge(Integer age) {
        this.age = age;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public Integer getCampusId() {
        return campusId;
    }
    
    public void setCampusId(Integer campusId) {
        this.campusId = campusId;
    }
    
    public Integer getUserRole() {
        return userRole;
    }
    
    public void setUserRole(Integer userRole) {
        this.userRole = userRole;
    }
    
    public Integer getUserStatus() {
        return userStatus;
    }
    
    public void setUserStatus(Integer userStatus) {
        this.userStatus = userStatus;
    }
    
    public String getAvatar() {
        return avatar;
    }
    
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    
    public String getAchievements() {
        return achievements;
    }
    
    public void setAchievements(String achievements) {
        this.achievements = achievements;
    }
    
    public Integer getCoachLevel() {
        return coachLevel;
    }
    
    public void setCoachLevel(Integer coachLevel) {
        this.coachLevel = coachLevel;
    }
    
    public BigDecimal getBalance() {
        return balance;
    }
    
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }
    
    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
    
    /**
     * 获取用户角色的文本描述
     */
    public String getUserRoleText() {
        if (userRole == null) return "";
        switch (userRole) {
            case ROLE_SUPER_ADMIN: return "超级管理员";
            case ROLE_CAMPUS_ADMIN: return "校区管理员";
            case ROLE_COACH: return "教练";
            case ROLE_STUDENT: return "学员";
            default: return "未知角色";
        }
    }
    
    /**
     * 获取用户状态的文本描述
     */
    public String getUserStatusText() {
        if (userStatus == null) return "";
        switch (userStatus) {
            case STATUS_PENDING: return "待审核";
            case STATUS_ACTIVE: return "正常";
            case STATUS_DISABLED: return "禁用";
            default: return "未知状态";
        }
    }
    
    /**
     * 获取性别的文本描述
     */
    public String getGenderText() {
        if (gender == null) return "";
        switch (gender) {
            case GENDER_MALE: return "男";
            case GENDER_FEMALE: return "女";
            default: return "未知";
        }
    }
    
    /**
     * 获取教练级别的文本描述
     */
    public String getCoachLevelText() {
        if (coachLevel == null) return "";
        switch (coachLevel) {
            case COACH_LEVEL_SENIOR: return "高级教练";
            case COACH_LEVEL_MIDDLE: return "中级教练";
            case COACH_LEVEL_JUNIOR: return "初级教练";
            default: return "未知级别";
        }
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", realName='" + realName + '\'' +
                ", gender=" + gender +
                ", age=" + age +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", campusId=" + campusId +
                ", userRole=" + userRole + 
                ", userStatus=" + userStatus +
                ", avatar='" + avatar + '\'' +
                ", achievements='" + achievements + '\'' +
                ", coachLevel=" + coachLevel +
                ", balance=" + balance +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
} 