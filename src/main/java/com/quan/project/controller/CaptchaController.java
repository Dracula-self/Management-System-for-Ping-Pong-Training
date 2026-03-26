package com.quan.project.controller;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.quan.project.common.R;
import com.quan.project.service.CaptchaService;
import com.quan.project.vo.CaptchaVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 验证码控制器 - 无状态设计
 */
@RestController
@RequestMapping("/api/captcha")
public class CaptchaController {

    @Autowired
    private DefaultKaptcha defaultKaptcha;
    
    @Autowired
    private CaptchaService captchaService;

    @Value("${captcha.secret}")
    private String captchaSecret;

    @Value("${captcha.expiration}")
    private Long captchaExpiration;

    /**
     * 生成验证码（无状态）
     */
    @GetMapping("/generate")
    public R<CaptchaVO> generateCaptcha() throws Exception {
        // 生成验证码文本
        String captchaText = defaultKaptcha.createText();
        
        // 生成验证码图片
        BufferedImage captchaImage = defaultKaptcha.createImage(captchaText);
        
        // 创建验证码Token（包含验证码和过期时间）
        long expireTime = System.currentTimeMillis() + captchaExpiration;
        String captchaData = captchaText + ":" + expireTime;
        String captchaToken = encryptCaptcha(captchaData);
        
        // 将图片转换为Base64
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(captchaImage, "jpg", baos);
        byte[] imageBytes = baos.toByteArray();
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        
        // 创建响应VO
        CaptchaVO captchaVO = new CaptchaVO(
            "data:image/jpeg;base64," + base64Image,
            captchaToken,
            System.currentTimeMillis()
        );
        
        return R.success(captchaVO);
    }

    /**
     * 验证验证码（无状态）
     */
    @GetMapping("/verify")
    public R<Map<String, Object>> verifyCaptcha(@RequestParam String captcha, 
                                               @RequestParam String captchaToken) {
        Map<String, Object> result = new HashMap<>();
        
        boolean isValid = captchaService.verifyCaptcha(captcha, captchaToken);
        
        if (isValid) {
            result.put("valid", true);
            result.put("message", "验证码正确");
            return R.success(result);
        } else {
            result.put("valid", false);
            result.put("message", "验证码错误或已过期");
            return R.error("验证码错误或已过期");
        }
    }

    /**
     * 加密验证码数据
     */
    private String encryptCaptcha(String data) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(captchaSecret.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encrypted = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }
} 