package com.blog.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ErrorResponseBody {
    private String message;
    private String code;
    private int status;

    public ErrorResponseBody(String message){
        this.message = message;
    }

    public ErrorResponseBody(String message, String code){
        this.message = message;
        this.code = code;
    }

    public ErrorResponseBody(String message, String code, int status){
        this.message = message;
        this.code = code;
        this.status = 1;
    }

}
