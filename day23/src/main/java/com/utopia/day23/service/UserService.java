// ============================================================
// service/UserService.java — 用户业务逻辑层
// ============================================================
// 职责：处理注册、登录相关的业务逻辑
//
// 注册流程：
//   1. 检查用户名是否已被注册
//   2. 用 BCrypt 加密密码（不存明文！）
//   3. 保存到数据库
//
// 为什么加密在 Service 层做？
//   因为这是"业务规则"——密码不能明文存储
//   Repository 只管"存数据"，不管"怎么处理数据"
// ============================================================

package com.utopia.day23.service;

import com.utopia.day23.exception.ResourceNotFoundException;
import com.utopia.day23.model.User;
import com.utopia.day23.repository.UserRepository;
import com.utopia.day23.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService
{
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public int register(String username, String password, String nickname)
    {
        User existingUser = userRepository.findByusername(username);
        if (existingUser != null){
            return -1;
        }

        String encodedPassword = passwordEncoder.encode(password);
        User user = new User(username, encodedPassword, nickname);
        userRepository.save(user);
        return 1;
    }

    public String login(String username, String password)
    {
        User user = userRepository.findByusername(username);
        if(user == null){
            return null;
        }
        boolean encodedPassword = passwordEncoder.matches(password, user.getPassword());
        if(!encodedPassword){
            return null;
        }

        // 第50天改造：JwtUtil.generateToken → jwtUtil.generateToken
        String token = jwtUtil.generateToken(username);

        return token;
    }

    // ============================================================
    // 根据用户名获取用户信息
    // 前端在 Token 里只存了用户名
    // 通过这个方法可以获取用户的完整信息（昵称等）
    // 不返回密码（安全）
    // ============================================================
    public User getUserInfo(String username) {
        return userRepository.findByusername(username);
    }

    public User updateProfile(
            String username,
            String nickname,
            String avatar,
            String bio
    ) {
        // username 来自 JWT，不来自前端请求体。
        // nickname 已经由 UpdateProfileRequest 完成校验。
        int updatedRows = userRepository.updateProfile(
                username,
                nickname,
                avatar,
                bio
        );

        // 没有更新任何数据，说明 Token 对应的用户不存在。
        if (updatedRows == 0) {
            throw new ResourceNotFoundException("用户不存在");
        }

        // 更新成功后重新查询最新用户资料。
        // 这样返回给前端的数据就是数据库中的最新值。
        return userRepository.findByusername(username);
    }

}