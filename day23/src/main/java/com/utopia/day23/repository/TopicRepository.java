// ============================================================
// repository/TopicRepository.java — 话题数据访问层
// 操作数据库 topics 表（查询全部、按ID查、新增）
// ============================================================

package com.utopia.day23.repository;

import com.utopia.day23.model.Topic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.time.OffsetDateTime;

@Repository
public class TopicRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    // ===== 查询全部话题（按时间倒序，最新的在最前面） =====
    public List<Topic> findAll() {
        String sql = """
        SELECT topics.*,
               (
                   SELECT COUNT(*)
                   FROM comments
                   WHERE comments.topic_id = topics.id
               ) AS replies,
               (
                   SELECT COUNT(*)
                   FROM topic_likes
                   WHERE topic_likes.topic_id = topics.id
               ) AS like_count
        FROM topics
        ORDER BY topics.id DESC
        """;


        return jdbcTemplate.query(
                sql, (rs, row) -> {
                    Topic t = new Topic();
                    // 把数据库的每一列设到 Topic 对象的属性里
                    t.setId(rs.getInt("id"));              // 取 id 列
                    t.setTitle(rs.getString("title"));     // 取 title 列
                    t.setContent(rs.getString("content")); // 取 content 列
                    t.setAuthor(rs.getString("author"));   // 取 author 列
                    t.setTag(rs.getString("tag"));         // 取 tag 列
                    t.setReplies(rs.getInt("replies"));    // 取 replies 列
                    // 点赞数来自 topic_likes，而不是旧的 topics.likes 字段。
                    t.setLikes(rs.getInt("like_count"));
                    t.setCreatedAt(rs.getObject("created_at", OffsetDateTime.class)); // 取 created_at 列
                    return t;
                });
    }

    public List<String> findAllTags() {
        // DISTINCT 去除重复标签。
        // ORDER BY 让标签在前端显示时保持稳定顺序。
        return jdbcTemplate.queryForList(
                """
                SELECT DISTINCT tag
                FROM topics
                WHERE tag IS NOT NULL
                  AND TRIM(tag) <> ''
                ORDER BY tag
                """,
                String.class
        );
    }


    // ===== 按关键词和分类搜索话题 =====
    public List<Topic> search(String keyword, String tag) {
        StringBuilder sql = new StringBuilder("""
            SELECT topics.*,
                   (
                       SELECT COUNT(*)
                       FROM comments
                       WHERE comments.topic_id = topics.id
                   ) AS replies,
                   (
                       SELECT COUNT(*)
                       FROM topic_likes
                       WHERE topic_likes.topic_id = topics.id
                   ) AS like_count
            FROM topics
            WHERE 1 = 1
            """);


        List<Object> params = new ArrayList<>();

        // 有关键词时搜索标题和正文
        if (keyword != null && !keyword.isBlank()) {
            sql.append("""
                AND (
                    title ILIKE ?
                    OR content ILIKE ?
                )
                """);

            String searchPattern = "%" + keyword + "%";

            // SQL 中有两个关键词占位符，所以传入两次
            params.add(searchPattern);
            params.add(searchPattern);
        }

        // 有分类时增加 tag 条件
        if (tag != null && !tag.isBlank()) {
            sql.append(" AND tag = ? ");
            params.add(tag);
        }

        sql.append(" ORDER BY id DESC");

        return jdbcTemplate.query(
                sql.toString(),
                (rs, row) -> {
                    Topic topic = new Topic();

                    // 将数据库字段映射为 Topic 对象
                    topic.setId(rs.getInt("id"));
                    topic.setTitle(rs.getString("title"));
                    topic.setContent(rs.getString("content"));
                    topic.setAuthor(rs.getString("author"));
                    topic.setTag(rs.getString("tag"));
                    // 搜索结果也必须使用 topic_likes 的实时点赞数。
                    topic.setLikes(rs.getInt("like_count"));
                    topic.setReplies(rs.getInt("replies"));
                    topic.setCreatedAt(rs.getObject("created_at", OffsetDateTime.class));

                    return topic;
                },
                params.toArray()
        );
    }


    // ===== 搜索、分类和分页 =====
    public TopicPageData searchPage(
            String keyword,
            String tag,
            int page,
            int pageSize
    ) {
        // 保存动态 SQL 的筛选条件。
        // 查询当前页和查询总数需要使用相同的条件。
        StringBuilder conditionSql = new StringBuilder(" WHERE 1 = 1 ");
        List<Object> conditionParams = new ArrayList<>();

        // 有关键词时，同时搜索标题和正文。
        if (keyword != null && !keyword.isBlank()) {
            conditionSql.append("""
                AND (
                    title ILIKE ?
                    OR content ILIKE ?
                )
                """);

            String searchPattern = "%" + keyword + "%";

            // SQL 中有两个关键词占位符，所以参数要添加两次。
            conditionParams.add(searchPattern);
            conditionParams.add(searchPattern);
        }

        // 有分类时，增加精确分类条件。
        if (tag != null && !tag.isBlank()) {
            conditionSql.append(" AND tag = ? ");
            conditionParams.add(tag);
        }

        // 计算当前页要跳过多少条数据。
        int offset = (page - 1) * pageSize;

        // 查询当前页数据。
        String pageSql = """
        SELECT topics.*,
               (
                   SELECT COUNT(*)
                   FROM comments
                   WHERE comments.topic_id = topics.id
               ) AS replies,
               (
                   SELECT COUNT(*)
                   FROM topic_likes
                   WHERE topic_likes.topic_id = topics.id
               ) AS like_count
        FROM topics
        """;


        pageSql += conditionSql;
        pageSql += " ORDER BY id DESC LIMIT ? OFFSET ?";

        // 当前页查询的参数 = 筛选参数 + 分页参数。
        List<Object> pageParams = new ArrayList<>(conditionParams);
        pageParams.add(pageSize);
        pageParams.add(offset);

        List<Topic> topics = jdbcTemplate.query(
                pageSql,
                (rs, row) -> {
                    Topic topic = new Topic();

                    // 数据库一行映射成一个 Topic 对象。
                    topic.setId(rs.getInt("id"));
                    topic.setTitle(rs.getString("title"));
                    topic.setContent(rs.getString("content"));
                    topic.setAuthor(rs.getString("author"));
                    topic.setTag(rs.getString("tag"));
                    // 分页结果也从点赞关系表读取实时数量。
                    topic.setLikes(rs.getInt("like_count"));
                    topic.setReplies(rs.getInt("replies"));
                    topic.setCreatedAt(rs.getObject("created_at", OffsetDateTime.class));

                    return topic;
                },
                pageParams.toArray()
        );

        // 查询符合筛选条件的总数量。
        // 注意：COUNT 查询不能带 LIMIT 和 OFFSET。
        String countSql = "SELECT COUNT(*) FROM topics" + conditionSql;

        Long total = jdbcTemplate.queryForObject(
                countSql,
                Long.class,
                conditionParams.toArray()
        );

        return new TopicPageData(
                topics,
                total == null ? 0 : total
        );
    }




    // ===== 根据 ID 查询单个话题 =====
    // 查不到返回 null（不是抛异常）
    public Topic findById(int id) {
        try {
            String sql = """
                SELECT topics.*,
                       (
                           SELECT COUNT(*)
                           FROM comments
                           WHERE comments.topic_id = topics.id
                       ) AS replies,
                       (
                           SELECT COUNT(*)
                           FROM topic_likes
                           WHERE topic_likes.topic_id = topics.id
                       ) AS like_count
                FROM topics
                WHERE topics.id = ?
                """;

            return jdbcTemplate.queryForObject(sql,
                    (rs, row) -> {
                        Topic t = new Topic();
                        t.setId(rs.getInt("id"));
                        t.setTitle(rs.getString("title"));
                        t.setContent(rs.getString("content"));
                        t.setAuthor(rs.getString("author"));
                        t.setTag(rs.getString("tag"));
                        t.setReplies(rs.getInt("replies"));
                        t.setLikes(rs.getInt("like_count"));
                        t.setCreatedAt(rs.getObject("created_at", OffsetDateTime.class));
                        return t;
                    }, id);
        } catch (Exception e) {
            return null;  // 没找到
        }
    }

    // ===== 新增话题 =====
    // id 是 serial 类型，数据库自动生成，不需要 INSERT
    public int save(Topic topic) {
        return jdbcTemplate.update(
                "INSERT INTO topics (title, content, author, tag, likes) VALUES (?, ?, ?, ?, ?)",
                topic.getTitle(), topic.getContent(), topic.getAuthor(),
                topic.getTag(), topic.getLikes()
        );
    }

    public int delete(int id) {
        return jdbcTemplate.update("DELETE FROM topics WHERE id = ?", id);
    }

    // ===== 查询话题是否存在 =====
    public boolean existsById(int topicId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM topics WHERE id = ?",
                Integer.class,
                topicId
        );

        return count != null && count > 0;
    }

    // ===== 查询某个用户是否已经点赞 =====
    public boolean existsLike(int topicId, int userId) {
        Integer count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM topic_likes
                WHERE topic_id = ?
                  AND user_id = ?
                """,
                Integer.class,
                topicId,
                userId
        );

        return count != null && count > 0;
    }

    // ===== 查询当前用户已经点赞的多个话题 =====
    public Set<Integer> findLikedTopicIds(
            int userId,
            List<Integer> topicIds
    ) {
        if (topicIds.isEmpty()) {
            return Set.of();
        }

        String placeholders = String.join(",", topicIds.stream()
                .map(id -> "?")
                .toList());

        String sql = """
                SELECT topic_id
                FROM topic_likes
                WHERE user_id = ?
                  AND topic_id IN (%s)
                """.formatted(placeholders);

        List<Object> params = new ArrayList<>();
        params.add(userId);
        params.addAll(topicIds);

        return new HashSet<>(jdbcTemplate.queryForList(
                sql,
                Integer.class,
                params.toArray()
        ));
    }

    // ===== 查询话题总点赞数 =====
    public long countLikes(int topicId) {
        Long count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM topic_likes
                WHERE topic_id = ?
                """,
                Long.class,
                topicId
        );

        return count == null ? 0 : count;
    }

    // ===== 新增点赞关系 =====
    public int addLike(int topicId, int userId) {
        return jdbcTemplate.update(
                """
                INSERT INTO topic_likes (topic_id, user_id)
                VALUES (?, ?)
                """,
                topicId,
                userId
        );
    }

    // ===== 删除点赞关系 =====
    public int removeLike(int topicId, int userId) {
        return jdbcTemplate.update(
                """
                DELETE FROM topic_likes
                WHERE topic_id = ?
                  AND user_id = ?
                """,
                topicId,
                userId
        );
    }

}
