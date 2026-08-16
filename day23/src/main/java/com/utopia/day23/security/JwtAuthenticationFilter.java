package com.utopia.day23.security;

import com.utopia.day23.service.UserService;
import com.utopia.day23.util.JwtUtil;   // ⭐ 新增
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final JwtUtil jwtUtil;   // ⭐ 新增

    // 第50天改造：增加 JwtUtil 参数
    // SecurityConfig 里 new 的时候把配置好的 Bean 传进来
    public JwtAuthenticationFilter(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }
    // …… doFilterInternal 方法体不变，只改下面一处调用

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 从请求头读取 Authorization
        String authHeader = request.getHeader("Authorization");

        // 没有 Bearer Token 时先继续执行
        // 是否必须登录，由 SecurityConfig 决定
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 去掉 "Bearer " 前缀，得到真正的 JWT
        String token = authHeader.substring(7);

        // 解析 Token，得到用户名
        // 第50天改造：JwtUtil.parseToken → jwtUtil.parseToken
        String username = jwtUtil.parseToken(token);

        // 只有 Token 有效、用户存在、当前请求还没有认证信息时才建立认证
        if (username != null
                && userService.getUserInfo(username) != null
                && SecurityContextHolder.getContext().getAuthentication() == null) {

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            Collections.emptyList()
                    );

            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            // 把当前用户保存到 Spring Security 的认证上下文
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 继续执行后续过滤器和 Controller
        filterChain.doFilter(request, response);
    }
}

