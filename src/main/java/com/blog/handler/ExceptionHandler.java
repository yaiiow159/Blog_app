package com.blog.handler;

import com.blog.dto.ErrorResponseBody;
import com.blog.exception.ValidateFailedException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;

import javax.naming.AuthenticationNotSupportedException;

@RestControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception e) {
        var errorResponse = new ErrorResponseBody("System Error");
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<Object> hadleException(ValidateFailedException e) {
        var errorResponse = new ErrorResponseBody("ValidateFailedException", e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<Object> handleException(ResourceAccessException e) {
        var errorResponse = new ErrorResponseBody("ResourceAccessException", e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(AuthenticationNotSupportedException.class)
    public ResponseEntity<Object> handleException(AuthenticationNotSupportedException e) {
        var errorResponse = new ErrorResponseBody("AuthenticationNotSupportedException", e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleException(AccessDeniedException e) {
        var errorResponse = new ErrorResponseBody("AccessDeniedException", e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NON_AUTHORITATIVE_INFORMATION);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Object> handle(UsernameNotFoundException e) {
        var errorResponseBody = new ErrorResponseBody(e.getMessage());
        return new ResponseEntity<>(errorResponseBody, HttpStatus.NOT_FOUND);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handle(BadCredentialsException e) {
        return new ResponseEntity<>(new ErrorResponseBody(e.getMessage(),"401"), HttpStatus.UNAUTHORIZED);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleArgumentNotValid(MethodArgumentNotValidException e) {
        var errorResponse = new ErrorResponseBody(e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Object> handleException(HttpRequestMethodNotSupportedException e) {
        var errorResponse = new ErrorResponseBody(e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

}
