package com.utopia.day23;

import com.utopia.day23.dto.ApiErrorResponse;
import com.utopia.day23.exception.ForbiddenOperationException;
import com.utopia.day23.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理 DTO 参数校验失败。
     *
     * 例如：
     * username 为空
     * password 长度不符合要求
     *
     * Spring 会把校验错误放在 BindingResult 中。
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(
            MethodArgumentNotValidException exception
    ) {
        Map<String, String> errors = new LinkedHashMap<>();

        // 遍历每一个字段的校验错误
        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error -> {
                    String field = error.getField();
                    String message = error.getDefaultMessage();

                    // 同一个字段可能有多个错误，这里保留第一个
                    errors.putIfAbsent(field, message);
                });

        ApiErrorResponse response = new ApiErrorResponse(
                false,
                "参数校验失败",
                errors,
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /**
     * 处理资源不存在异常。
     *
     * 例如：
     * - 话题 ID 不存在
     * - 评论 ID 不存在
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFoundException(
            ResourceNotFoundException exception
    ) {
        ApiErrorResponse response = new ApiErrorResponse(
                false,
                exception.getMessage(),
                Map.of(),
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    /**
     * 处理已登录但无权操作的异常。
     *
     * 例如：
     * - 用户 A 删除用户 B 的话题
     * - 用户 A 删除用户 B 的评论
     */
    @ExceptionHandler(ForbiddenOperationException.class)
    public ResponseEntity<ApiErrorResponse> handleForbiddenException(
            ForbiddenOperationException exception
    ) {
        ApiErrorResponse response = new ApiErrorResponse(
                false,
                exception.getMessage(),
                Map.of(),
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(response);
    }

}

