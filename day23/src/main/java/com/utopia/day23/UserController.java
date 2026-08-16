// ============================================================
// UserController.java — 用户控制器
// ============================================================
// 功能：处理注册请求
//
// POST /api/register
// 前端发送：{ "username": "xiaoming", "password": "123456", "nickname": "小明" }
// 后端返回：注册成功 或 用户名已存在
// ============================================================

package com.utopia.day23;

import com.utopia.day23.dto.LoginRequest;
import com.utopia.day23.dto.RegisterRequest;
import com.utopia.day23.dto.UpdateProfileRequest;
import com.utopia.day23.dto.UserResponse;
import com.utopia.day23.exception.ResourceNotFoundException;
import com.utopia.day23.model.User;
import com.utopia.day23.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    private UserResponse toUserResponse(User user) {
        // Entity 可能包含 password，
        // 这里只复制允许返回给前端的公开字段。
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                user.getAvatar(),
                user.getBio()
        );
    }


    @PostMapping("/api/register")
    public String register(
            @Valid @RequestBody RegisterRequest request
    ) {
        // DTO 已经完成注册字段校验。
        // 这里读取校验通过后的字段。
        String username = request.getUsername();
        String password = request.getPassword();
        String nickname = request.getNickname();

        int num = userService.register(username,password,nickname);
        if (num == 1) {
            return "注册成功";
        } else  {
            return "注册失败";
        }

    }


    @PostMapping("/api/login")
    public Map<String, Object> login(
            @Valid @RequestBody LoginRequest request
    ) {
        // DTO 已经完成用户名和密码的格式校验。
        // 这里直接读取校验通过后的字段。
        String username = request.getUsername();
        String password = request.getPassword();


        String token = userService.login(username, password);
        Map<String, Object> result = new HashMap<>();

        if (token == null) {
            result.put("success", false);
            result.put("message", "用户名或密码错误");
            return result;
        }

        User user = userService.getUserInfo(username);
        Map<String, Object> safeUser = new HashMap<>();
        safeUser.put("id", user.getId());
        safeUser.put("username", user.getUsername());
        safeUser.put("nickname", user.getNickname());
        safeUser.put("avatar", user.getAvatar());
        safeUser.put("bio", user.getBio());

        result.put("success", true);
        result.put("token", token);
        result.put("user", safeUser);
        return result;
    }

    // ============================================================
    // GET /api/me — 获取当前登录用户信息
    // 前端请求时在 header 里带 Token
    // 后端用 JwtUtil 解析 Token → 拿到用户名 → 查数据库
    //
    // 这个接口的作用：
    //   前端刷新页面后，可以从 localStorage 取出 Token
    //   调这个接口验证 Token 是否有效，同时获取用户信息
    // ============================================================
    @GetMapping("/api/me")
    public UserResponse getMe(Authentication authentication) {
        // Authentication 由 Spring Security 自动注入。
        // 当前用户名来自 JWT Filter，而不是来自前端请求参数。
        String username = authentication.getName();

        // 根据 JWT 中的用户名查询数据库。
        User user = userService.getUserInfo(username);

        // Token 可能仍然有效，但对应用户可能已经被删除。
        if (user == null) {
            throw new ResourceNotFoundException("用户不存在");
        }

        // 只返回安全 DTO，绝不返回 password。
        return toUserResponse(user);
    }


    @PutMapping("/api/me")
    public UserResponse updateProfile(Authentication authentication,
                                      @Valid @RequestBody UpdateProfileRequest request){
        String username = authentication.getName();
        User user = userService.updateProfile(username, request.getNickname(),
                request.getAvatar(), request.getBio());

        return toUserResponse(user);
    }

    @GetMapping("/api/users/{username}")
    public UserResponse getPublicProfile(
            @PathVariable String username
    ) {
        // username 来自 URL，表示游客想查看哪个用户的公开资料。
        User user = userService.getUserInfo(username);

        // 用户不存在时交给全局异常处理器，返回 404 JSON。
        if (user == null) {
            throw new ResourceNotFoundException("用户不存在");
        }

        // 只返回安全 DTO，不返回 password。
        return toUserResponse(user);
    }// 正常返回用户对象（包含 username/nickname 等）
}







