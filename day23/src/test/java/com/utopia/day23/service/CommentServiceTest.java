package com.utopia.day23.service;

import com.utopia.day23.exception.ForbiddenOperationException;
import com.utopia.day23.exception.ResourceNotFoundException;
import com.utopia.day23.model.Comment;
import com.utopia.day23.repository.CommentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentService commentService;

    // 测试场景一：评论不存在时，删除应抛出异常
    @Test
    void delete_shouldThrow_whenCommentNotFound() {
        // 准备：评论查不到
        when(commentRepository.findById(1)).thenReturn(null);

        // 执行 + 断言：应抛 ResourceNotFoundException
        assertThrows(
                ResourceNotFoundException.class,
                () -> commentService.delete(1, "ceshi")
        );

        // 验证：评论不存在，不该真的删除
        verify(commentRepository, never()).delete(1);
    }

    // 测试场景二：当前用户不是评论作者时，删除应抛出异常
    @Test
    void delete_shouldThrow_whenNotAuthor() {
        // 准备：评论存在，作者是其他用户
        Comment comment = new Comment();
        comment.setId(1);
        comment.setAuthor("other");

        when(commentRepository.findById(1)).thenReturn(comment);

        // 执行 + 断言：ceshi 不是作者，应抛 ForbiddenOperationException
        assertThrows(
                ForbiddenOperationException.class,
                () -> commentService.delete(1, "ceshi")
        );

        // 验证：无权删除时，不该真的删除
        verify(commentRepository, never()).delete(1);
    }

    // 测试场景三：作者本人删除评论，应该成功删除
    @Test
    void delete_shouldDelete_whenIsAuthor() {
        // 准备：评论存在，作者就是当前用户
        Comment comment = new Comment();
        comment.setId(1);
        comment.setAuthor("ceshi");

        when(commentRepository.findById(1)).thenReturn(comment);

        // 执行
        commentService.delete(1, "ceshi");

        // 验证：删除方法应该被调用
        verify(commentRepository).delete(1);
    }
}
