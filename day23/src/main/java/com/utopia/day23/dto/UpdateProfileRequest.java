package com.utopia.day23.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateProfileRequest {

    // 昵称允许修改，但不能是空值或空白字符串
    @NotBlank(message = "昵称不能为空")
    @Size(max = 50, message = "昵称长度不能超过50个字符")
    private String nickname;

    //头像
    @Size(max = 500, message = "头像URL不能超过500字符")
    private String avatar;

    //简介
    @Size(max = 500, message = "简介不能超过500字符")
    private String bio;

    public UpdateProfileRequest() {
    }

    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {return avatar;}
    public void setAvatar(String avatar) {this.avatar = avatar;}

    public String getBio() {return bio;}
    public void setBio(String bio) {this.bio = bio;}
}

