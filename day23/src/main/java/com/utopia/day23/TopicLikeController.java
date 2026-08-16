package com.utopia.day23;

import com.utopia.day23.dto.LikeResponse;
import com.utopia.day23.service.TopicLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
public class TopicLikeController {

    @Autowired
    private TopicLikeService topicLikeService;

    /**
     * 点赞或取消点赞。
     *
     * 同一个接口根据当前状态自动切换：
     * 未点赞 → 点赞
     * 已点赞 → 取消点赞
     */
    @PostMapping("/api/topics/{topicId}/like")
    public LikeResponse toggleLike(
            @PathVariable int topicId,
            Authentication authentication
    ) {
        // 当前用户名来自 JWT 和 SecurityContext。
        // 不从请求体接收 userId 或 username。
        String username = authentication.getName();

        // Service 负责：
        // 1. 检查话题是否存在
        // 2. 查询用户 ID
        // 3. 判断点赞状态
        // 4. 插入或删除点赞关系
        // 5. 返回最新点赞数量
        return topicLikeService.toggleLike(
                topicId,
                username
        );
    }

    @GetMapping("/api/topics/{topicId}/like")
    public LikeResponse getLikeStatus(
            @PathVariable int topicId,
            Authentication authentication
    ) {
        // 当前用户身份来自 JWT。
        String username = authentication.getName();

        // 只查询当前用户的点赞状态和话题总点赞数。
        return topicLikeService.getLikeStatus(
                topicId,
                username
        );
    }

    /**
     * 批量查询当前用户对多个话题的点赞状态。
     *
     * 请求：GET /api/topics/like-status?ids=1,2,3
     * 返回：{ "1": true, "2": false, "3": true }
     */
    @GetMapping("/api/topics/like-status")
    public Map<Integer, Boolean> getLikeStatusBatch(
            @RequestParam("ids") String ids,
            Authentication authentication
    ) {
        // 把逗号分隔的字符串转换成 List<Integer>。
        List<Integer> topicIds = Arrays.stream(ids.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .toList();

        // 当前用户名来自 JWT。
        String username = authentication.getName();

        return topicLikeService.getLikeStatusBatch(
                topicIds,
                username
        );
    }

}

