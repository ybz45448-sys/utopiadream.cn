package com.utopia.day23.service;

import com.utopia.day23.dto.LikeResponse;
import com.utopia.day23.exception.ResourceNotFoundException;
import com.utopia.day23.model.User;
import com.utopia.day23.repository.TopicRepository;
import com.utopia.day23.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class TopicLikeService {

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public LikeResponse toggleLike(
            int topicId,
            String username
    ) {
        // 先确认话题存在。
        // 不存在时不能创建点赞关系。
        if (!topicRepository.existsById(topicId)) {
            throw new ResourceNotFoundException("话题不存在");
        }

        // JWT 中只有 username。
        // 通过 username 查询数据库，获取真正的 user_id。
        User user = userRepository.findByusername(username);

        if (user == null) {
            throw new ResourceNotFoundException("用户不存在");
        }

        int userId = user.getId();

        // 判断当前用户是否已经点赞。
        boolean alreadyLiked =
                topicRepository.existsLike(topicId, userId);

        boolean liked;

        if (alreadyLiked) {
            // 已点赞：再次点击表示取消点赞。
            topicRepository.removeLike(topicId, userId);
            liked = false;
        } else {
            // 未点赞：插入一条点赞关系。
            topicRepository.addLike(topicId, userId);
            liked = true;
        }

        // 重新统计数据库中的真实点赞数。
        long likes = topicRepository.countLikes(topicId);

        return new LikeResponse(liked, likes);
    }

    public LikeResponse getLikeStatus(
            int topicId,
            String username
    ) {
        // 先确认话题存在。
        // 即使只是查询点赞状态，话题不存在时也应该返回404。
        if (!topicRepository.existsById(topicId)) {
            throw new ResourceNotFoundException("话题不存在");
        }

        // JWT 中保存 username，点赞关系表使用 user_id。
        User user = userRepository.findByusername(username);

        if (user == null) {
            throw new ResourceNotFoundException("用户不存在");
        }

        int userId = user.getId();

        // 查询当前用户是否已经点赞。
        boolean liked = topicRepository.existsLike(
                topicId,
                userId
        );

        // 查询话题当前总点赞数。
        long likes = topicRepository.countLikes(topicId);

        // 这里只查询，不插入，也不删除点赞关系。
        return new LikeResponse(liked, likes);
    }

    /**
     * 批量查询当前用户是否点赞了这批话题。
     *
     * 返回的 Map：topicId → 当前用户是否已点赞。
     * 例如 {1: true, 2: false, 3: true}
     *
     * 用途：讨论区列表页一次拿到当前页所有话题的点赞状态，
     * 避免为每个话题单独请求一次。
     */
    public Map<Integer, Boolean> getLikeStatusBatch(
            List<Integer> topicIds,
            String username
    ) {
        // 没有话题时直接返回空 Map。
        if (topicIds == null || topicIds.isEmpty()) {
            return Map.of();
        }

        // 去重，避免 SQL IN 中出现重复 id。
        List<Integer> distinctIds = topicIds.stream()
                .distinct()
                .toList();

        // 查询当前用户。
        User user = userRepository.findByusername(username);

        if (user == null) {
            throw new ResourceNotFoundException("用户不存在");
        }

        int userId = user.getId();

        // 一次 SQL 查出当前用户在这批话题中点赞过的所有 id。
        Set<Integer> likedIds = topicRepository.findLikedTopicIds(
                userId,
                distinctIds
        );

        // 组装结果：每个话题都返回 false/true。
        Map<Integer, Boolean> result = new HashMap<>();

        for (Integer topicId : distinctIds) {
            result.put(topicId, likedIds.contains(topicId));
        }

        return result;
    }

}
