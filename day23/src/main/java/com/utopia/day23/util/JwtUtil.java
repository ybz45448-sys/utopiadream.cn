package com.utopia.day23.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

// ============================================================
// util/JwtUtil.java — JWT 生成与解析工具
// ============================================================
// 第50天改造：从"静态工具类"变成"Spring Bean"
//
// 为什么必须改？
//   1. 之前密钥写死在代码里（KEY 常量），提交 git 后永久泄漏
//   2. 静态方法无法读取 Spring 配置（@Value 只能注入 Bean 实例字段）
//   3. 改成 Bean 后，密钥从配置读取，生产用环境变量 JWT_SECRET 覆盖
//
// 改造要点：
//   @Component   → 交给 Spring 管理，成为容器里的一个 Bean
//   @Value       → 从配置读取值；冒号后是默认值（本地开发不设环境变量也能跑）
//   静态方法     → 变成实例方法（生成和解析都用 this.secret）
// ============================================================
@Component
public class JwtUtil {

    // 从配置读取 JWT 密钥（第50天改造）
    // 属性优先级（Spring 宽松绑定，优先级从高到低）：
    //   命令行参数 --jwt.secret=xxx
    //   > 环境变量 JWT_SECRET              ← 生产用这个
    //   > application-prod.properties 的 jwt.secret
    //   > 冒号后面的默认值（仅本地开发兜底）
    @Value("${jwt.secret:UtopiaSecretKey2026SpringBootJWTAuthentication}")
    private String secret;

    // 过期时间：默认 24 小时（单位毫秒）
    // 生产可用环境变量 JWT_EXPIRATION 覆盖
    @Value("${jwt.expiration:86400000}")
    private long expiration;

    // 1. 生成 Token（static → 实例方法）
    public String generateToken(String username) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .compact();
    }

    // 2. 验证 Token（返回用户名，无效返回 null）
    public String parseToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (Exception e) {
            return null;
        }
    }
}
