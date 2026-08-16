// ============================================================
// service/CommentService.java — 评论业务逻辑
// ============================================================

package com.utopia.day23.service;

import com.utopia.day23.exception.ForbiddenOperationException;
import com.utopia.day23.exception.ResourceNotFoundException;
import com.utopia.day23.model.Comment;
import com.utopia.day23.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    public List<Comment> getByTopicId(int topicId) {
        return commentRepository.findByTopicId(topicId);
    }

    public Comment create(Comment comment) {
        commentRepository.save(comment);

        return comment;
    }

    // ===== 删除评论 =====
    public void delete(int id, String username) {
        // 先查询要删除的评论
        Comment comment = commentRepository.findById(id);

        // 评论不存在，返回 404
        if (comment == null) {
            throw new ResourceNotFoundException("评论不存在");
        }

        // 当前用户不是评论作者，返回 403
        if (!username.equals(comment.getAuthor())) {
            throw new ForbiddenOperationException("无权删除此评论");
        }

        // 只有作者本人才能执行删除
        commentRepository.delete(id);
    }


    // ===== 删除某话题的所有评论 =====
    public void deleteByTopicId(int topicId) {
        commentRepository.deleteByTopicId(topicId);
    }


}

