// ============================================================
// repository/CommentRepository.java — 评论数据库操作
// ============================================================

package com.utopia.day23.repository;

import com.utopia.day23.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.time.OffsetDateTime;

@Repository
public class CommentRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Comment findById(int id) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT id, topic_id, author, content, created_at FROM comments WHERE id = ?",
                    (rs, row) -> {
                        Comment comment = new Comment();

                        // 把数据库查询结果转换成 Comment 对象
                        comment.setId(rs.getInt("id"));
                        comment.setTopicId(rs.getInt("topic_id"));
                        comment.setAuthor(rs.getString("author"));
                        comment.setContent(rs.getString("content"));
                        comment.setCreatedAt(rs.getObject("created_at", OffsetDateTime.class));

                        return comment;
                    },
                    id
            );
        } catch (EmptyResultDataAccessException e) {
            // 查询不到评论时返回 null，
            // 由 Service 层统一转换为“评论不存在”
            return null;
        }
    }


    // 根据话题 ID 查询所有评论（按时间正序，旧的在前）
    public List<Comment> findByTopicId(int topicId) {
        return jdbcTemplate.query("SELECT * FROM comments WHERE topic_id = ? ORDER BY id",
                (rs, row) -> {
                    Comment c = new Comment();
                    c.setId(rs.getInt("id"));
                    c.setTopicId(rs.getInt("topic_id"));
                    c.setAuthor(rs.getString("author"));
                    c.setContent(rs.getString("content"));
                    c.setCreatedAt(rs.getObject("created_at", OffsetDateTime.class));
                    return c;
                }, topicId);
    }

    // 新增评论
    public int save(Comment comment) {
        return jdbcTemplate.update(
                "INSERT INTO comments (topic_id, author, content) VALUES (?, ?, ?)",
                comment.getTopicId(), comment.getAuthor(),
                comment.getContent()
        );
    }

    // ============================================================
    // 根据 ID 删除评论
    // ============================================================
    public int delete(int id) {
        return jdbcTemplate.update("DELETE FROM comments WHERE id = ?", id);
    }

    // ============================================================
    // 根据话题 ID 删除该话题下的所有评论（删除话题时用）
    // ============================================================
    public int deleteByTopicId(int topicId) {
        return jdbcTemplate.update("DELETE FROM comments WHERE topic_id = ?", topicId);
    }
}

