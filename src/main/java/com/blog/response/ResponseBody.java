package com.blog.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 * 統一回應物件
 * @param <T> 響應物件
 */
@Setter
@Getter
@NoArgsConstructor
public class ResponseBody<T> {
    private T data;
    private String message;
    private boolean result;
    private HttpStatus status;

    private ResponseBody(Builder<T> builder) {
        this.data = builder.data;
        this.message = builder.message;
        this.result = builder.result;
        this.status = builder.status;
    }

    public static class Builder<T> {

        private T data;
        private String message;
        private boolean result;
        private HttpStatus status;

        public Builder<T> data(T data) {
            this.data = data;
            return this;
        }

        public Builder<T> message(String message) {
            this.message = message;
            return this;
        }

        public Builder<T> result(boolean result) {
            this.result = result;
            return this;
        }

        public Builder<T> status(HttpStatus status) {
            this.status = status;
            return this;
        }
        public ResponseBody<T> build() {
            return new ResponseBody<>(this);
        }
    }



    public ResponseBody(boolean result, String message, T data, HttpStatus status) {
        this.result = result;
        this.message = message;
        this.data = data;
        this.status = status;
    }

    public ResponseBody(boolean result, String message, HttpStatus status) {
        this.result = result;
        this.message = message;
        this.status = status;
    }

    public ResponseBody(boolean result, String message) {
        this.result = result;
        this.message = message;
    }

    public ResponseBody(String message) {
        this.message = message;
    }
}
