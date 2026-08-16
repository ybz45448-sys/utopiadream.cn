package com.utopia.day23.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateTopicRequest {

    // 标题不能为空，长度不能超过 200 个字符
    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题长度不能超过200个字符")
    private String title;

    // 正文不能为空，长度不能超过 10000 个字符
    @NotBlank(message = "正文不能为空")
    @Size(max = 10000, message = "正文长度不能超过10000个字符")
    private String content;

    // 分类不能为空，长度不能超过 20 个字符
    @NotBlank(message = "分类不能为空")
    @Size(max = 20, message = "分类长度不能超过20个字符")
    private String tag;

    public CreateTopicRequest() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}

