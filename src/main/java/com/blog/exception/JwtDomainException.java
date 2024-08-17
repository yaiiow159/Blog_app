package com.blog.exception;

import org.springframework.http.HttpStatus;

public class JwtDomainException extends RuntimeException {

    public JwtDomainException(HttpStatus httpStatus, String s) {
        super(s);
    }
}
