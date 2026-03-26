package com.quan.project.config;

import com.quan.project.interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 * 配置拦截器、跨域等
 * 
 * @author quan
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Autowired
    private JwtInterceptor jwtInterceptor;
    
    @Value("${file.upload.root-path}")
    private String uploadPath;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                // 拦截所有/api开头的请求
                .addPathPatterns("/api/**")
                // 排除登录接口
                .excludePathPatterns("/api/users/login")
                // 排除注册接口
                .excludePathPatterns("/api/users/register")
                // 排除验证码相关接口
                .excludePathPatterns("/api/captcha/**")
                // 排除文件上传接口（需要认证，但在拦截器中单独处理）
                .excludePathPatterns("/api/file/**")
                // 排除获取所有校区接口（用于注册时选择校区）
                .excludePathPatterns("/api/campus/all")
                // 排除查询可报名比赛接口（公开查看）
                .excludePathPatterns("/api/competitions/available");
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置上传文件的静态资源访问
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath + "/");
    }
} 