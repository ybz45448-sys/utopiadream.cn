package com.utopia.day23.repository;

import com.utopia.day23.model.Topic;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// @SpringBootTest：加载完整应用上下文（连真实数据库）
// @ActiveProfiles("test")：激活测试环境，连接 utopia_test
// @Transactional：每个测试结束后回滚，不污染数据库
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TopicRepositoryTest {

    @Autowired
    private TopicRepository topicRepository;

    // 测试：保存话题后能通过 ID 查回，字段映射正确
    @Test
    void save_shouldPersistAndFindById() {
        // 准备：创建一个话题对象
        Topic topic = new Topic(
                "测试标题",
                "测试正文",
                "ceshi",
                "AI"
        );

        // 执行：保存到测试数据库
        int rows = topicRepository.save(topic);

        // 断言：影响行数为 1
        assertEquals(1, rows);

        // 查回最后一条话题
        List<Topic> all = topicRepository.findAll();
        Topic saved = all.get(all.size() - 1);

        // 断言：保存的字段能正确映射回来
        assertEquals("测试标题", saved.getTitle());
        assertEquals("测试正文", saved.getContent());
        assertEquals("ceshi", saved.getAuthor());
        assertEquals("AI", saved.getTag());
        // createdAt 由数据库默认填充，查询回来应为非空时间戳
        assertNotNull(saved.getCreatedAt());
    }

    // 测试：删除话题后，通过 ID 查不到
    @Test
    void delete_shouldRemoveTopic() {
        // 准备：先保存一条话题
        Topic topic = new Topic("待删除", "正文", "ceshi", "AI");
        topicRepository.save(topic);

        // 找到刚保存的话题
        List<Topic> all = topicRepository.findAll();
        Topic saved = all.get(all.size() - 1);

        // 执行：删除
        int rows = topicRepository.delete(saved.getId());

        // 断言：影响行数为 1，且查不到
        assertEquals(1, rows);
        assertNull(topicRepository.findById(saved.getId()));
    }

    // 测试：关键词搜索能匹配标题
    @Test
    void search_shouldFindByKeyword() {
        // 准备：保存两条话题，一条标题含"Java"，一条不含
        Topic javaTopic = new Topic("Java 入门", "学习", "ceshi", "后端");
        topicRepository.save(javaTopic);

        Topic otherTopic = new Topic("其他话题", "学习", "ceshi", "AI");
        topicRepository.save(otherTopic);

        // 执行：搜索 "Java"
        List<Topic> result = topicRepository.search("Java", "");

        // 断言：至少能找到一条，且标题包含 Java
        assertFalse(result.isEmpty());
        assertTrue(result.stream()
                .anyMatch(t -> t.getTitle().contains("Java")));
    }
}
