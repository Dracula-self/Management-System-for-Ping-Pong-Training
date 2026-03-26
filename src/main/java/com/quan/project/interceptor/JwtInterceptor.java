package com.quan.project.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quan.project.common.R;
import com.quan.project.common.UserContext;
import com.quan.project.utils.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * JWT拦截器
 * 用于验证请求中的JWT token
 * 拦截所有/api开头的接口，排除特定的公开接口
 * 
 * @author quan
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {
    
    private static final Logger log = LoggerFactory.getLogger(JwtInterceptor.class);
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求路径
        String requestURI = request.getRequestURI();
        log.debug("JWT拦截器拦截API请求: {}", requestURI);
        
        // 检查资源是否存在
        if (!isResourceExists(handler)) {
            log.warn("请求的资源不存在: {}", requestURI);
            sendErrorResponse(response, 404, "请求的资源不存在");
            return false;
        }
        
        // 从请求头中获取token
        String token = getTokenFromRequest(request);
        
        if (token == null) {
            log.warn("API请求未携带token: {}", requestURI);
            sendErrorResponse(response, 401, "未提供认证token");
            return false;
        }
        
        // 验证token
        Map<String, Object> validateResult = jwtUtil.validateTokenWithDetails(token);
        boolean isValid = (Boolean) validateResult.get("valid");
        
        if (!isValid) {
            String message = (String) validateResult.get("message");
            log.warn("API请求token验证失败: {}, 请求路径: {}", message, requestURI);
            sendErrorResponse(response, 401, message);
            return false;
        }
        
        // token有效，获取用户信息并存储到request中
        try {
            String username = jwtUtil.getUsernameFromToken(token);
            Integer userId = jwtUtil.getUserIdFromToken(token);
            
            // 从token中获取用户角色（如果有）
            Integer userRole = null;
            try {
                userRole = jwtUtil.getClaimsFromToken(token).get("userRole", Integer.class);
            } catch (Exception e) {
                log.warn("无法从token中获取用户角色，使用默认值");
            }
            
            // 存入UserContext，便于在业务代码中直接获取
            UserContext.setUserId(userId);
            UserContext.setUsername(username);
            UserContext.setUserRole(userRole);
            
            log.debug("API请求token验证成功，用户: {}, ID: {}, 路径: {}", username, userId, requestURI);
            return true;
            
        } catch (Exception e) {
            log.error("API请求解析token用户信息失败: {}, 路径: {}", e.getMessage(), requestURI);
            sendErrorResponse(response, 401, "token信息解析失败");
            return false;
        }
    }
    
    /**
     * 检查请求的资源是否存在
     * 
     * @param handler 处理器对象
     * @return 资源是否存在
     */
    private boolean isResourceExists(Object handler) {
        // 如果是Controller方法，说明资源存在
        if (handler instanceof HandlerMethod) {
            return true;
        }
        
        // 如果是静态资源处理器，需要检查资源是否存在
        if (handler instanceof ResourceHttpRequestHandler) {
            // 静态资源的存在性会由ResourceHttpRequestHandler自行处理
            return true;
        }
        
        // 如果handler为null或其他类型，可能表示资源不存在
        return handler != null;
    }
    
    /**
     * 从请求中获取token
     * 支持从Header的Authorization字段获取
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        // 从Authorization header获取token
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7); // 去掉"Bearer "前缀
        }
        
        // 也可以从参数中获取（不推荐，但提供兼容性）
        return request.getParameter("token");
    }
    
    /**
     * 发送错误响应
     * 使用统一的R响应格式
     */
    private void sendErrorResponse(HttpServletResponse response, int businessCode, String message) throws IOException {
        response.setStatus(200); // 统一返回HTTP 200
        response.setContentType("application/json;charset=UTF-8");
        
        R<Object> result = R.error(businessCode, message);
        String jsonResponse = objectMapper.writeValueAsString(result);
        
        response.getWriter().write(jsonResponse);
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 请求结束后，清理ThreadLocal中的用户信息，防止内存泄漏
        UserContext.clear();
    }
} 