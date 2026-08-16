package com.utopia.day23.repository;

import com.utopia.day23.model.Topic;

import java.util.List;

public class TopicPageData {

    // 当前页的话题数据
    private List<Topic> topics;

    // 符合当前筛选条件的总数量
    private long total;

    public TopicPageData(
            List<Topic> topics,
            long total
    ) {
        this.topics = topics;
        this.total = total;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public long getTotal() {
        return total;
    }
}

