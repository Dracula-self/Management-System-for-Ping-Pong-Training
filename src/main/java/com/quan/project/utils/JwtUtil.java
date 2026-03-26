package com.quan.project.utils;

// JWT相关类导入 - SpringBoot 3适配版本
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
// Spring相关类导入
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// Java工具类导入
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类 - SpringBoot 3适配版本
 */
@Component  // 标记为Spring组件，可被自动注入
public class JwtUtil {
    
    @Value("${jwt.secret}")  // 从配置文件读取JWT密钥
    private String secret;
    
    @Value("${jwt.expiration}")  // 从配置文件读取过期时间
    private Long expiration;
    
    /**
     * 获取签名密钥
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    /**
     * 生成JWT令牌
     */
    public String generateToken(Integer userId, String username) {
        return generateToken(userId, username, null);
    }
    
    /**
     * 生成JWT令牌（带用户角色）
     */
    public String generateToken(Integer userId, String username, Integer userRole) {
        Date now = new Date();  // 获取当前时间
        Date expiryDate = new Date(now.getTime() + expiration);  // 计算过期时间
        
        // 创建JWT构建器
        var builder = Jwts.builder()
                .subject(username)  // 设置主题为用户名
                .claim("userId", userId)  // 添加用户ID到载荷
                .issuedAt(now)  // 设置签发时间
                .expiration(expiryDate);  // 设置过期时间
                
        // 如果有用户角色，添加到载荷
        if (userRole != null) {
            builder.claim("userRole", userRole);
        }
        
        return builder.signWith(getSigningKey())  // 使用密钥签名
                .compact();  // 生成最终的JWT字符串
    }
    
    /**
     * 从JWT令牌中获取用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()  // 创建JWT解析器
                .verifyWith(getSigningKey())  // 设置验证密钥
                .build()
                .parseSignedClaims(token)  // 解析JWT令牌
                .getPayload();  // 获取载荷部分
        return claims.getSubject();  // 返回主题（用户名）
    }
    
    /**
     * 从JWT令牌中获取用户ID
     */
    public Integer getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()  // 创建JWT解析器
                .verifyWith(getSigningKey())  // 设置验证密钥
                .build()
                .parseSignedClaims(token)  // 解析JWT令牌
                .getPayload();  // 获取载荷部分
        return (Integer) claims.get("userId");  // 从载荷中获取用户ID
    }
    
    /**
     * 从JWT令牌中获取完整载荷信息
     */
    public Claims getClaimsFromToken(String token) {
        Claims claims = Jwts.parser()  // 创建JWT解析器
                .verifyWith(getSigningKey())  // 设置验证密钥
                .build()
                .parseSignedClaims(token)  // 解析JWT令牌
                .getPayload();  // 获取载荷部分
        return claims;  // 返回完整的载荷信息
    }
    
    /**
     * 验证JWT令牌
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);  // 尝试解析令牌
            return true;  // 解析成功，返回true
        } catch (JwtException | IllegalArgumentException e) {
            return false;  // 解析失败，返回false
        }
    }
    
    /**
     * 验证JWT令牌并返回详细信息
     */
    public Map<String, Object> validateTokenWithDetails(String token) {
        Map<String, Object> result = new HashMap<>();  // 创建结果Map
        
        if (token == null || token.trim().isEmpty()) {  // 检查令牌是否为空
            result.put("valid", false);  // 设置验证结果为false
            result.put("message", "令牌为空");  // 设置错误信息
            return result;  // 返回结果
        }
        
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);  // 尝试解析令牌
            result.put("valid", true);  // 解析成功，设置为true
            result.put("message", "令牌有效");  // 设置成功信息
            return result;  // 返回结果
        } catch (ExpiredJwtException e) {  // 捕获过期异常
            result.put("valid", false);  // 设置验证结果为false
            result.put("message", "令牌已过期");  // 设置过期信息
            return result;  // 返回结果
        } catch (UnsupportedJwtException e) {  // 捕获不支持的格式异常
            result.put("valid", false);  // 设置验证结果为false
            result.put("message", "不支持的令牌格式");  // 设置错误信息
            return result;  // 返回结果
        } catch (MalformedJwtException e) {  // 捕获格式错误异常
            result.put("valid", false);  // 设置验证结果为false
            result.put("message", "令牌格式错误");  // 设置错误信息
            return result;  // 返回结果
        } catch (SignatureException e) {  // 捕获签名错误异常
            result.put("valid", false);  // 设置验证结果为false
            result.put("message", "令牌签名无效");  // 设置错误信息
            return result;  // 返回结果
        } catch (IllegalArgumentException e) {  // 捕获参数错误异常
            result.put("valid", false);  // 设置验证结果为false
            result.put("message", "令牌参数错误");  // 设置错误信息
            return result;  // 返回结果
        } catch (Exception e) {  // 捕获其他所有异常
            result.put("valid", false);  // 设置验证结果为false
            result.put("message", "令牌验证失败: " + e.getMessage());  // 设置具体错误信息
            return result;  // 返回结果
        }
    }    
} 