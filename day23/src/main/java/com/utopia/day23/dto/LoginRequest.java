package com.utopia.day23.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginRequest {

    // 用户名不能是 null、空字符串或只包含空格
    @NotBlank(message = "用户名不能为空")
    @Size(max = 50, message = "用户名长度不能超过50个字符")
    private String username;

    // 密码不能是空字符串，并限制长度范围
    @NotBlank(message = "密码不能为空")
    @Size(min = 4, max = 100, message = "密码长度必须在4到100个字符之间")
    private String password;

    public LoginRequest() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

