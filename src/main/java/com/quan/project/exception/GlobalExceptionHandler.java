package com.quan.project.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import com.quan.project.common.R;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 全局异常处理器
 * 
 * @author quan
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    /**
     * 业务异常处理
     */
    @ExceptionHandler(BusinessException.class)
    public R<Object> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.error("业务异常: {}, 请求路径: {}", e.getMessage(), request.getRequestURI());
        return R.error("系统业务异常：" + e.getMessage() + " [请求路径: " + request.getRequestURI() + "]");
    }
    
    /**
     * 处理资源不存在异常(404)
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public R<Object> handleNoResourceFoundException(NoResourceFoundException e) {
        String requestPath = e.getResourcePath();
        log.warn("请求的资源不存在: {}", requestPath);
        return R.error(404, "请求的资源不存在: " + requestPath);
    }
    
    /**
     * 系统异常处理
     */
    @ExceptionHandler(Exception.class)
    public R<Object> handleException(Exception e, HttpServletRequest request) {
        log.error("系统异常: {}, 请求路径: {}", e.getMessage(), request.getRequestURI(), e);
        return R.error("系统内部错误：" + e.getMessage() + " [请求路径: " + request.getRequestURI() + "]");
    }
    
    /**
     * 运行时异常处理
     */
    @ExceptionHandler(RuntimeException.class)
    public R<Object> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        log.error("运行时异常: {}, 请求路径: {}", e.getMessage(), request.getRequestURI(), e);
        return R.error("系统运行异常：" + e.getMessage() + " [请求路径: " + request.getRequestURI() + "]");
    }
} 