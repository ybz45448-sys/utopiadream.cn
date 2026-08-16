package com.utopia.day23;

import com.utopia.day23.dto.PageResponse;
import com.utopia.day23.model.Topic;
import com.utopia.day23.service.TopicService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 只加载 TopicController 相关的 Spring MVC 配置，不启动服务器
@WebMvcTest(TopicController.class)
class TopicControllerTest {

    // MockMvc：模拟 HTTP 请求的工具，由 Spring 自动注入
    @Autowired
    private MockMvc mockMvc;

    // MockitoBean：用假对象替换容器中的 TopicService
    @MockitoBean
    private TopicService topicService;

    // 测试：GET /api/topics 公开接口正常返回
    @Test
    void getAll_shouldReturn200() throws Exception {
        // 准备：Service 返回一个空分页对象
        when(topicService.getPage(null, null, 1, 10))
                .thenReturn(new PageResponse<>(List.of(), 1, 10, 0, 0));

        // 执行 + 断言：请求 GET /api/topics，期望 200
        mockMvc.perform(get("/api/topics"))
                .andExpect(status().isOk())
                // 响应 JSON 中 content 字段是一个数组
                .andExpect(jsonPath("$.content").isArray());
    }

    // 测试：话题详情返回的 createdAt 是 ISO-8601 字符串（不是数字）
    @Test
    void getById_shouldSerializeCreatedAtAsString() throws Exception {
        // 准备：Service 返回一个带创建时间的话题
        Topic topic = new Topic("标题", "内容", "ceshi", "AI");
        topic.setId(1);
        topic.setCreatedAt(OffsetDateTime.of(2026, 8, 16, 10, 30, 0, 0, ZoneOffset.ofHours(8)));

        when(topicService.getById(1)).thenReturn(topic);

        // 执行 + 断言：createdAt 以字符串形式出现在 JSON 里
        mockMvc.perform(get("/api/topics/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.createdAt").isString());
    }

    // 测试：GET /api/topics/999 话题不存在返回 404
    @Test
    void getById_shouldReturn404_whenNotFound() throws Exception {
        // 准备：Service 返回 null（话题不存在）
        when(topicService.getById(999)).thenReturn(null);

        // 执行 + 断言：期望 404
        mockMvc.perform(get("/api/topics/999"))
                .andExpect(status().isNotFound());
    }

    // 测试：空标题创建话题，应被参数校验拦截返回 400
    @Test
    void createTopic_emptyTitle_shouldReturn400() throws Exception {
        // 执行 + 断言：POST /api/topics 携带空 title，期望 400
        mockMvc.perform(post("/api/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"\",\"content\":\"内容\",\"tag\":\"AI\"}"))
                .andExpect(status().isBadRequest())
                // 校验错误信息包含 title 字段
                .andExpect(jsonPath("$.errors.title").exists());
    }

    // 测试：空正文创建话题，应返回 400
    @Test
    void createTopic_emptyContent_shouldReturn400() throws Exception {
        mockMvc.perform(post("/api/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"标题\",\"content\":\"\",\"tag\":\"AI\"}"))
                .andExpect(status().isBadRequest())
                // 校验错误信息包含 content 字段
                .andExpect(jsonPath("$.errors.content").exists());
    }

    // 测试：空分类创建话题，应返回 400
    @Test
    void createTopic_emptyTag_shouldReturn400() throws Exception {
        mockMvc.perform(post("/api/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"标题\",\"content\":\"内容\",\"tag\":\"\"}"))
                .andExpect(status().isBadRequest())
                // 校验错误信息包含 tag 字段
                .andExpect(jsonPath("$.errors.tag").exists());
    }

}

