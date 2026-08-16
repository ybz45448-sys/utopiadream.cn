package com.utopia.day23.repository;

import com.utopia.day23.model.Comment;
import com.utopia.day23.model.Topic;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TopicRepository topicRepository;

    // 工具方法：先保存一个话题，返回它的 id（评论要挂在话题下）
    private int createTopic() {
        Topic topic = new Topic("评论测试话题", "正文", "ceshi", "AI");
        topicRepository.save(topic);

        List<Topic> all = topicRepository.findAll();
        return all.get(all.size() - 1).getId();
    }

    // 测试：保存评论后能按话题查询到，字段映射正确
    @Test
    void save_shouldPersistAndFindByTopicId() {
        int topicId = createTopic();

        Comment comment = new Comment(topicId, "ceshi", "这是一条测试评论");

        int rows = commentRepository.save(comment);

        // 断言：插入成功
        assertEquals(1, rows);

        // 按话题查询评论
        List<Comment> comments = commentRepository.findByTopicId(topicId);

        // 断言：能查到刚才插入的评论
        assertFalse(comments.isEmpty());
        assertEquals("这是一条测试评论", comments.get(comments.size() - 1).getContent());
        assertEquals("ceshi", comments.get(comments.size() - 1).getAuthor());
        // createdAt 由数据库默认填充，查询回来应为非空时间戳
        assertNotNull(comments.get(comments.size() - 1).getCreatedAt());
    }

    // 测试：删除评论后，按 ID 查不到
    @Test
    void delete_shouldRemoveComment() {
        int topicId = createTopic();

        Comment comment = new Comment(topicId, "ceshi", "待删除评论");
        commentRepository.save(comment);

        List<Comment> comments = commentRepository.findByTopicId(topicId);
        Comment saved = comments.get(comments.size() - 1);

        // 执行删除
        int rows = commentRepository.delete(saved.getId());

        // 断言：删除成功，且查不到
        assertEquals(1, rows);
        assertNull(commentRepository.findById(saved.getId()));
    }
}

