package com.utopia.day23.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class ApiErrorResponse {

    // 方便前端判断这次请求是否成功
    private boolean success;

    // 给用户或开发者看的错误概括
    private String message;

    // 具体字段的错误信息，例如 username、password
    private Map<String, String> errors;

    // 错误发生时间，方便日志排查
    private LocalDateTime timestamp;

    public ApiErrorResponse() {
    }

    public ApiErrorResponse(
            boolean success,
            String message,
            Map<String, String> errors,
            LocalDateTime timestamp
    ) {
        this.success = success;
        this.message = message;
        this.errors = errors;
        this.timestamp = timestamp;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
