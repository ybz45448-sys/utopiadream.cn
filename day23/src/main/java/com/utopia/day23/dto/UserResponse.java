package com.utopia.day23.dto;

public class UserResponse {

    // 用户数据库主键
    private int id;
    // 登录用户名，也是后端识别用户身份的依据
    private String username;
    // 页面显示名称
    private String nickname;
    // 头像
    private String avatar;
    //简介
    private String bio;

    public UserResponse() {
    }

    public UserResponse(
            int id,
            String username,
            String nickname,
            String avatar,
            String bio
    ) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
        this.avatar = avatar;
        this.bio = bio;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
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

