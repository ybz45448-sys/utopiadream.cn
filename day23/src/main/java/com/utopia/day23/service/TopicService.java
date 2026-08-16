// ============================================================
// service/TopicService.java — 话题业务逻辑层
// 调用 Repository 操作数据库，提供给 Controller 调用
// ============================================================

package com.utopia.day23.service;

import com.utopia.day23.dto.PageResponse;
import com.utopia.day23.exception.ForbiddenOperationException;
import com.utopia.day23.exception.ResourceNotFoundException;
import com.utopia.day23.model.Topic;
import com.utopia.day23.repository.CommentRepository;
import com.utopia.day23.repository.TopicPageData;
import com.utopia.day23.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TopicService {
    @Autowired
    private TopicRepository topicRepository;

    // 获取全部话题
    public List<Topic> getAll(String keyword, String tag) {
        // 清理关键词：
        // null 或空白字符串都表示没有搜索条件。
        String cleanKeyword = keyword == null
                ? ""
                : keyword.trim();

        // 清理分类：
        // null 或空白字符串都表示没有分类筛选。
        String cleanTag = tag == null
                ? ""
                : tag.trim();

        // 没有关键词，也没有分类时，查询全部话题。
        if (cleanKeyword.isBlank() && cleanTag.isBlank()) {
            return topicRepository.findAll();
        }

        // 只要存在关键词或分类，就交给 Repository 组合查询条件。
        return topicRepository.search(cleanKeyword, cleanTag);
    }

    public List<String> getAllTags() {
        // 标签查询没有复杂业务规则，
        // 由 Service 统一转发给 Repository。
        return topicRepository.findAllTags();
    }


    public PageResponse<Topic> getPage(
            String keyword,
            String tag,
            int page,
            int pageSize
    ) {
        // 清理关键词和分类。
        String cleanKeyword = keyword == null
                ? ""
                : keyword.trim();

        String cleanTag = tag == null
                ? ""
                : tag.trim();

        // 防止页码小于1。
        int safePage = Math.max(page, 1);

        // 防止每页数量非法或过大。
        // 暂时最多允许一次查询100条。
        int safePageSize = Math.min(
                Math.max(pageSize, 1),
                100
        );

        // Repository 返回当前页数据和总数量。
        TopicPageData pageData = topicRepository.searchPage(
                cleanKeyword,
                cleanTag,
                safePage,
                safePageSize
        );

        // 计算总页数：
        // total=25、pageSize=10 时，totalPages=3。
        int totalPages = (int) Math.ceil(
                (double) pageData.getTotal() / safePageSize
        );

        return new PageResponse<>(
                pageData.getTopics(),
                safePage,
                safePageSize,
                pageData.getTotal(),
                totalPages
        );
    }


    // 根据 ID 获取单个话题
    public Topic getById(int id) { return topicRepository.findById(id); }

    // 创建话题（保存到数据库，返回创建好的话题）
    public Topic create(Topic topic) {
        topicRepository.save(topic);
        return topic;
    }

    @Autowired
    CommentRepository commentRepository;
    @Transactional //要么两个数据全删除，要么全回滚
    public void delete(int id, String username) {
        // 先查询要删除的话题
        Topic topic = topicRepository.findById(id);

        // 话题不存在时抛出异常
        if (topic == null) {
            throw new ResourceNotFoundException("话题不存在");
        }

        // 当前登录用户不是话题作者，禁止删除
        if (!username.equals(topic.getAuthor())) {
            throw new ForbiddenOperationException("无权删除此话题");
        }

        // 只有作者本人通过校验后，才删除关联评论和话题
        commentRepository.deleteByTopicId(id);
        topicRepository.delete(id);
    }


}
