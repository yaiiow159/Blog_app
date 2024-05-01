package com.blog.exception;

import org.springframework.http.HttpStatus;

public class JwtDomainException extends RuntimeException {

    public JwtDomainException(HttpStatus status, String code) {
    }
    public JwtDomainException(String code) {
        super(code);
    }
}
