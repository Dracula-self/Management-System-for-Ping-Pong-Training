package com.quan.project.vo;

import com.quan.project.entity.SystemLog;

/**
 * 系统日志视图对象
 * 继承SystemLog，增加用户名等显示字段
 */
public class SystemLogVO extends SystemLog {
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 用户真实姓名
     */
    private String realName;
    
    // 无参构造方法
    public SystemLogVO() {
        super();
    }
    
    // 带SystemLog参数的构造方法
    public SystemLogVO(SystemLog systemLog) {
        super(systemLog.getId(), systemLog.getUserId(), systemLog.getAction(), 
              systemLog.getDetails(), systemLog.getLogTime());
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getRealName() {
        return realName;
    }
    
    public void setRealName(String realName) {
        this.realName = realName;
    }
    
    @Override
    public String toString() {
        return "SystemLogVO{" +
                "username='" + username + '\'' +
                ", realName='" + realName + '\'' +
                ", " + super.toString() +
                '}';
    }
}
