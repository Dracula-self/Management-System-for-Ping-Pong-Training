package com.quan.project.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * 分页请求DTO
 * 用于接收前端分页查询参数
 */
public class PageDTO {
    
    /**
     * 页码，从1开始
     */
    private Integer pageNum = 1;
    
    /**
     * 每页大小
     */
    private Integer pageSize = 10;
    
    /**
     * 查询参数
     */
    private Map<String, Object> params = new HashMap<>();
    
    public PageDTO() {
    }
    
    public PageDTO(Integer pageNum, Integer pageSize) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }
    
    public PageDTO(Integer pageNum, Integer pageSize, Map<String, Object> params) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.params = params != null ? params : new HashMap<>();
    }
    
    /**
     * 添加查询参数
     */
    public PageDTO addParam(String key, Object value) {
        if (key != null && value != null) {
            this.params.put(key, value);
        }
        return this;
    }
    
    /**
     * 获取字符串类型参数
     */
    public String getStringParam(String key) {
        Object value = params.get(key);
        return value != null ? value.toString() : null;
    }
    
    /**
     * 获取整数类型参数
     */
    public Integer getIntegerParam(String key) {
        Object value = params.get(key);
        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
    
    /**
     * 检查是否包含某个参数
     */
    public boolean hasParam(String key) {
        return params.containsKey(key) && params.get(key) != null;
    }
    
    /**
     * 移除参数
     */
    public PageDTO removeParam(String key) {
        params.remove(key);
        return this;
    }
    
    /**
     * 清空所有参数
     */
    public PageDTO clearParams() {
        params.clear();
        return this;
    }
    
    // Getter and Setter methods
    public Integer getPageNum() {
        return pageNum;
    }
    
    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }
    
    public Integer getPageSize() {
        return pageSize;
    }
    
    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
    
    public Map<String, Object> getParams() {
        return params;
    }
    
    public void setParams(Map<String, Object> params) {
        this.params = params != null ? params : new HashMap<>();
    }
    
    @Override
    public String toString() {
        return "PageRequestDTO{" +
                "pageNum=" + pageNum +
                ", pageSize=" + pageSize +
                ", params=" + params +
                '}';
    }
} 