package com.quan.project.exception;

/**
 * 业务异常
 * 
 * @author quan
 */
public class BusinessException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    private String code;
    
    public BusinessException(String message) {
        super(message);
    }
    
    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }
    
    public String getCode() {
        return code;
    }
} 