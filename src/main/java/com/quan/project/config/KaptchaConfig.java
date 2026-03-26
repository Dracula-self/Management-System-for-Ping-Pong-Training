package com.quan.project.config;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * Kaptcha 验证码配置
 */
@Configuration
public class KaptchaConfig {

    @Bean
    public DefaultKaptcha getDefaultKaptcha() {
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        Properties properties = new Properties();
        
        // 验证码字符范围
        properties.setProperty("kaptcha.textproducer.char.string", "0123456789");
        // 验证码字符长度
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        // 验证码字符间距
        properties.setProperty("kaptcha.textproducer.char.space", "4");
        
        // 验证码图片宽度
        properties.setProperty("kaptcha.image.width", "120");
        // 验证码图片高度
        properties.setProperty("kaptcha.image.height", "40");
        
        // 字体设置
        properties.setProperty("kaptcha.textproducer.font.names", "Arial,Courier");
        properties.setProperty("kaptcha.textproducer.font.size", "32");
        properties.setProperty("kaptcha.textproducer.font.color", "black");
        
        // 干扰线颜色
        properties.setProperty("kaptcha.noise.color", "blue");
        
        // 背景色渐变，开始颜色
        properties.setProperty("kaptcha.background.color.from", "lightGray");
        // 背景色渐变，结束颜色
        properties.setProperty("kaptcha.background.color.to", "white");
        
        // 边框
        properties.setProperty("kaptcha.border", "yes");
        properties.setProperty("kaptcha.border.color", "gray");
        
        Config config = new Config(properties);
        defaultKaptcha.setConfig(config);
        
        return defaultKaptcha;
    }
} 