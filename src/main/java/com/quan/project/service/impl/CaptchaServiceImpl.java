package com.quan.project.service.impl;

import com.quan.project.service.CaptchaService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * 验证码服务实现类
 */
@Service
public class CaptchaServiceImpl implements CaptchaService {
    
    @Value("${captcha.secret}")
    private String captchaSecret;
    
    @Override
    public boolean verifyCaptcha(String captcha, String captchaToken) {
        try {
            // 解密验证码token
            Cipher cipher = Cipher.getInstance("AES");
            SecretKeySpec secretKey = new SecretKeySpec(captchaSecret.getBytes(), "AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(captchaToken));
            String captchaData = new String(decrypted);
            
            String[] parts = captchaData.split(":");
            if (parts.length != 2) {
                return false;
            }
            
            String originalCaptcha = parts[0];
            long expireTime = Long.parseLong(parts[1]);
            
            // 检查是否过期
            if (System.currentTimeMillis() > expireTime) {
                return false;
            }
            
            // 验证验证码（不区分大小写）
            return originalCaptcha.equalsIgnoreCase(captcha);
            
        } catch (Exception e) {
            return false;
        }
    }
}