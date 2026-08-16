// ============================================================
// CommentController.java — 评论 API 接口
// ============================================================

package com.utopia.day23;

import com.utopia.day23.dto.CreateCommentRequest;
import com.utopia.day23.model.Comment;
import com.utopia.day23.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CommentController {
    @Autowired
    private CommentService commentService;

    // 获取某个话题的所有评论
    @GetMapping("/api/topics/{topicId}/comments")
    public List<Comment> getComments(@PathVariable int topicId) {
        return commentService.getByTopicId(topicId);
    }

    // 发表评论
    @PostMapping("/api/topics/{topicId}/comments")
    public Comment createComment(
            @PathVariable int topicId,
            @Valid @RequestBody CreateCommentRequest request,
            Authentication authentication
    ) {
        // 当前用户来自 JWT，不从前端请求体读取 author。
        String username = authentication.getName();

        // DTO 已经完成评论内容的非空和长度校验。
        Comment comment = new Comment(
                topicId,
                username,
                request.getContent()
        );

        return commentService.create(comment);
    }



    // DELETE /api/comments/{id} — 删除评论
    @DeleteMapping("/api/comments/{id}")
    public String deleteComment(
            @PathVariable int id,
            Authentication authentication
    ) {
        // Authentication 来自 Spring Security 的 SecurityContext。
        // 当前用户名由 JWT Filter 根据 Token 解析得到。
        String username = authentication.getName();

        // 把评论 ID 和当前用户名交给 Service。
        // Service 会查询评论作者并进行所有权校验。
        commentService.delete(id, username);

        return "删除成功";
    }


}
