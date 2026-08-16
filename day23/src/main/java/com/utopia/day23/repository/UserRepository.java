

package com.utopia.day23.repository;

import com.utopia.day23.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public User findByusername(String username){
        try{
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM users WHERE username = ?",
                    (rs, rowNum) -> {
                        User user = new User();
                        user.setId(rs.getInt("id"));
                        user.setUsername(rs.getString("username"));
                        user.setPassword(rs.getString("password"));
                        user.setNickname(rs.getString("nickname"));
                        user.setAvatar(rs.getString("avatar"));
                        user.setBio(rs.getString("bio"));
                        return user;
                    },username
            );
        } catch (Exception e) {
            return null;
        }
    }

    public int save(User user){
        return jdbcTemplate.update("INSERT INTO users(username,password,nickname) VALUES(?,?,?)",
                user.getUsername(),user.getPassword(),user.getNickname());
    }

    public int updateProfile(
            String username,
            String nickname,
            String avatar,
            String bio
    ) {
        // username 来自 JWT，不来自前端请求体。
        // 其他三个字段来自经过 DTO 校验的请求。
        return jdbcTemplate.update(
                """
                UPDATE users
                SET nickname = ?, avatar = ?, bio = ?
                WHERE username = ?
                """,
                nickname,
                avatar,
                bio,
                username
        );
    }


}
