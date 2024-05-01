package com.blog.dto;

import lombok.*;
import org.springframework.http.HttpStatus;

/**
 * 統一回應物件
 * @param <T>
 * @authr Timmy
 */
@Setter
@Getter
@NoArgsConstructor
public class ApiResponse<T> {
    private T data;
    private String message;
    private boolean result;
    private HttpStatus status;

    public ApiResponse(boolean result, String message, T data, HttpStatus status) {
        this.result = result;
        this.message = message;
        this.data = data;
        this.status = status;
    }

    public ApiResponse(boolean result, String message, HttpStatus status) {
        this.result = result;
        this.message = message;
        this.status = status;
    }

    public ApiResponse(boolean result, String message) {
        this.result = result;
        this.message = message;
    }

    public ApiResponse(String message) {
        this.message = message;
    }
}
