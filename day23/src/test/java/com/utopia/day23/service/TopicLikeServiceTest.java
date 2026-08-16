package com.utopia.day23.service;

import com.utopia.day23.dto.LikeResponse;
import com.utopia.day23.exception.ResourceNotFoundException;
import com.utopia.day23.model.User;
import com.utopia.day23.repository.TopicRepository;
import com.utopia.day23.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TopicLikeServiceTest {

    // 假的 Repository，不会真的访问数据库
    @Mock
    private TopicRepository topicRepository;

    @Mock
    private UserRepository userRepository;

    // 真实的 Service，自动注入上面的假依赖
    @InjectMocks
    private TopicLikeService topicLikeService;

    // 测试场景一：话题不存在时，应该抛出异常
    @Test
    void toggleLike_shouldThrow_whenTopicNotFound() {
        // 准备：让 topicRepository.existsById(1) 返回 false
        when(topicRepository.existsById(1)).thenReturn(false);

        // 执行 + 断言：调用 toggleLike 应该抛 ResourceNotFoundException
        assertThrows(
                ResourceNotFoundException.class,
                () -> topicLikeService.toggleLike(1, "ceshi")
        );
    }

    // 测试场景二：用户不存在时，应该抛出异常
    @Test
    void toggleLike_shouldThrow_whenUserNotFound() {
        // 准备：话题存在，但用户查不到
        when(topicRepository.existsById(1)).thenReturn(true);
        when(userRepository.findByusername("ceshi")).thenReturn(null);

        // 执行 + 断言：应该抛 ResourceNotFoundException
        assertThrows(
                ResourceNotFoundException.class,
                () -> topicLikeService.toggleLike(1, "ceshi")
        );
    }

    // 测试场景三：未点赞时点击，应该插入关系并返回 liked=true
    @Test
    void toggleLike_shouldLike_whenNotLikedYet() {
        // 准备一个用户
        User user = new User();
        user.setId(10);

        // 话题存在
        when(topicRepository.existsById(1)).thenReturn(true);
        // 用户存在
        when(userRepository.findByusername("ceshi")).thenReturn(user);
        // 用户当前未点赞
        when(topicRepository.existsLike(1, 10)).thenReturn(false);
        // 点赞后数量变为 1
        when(topicRepository.countLikes(1)).thenReturn(1L);

        // 执行
        LikeResponse result = topicLikeService.toggleLike(1, "ceshi");

        // 断言
        assertTrue(result.isLiked());
        assertEquals(1L, result.getLikes());

        // 验证：应该调用了 addLike（插入点赞）
        verify(topicRepository).addLike(1, 10);
        // 验证：不应该调用 removeLike（取消点赞）
        verify(topicRepository, never()).removeLike(1, 10);
    }

    // 测试场景四：已点赞时再点，应该删除关系并返回 liked=false
    @Test
    void toggleLike_shouldUnlike_whenAlreadyLiked() {
        User user = new User();
        user.setId(10);

        when(topicRepository.existsById(1)).thenReturn(true);
        when(userRepository.findByusername("ceshi")).thenReturn(user);
        // 用户当前已点赞
        when(topicRepository.existsLike(1, 10)).thenReturn(true);
        when(topicRepository.countLikes(1)).thenReturn(0L);

        // 执行
        LikeResponse result = topicLikeService.toggleLike(1, "ceshi");

        // 断言
        assertFalse(result.isLiked());
        assertEquals(0L, result.getLikes());

        // 验证：应该调用了 removeLike
        verify(topicRepository).removeLike(1, 10);
        // 验证：不应该调用 addLike
        verify(topicRepository, never()).addLike(1, 10);
    }
}

