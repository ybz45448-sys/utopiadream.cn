// ============================================================
// model/Comment.java — 评论实体类
// 对应数据库 comments 表的每一行
// ============================================================

package com.utopia.day23.model;

import java.time.OffsetDateTime;

public class Comment {
    private int id;           // 评论 ID（自动生成）
    private int topicId;      // 所属话题 ID（关联 topics 表）
    private String author;    // 评论作者
    private String content;   // 评论内容
    private OffsetDateTime createdAt; // 创建时间（带时区的时间戳，由数据库默认填充）

    public Comment() {}

    public Comment(int topicId, String author, String content) {
        this.topicId = topicId;
        this.author = author;
        this.content = content;
        // createdAt 不在这里设置：由数据库 DEFAULT CURRENT_TIMESTAMP 填充
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getTopicId() { return topicId; }
    public void setTopicId(int topicId) { this.topicId = topicId; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}

