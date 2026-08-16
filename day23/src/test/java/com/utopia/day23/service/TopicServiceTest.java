package com.utopia.day23.service;

import com.utopia.day23.exception.ForbiddenOperationException;
import com.utopia.day23.exception.ResourceNotFoundException;
import com.utopia.day23.model.Topic;
import com.utopia.day23.repository.CommentRepository;
import com.utopia.day23.repository.TopicRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TopicServiceTest {

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private TopicService topicService;

    // 测试场景一：话题不存在时，删除应抛出异常
    @Test
    void delete_shouldThrow_whenTopicNotFound() {
        // 准备：话题查不到
        when(topicRepository.findById(1)).thenReturn(null);

        // 执行 + 断言：应抛 ResourceNotFoundException
        assertThrows(
                ResourceNotFoundException.class,
                () -> topicService.delete(1, "ceshi")
        );

        // 验证：既然话题不存在，两个删除方法都不该被调用
        verify(topicRepository, never()).delete(1);
        verify(commentRepository, never()).deleteByTopicId(1);
    }

    // 测试场景二：当前用户不是作者时，删除应抛出异常
    @Test
    void delete_shouldThrow_whenNotAuthor() {
        // 准备：话题存在，作者是其他用户
        Topic topic = new Topic();
        topic.setId(1);
        topic.setAuthor("other");

        when(topicRepository.findById(1)).thenReturn(topic);

        // 执行 + 断言：ceshi 不是作者，应抛 ForbiddenOperationException
        assertThrows(
                ForbiddenOperationException.class,
                () -> topicService.delete(1, "ceshi")
        );

        // 验证：无权删除时，两个删除方法都不该被调用
        verify(topicRepository, never()).delete(1);
        verify(commentRepository, never()).deleteByTopicId(1);
    }

    // 测试场景三：作者本人删除，应该删评论 + 删话题
    @Test
    void delete_shouldDelete_whenIsAuthor() {
        // 准备：话题存在，作者就是当前用户
        Topic topic = new Topic();
        topic.setId(1);
        topic.setAuthor("ceshi");

        when(topicRepository.findById(1)).thenReturn(topic);

        // 执行
        topicService.delete(1, "ceshi");

        // 验证：两个删除方法都应该被调用
        verify(commentRepository).deleteByTopicId(1);
        verify(topicRepository).delete(1);
    }
}
