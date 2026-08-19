// ============================================================
// config/SecurityConfig.java — 安全配置
// ============================================================
// 作用：告诉 Spring Security"别拦截我们的 API"
// 我们只需要 Spring Security 的密码加密功能（BCrypt）
// 不需要它的登录拦截功能
// ============================================================

package com.utopia.day23.config;

import com.utopia.day23.security.JwtAuthenticationFilter;
import com.utopia.day23.service.UserService;
import com.utopia.day23.util.JwtUtil;   // ⭐ 新增
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;



@Configuration  // 标记这是一个"配置类"
@EnableWebSecurity
public class SecurityConfig {

    // ============================================================
    // ⭐ BCryptPasswordEncoder = 密码加密工具
    //
    // 为什么不能直接存明文密码？
    //   如果数据库被黑客拿到，所有用户的密码就暴露了
    //   用 BCrypt 加密后，即使数据库被拿，也解不出原始密码
    //
    // 加密原理：
    //   用户注册：密码 "123456" → BCrypt 加密 → "$2a$10$..." → 存数据库
    //   用户登录：输入 "123456" → BCrypt 比对 → 和数据库里加密的对比
    //   但 "123456" 本身不会出现在数据库里
    //
    // @Bean = 把这个方法返回的对象交给 Spring Boot 管理
    //   其他地方可以直接 @Autowired 来用
    // ============================================================
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ============================================================
    // SecurityFilterChain = 安全过滤链
    // 这里配置"哪些请求不需要登录"
    //
    // authorizeHttpRequests(auth -> auth
    //     .requestMatchers("/**").permitAll()  // 所有请求都放行
    // )
    //
    // 也就是：暂时关闭 Spring Security 的拦截功能
    // 我们只用它的密码加密
    // ============================================================
    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            UserService userService,
            JwtUtil jwtUtil        // ⭐ 第50天新增：方法参数注入
    ) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint((request, response, authException) -> {
                            // ⭐ 未登录 / token 过期 → 返回 401（而不是 Spring 默认的 403）
                            //   这样前端就能区分：401=身份失效，403=没权限
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write(
                                    "{\"success\":false,\"message\":\"登录已过期，请重新登录\"}"
                            );
                        })
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()



                        // 注册和登录允许匿名访问
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/register",
                                "/api/login"
                        ).permitAll()

                        // 话题和评论和用户的查询接口允许匿名访问
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/topics",
                                "/api/topics/*",
                                "/api/topics/*/comments",
                                "/api/users/*"
                        ).permitAll()

                        // 其他接口必须登录
                        .anyRequest().authenticated()
                )
                // 在 Spring Security 的用户名密码过滤器之前执行 JWT Filter
                // 在 Spring Security 的用户名密码过滤器之前执行 JWT Filter
                .addFilterBefore(
                        new JwtAuthenticationFilter(userService, jwtUtil),   // ⭐ 加了 jwtUtil
                        UsernamePasswordAuthenticationFilter.class
                );


        return http.build();
    }

}