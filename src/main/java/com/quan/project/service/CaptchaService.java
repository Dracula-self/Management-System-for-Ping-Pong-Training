package com.quan.project.service;

/**
 * 验证码服务接口
 */
public interface CaptchaService {
    
    /**
     * 验证验证码
     * 
     * @param captcha 用户输入的验证码
     * @param captchaToken 验证码令牌
     * @return 验证是否成功
     */
    boolean verifyCaptcha(String captcha, String captchaToken);
}