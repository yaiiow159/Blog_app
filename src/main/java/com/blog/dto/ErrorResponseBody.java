package com.blog.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpEntity;

@Setter
@Getter
@NoArgsConstructor
public class ErrorResponseBody {
    private String message;
    private String errorType;
    private int status;

    public ErrorResponseBody(String message){
        this.message = message;
    }

    public ErrorResponseBody(String message, String errorType){
        this.message = message;
        this.errorType = errorType;
    }

    public ErrorResponseBody(String errorType, String message, int status){
        this.message = message;
        this.errorType = errorType;
        this.status = status;
    }

}
