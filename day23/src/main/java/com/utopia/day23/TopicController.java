// ============================================================
// TopicController.java — 话题控制器
// 提供话题相关的 REST API
// ============================================================

package com.utopia.day23;

import com.utopia.day23.dto.CreateTopicRequest;
import com.utopia.day23.dto.PageResponse;
import com.utopia.day23.exception.ResourceNotFoundException;
import com.utopia.day23.model.Topic;
import com.utopia.day23.service.TopicService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TopicController {
    @Autowired
    private TopicService topicService;

    // GET /api/topics — 获取话题列表
    @GetMapping("/api/topics")
    public PageResponse<Topic> getAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String tag,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        // page 和 pageSize 来自 URL：
        // /api/topics?page=2&pageSize=10
        //
        // keyword 和 tag 继续支持搜索与分类：
        // /api/topics?keyword=java&tag=AI&page=1&pageSize=10
        return topicService.getPage(
                keyword,
                tag,
                page,
                pageSize
        );
    }

    @GetMapping("/api/topics/tags")
    public List<String> getAllTags() {
        // 返回所有不重复的公开话题标签。
        return topicService.getAllTags();
    }


    @GetMapping("/api/topics/{id}")
    public Topic getById(@PathVariable int id) {
        // 先查询话题
        Topic topic = topicService.getById(id);

        // 查询不到时主动抛出明确的业务异常
        // GlobalExceptionHandler 会将它转换成 404 JSON
        if (topic == null) {
            throw new ResourceNotFoundException("话题不存在");
        }

        return topic;
    }


    // POST /api/topics — 创建新话题
    @PostMapping("/api/topics")
    public Topic create(
            @Valid @RequestBody CreateTopicRequest request,
            Authentication authentication
    ) {
        // 身份仍然从 JWT 对应的 Authentication 获取。
        // author 不再由前端提交。
        String username = authentication.getName();

        // DTO 已经完成 title、content、tag 的格式校验。
        Topic topic = new Topic(
                request.getTitle(),
                request.getContent(),
                username,
                request.getTag()
        );

        return topicService.create(topic);
    }



    @DeleteMapping("/api/topics/{id}")
    public String deleteById(
            @PathVariable int id,
            Authentication authentication
    ) {
        // 从 SecurityContext 获取当前登录用户。
        // 这里的用户名来自 JWT，而不是前端请求体。
        String username = authentication.getName();

        // 将话题 ID 和当前用户名一起交给 Service。
        // Service 会查询话题作者并进行所有权校验。
        topicService.delete(id, username);

        return "删除成功";
    }

}
