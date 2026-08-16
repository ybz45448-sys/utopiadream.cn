package com.utopia.day23.repository;

import com.utopia.day23.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    // 测试：保存用户后能按用户名查询，字段映射正确
    @Test
    void save_shouldPersistAndFindByUsername() {
        // 准备：创建一个用户对象（密码是模拟的 BCrypt 哈希）
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("$2a$10$fakehash");
        user.setNickname("测试用户");

        // 执行：保存
        int rows = userRepository.save(user);

        // 断言：插入成功
        assertEquals(1, rows);

        // 按用户名查询
        User found = userRepository.findByusername("testuser");

        // 断言：查询到且字段映射正确
        assertNotNull(found);
        assertEquals("testuser", found.getUsername());
        assertEquals("测试用户", found.getNickname());
        assertEquals("$2a$10$fakehash", found.getPassword());
        assertTrue(found.getId() > 0);  // id 应该是数据库生成的正整数
    }

    // 测试：不存在的用户返回 null
    @Test
    void findByusername_shouldReturnNull_whenNotExists() {
        User found = userRepository.findByusername("不存在的用户");

        // 断言：查不到返回 null
        assertNull(found);
    }

    // 测试：更新资料后能查回新值
    @Test
    void updateProfile_shouldUpdateFields() {
        // 准备：先保存用户
        User user = new User();
        user.setUsername("updater");
        user.setPassword("$2a$10$fakehash");
        user.setNickname("旧昵称");
        userRepository.save(user);

        // 执行：更新昵称、头像、简介
        int rows = userRepository.updateProfile(
                "updater",
                "新昵称",
                "https://example.com/avatar.png",
                "Java 学习者"
        );

        // 断言：更新成功
        assertEquals(1, rows);

        // 查回验证新值
        User updated = userRepository.findByusername("updater");
        assertEquals("新昵称", updated.getNickname());
        assertEquals("https://example.com/avatar.png", updated.getAvatar());
        assertEquals("Java 学习者", updated.getBio());
    }
}

