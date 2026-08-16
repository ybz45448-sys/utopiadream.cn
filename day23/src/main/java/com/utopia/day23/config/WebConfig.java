// ============================================================
// config/WebConfig.java — CORS 跨域配置
// ============================================================
// CORS = 跨域资源共享
// 让前端的 localhost:3000 能调后端的 localhost:8080
//
// 第50天改造：
//   之前"允许哪个前端地址"写死在代码里（http://localhost:3000）
//   生产环境前端域名不同，必须改成从配置读取
//   用环境变量 CORS_ALLOWED_ORIGINS 覆盖，多个地址用英文逗号分隔
// ============================================================

package com.utopia.day23.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    // 从配置读取允许的跨域来源（第50天改造）
    // 默认值只服务本地开发；生产用环境变量 CORS_ALLOWED_ORIGINS 覆盖
    // 多个地址用英文逗号分隔，例如：
    //   CORS_ALLOWED_ORIGINS=https://api.mydomain.com,https://www.mydomain.com
    @Value("${cors.allowed-origins:http://localhost:3000}")
    private String allowedOrigins;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // 把"逗号分隔的字符串"拆成 String 数组
                // 这样 CORS 可以同时允许本地和线上多个前端地址
                String[] origins = allowedOrigins.split(",");

                registry.addMapping("/**")              // 所有 API 路径
                        .allowedOrigins(origins)        // ⭐ 允许的前端地址（来自配置）
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);        // 允许携带凭证（Cookie等）
            }
        };
    }
}


