// ============================================================
// model/Topic.java — 话题实体类
// 对应数据库 topics 表的每一行数据
// ============================================================

package com.utopia.day23.model;

import java.time.OffsetDateTime;

public class Topic {
    // 属性 = 数据库表的列
    private int id;           // 话题ID（数据库自动生成）
    private String title;     // 标题
    private String content;   // 完整正文
    private String author;    // 作者
    private String tag;       // 分类（如"前端"、"后端"）
    private int replies;      // 回复数（默认 0）
    private int likes;        // 点赞数（默认 0）
    private OffsetDateTime createdAt; // 创建时间（带时区的时间戳，由数据库默认填充）

    // 无参构造（JdbcTemplate 查数据时用）
    public Topic() {}

    // 有参构造（创建新话题时用）
    // 只需要用户传 4 个参数，其他自动赋默认值
    public Topic(String title, String content, String author, String tag) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.tag = tag;
        this.replies = 0;      // 新话题回复数为 0
        this.likes = 0;        // 新话题点赞数为 0
        // createdAt 不在这里设置：由数据库 DEFAULT CURRENT_TIMESTAMP 填充
    }

    // Getter / Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }
    public int getReplies() { return replies; }
    public void setReplies(int replies) { this.replies = replies; }
    public int getLikes() { return likes; }
    public void setLikes(int likes) { this.likes = likes; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
