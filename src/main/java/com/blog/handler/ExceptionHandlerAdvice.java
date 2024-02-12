package com.blog.handler;

import com.blog.dto.ErrorResponseBody;
import com.blog.exception.ValidateFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;

import javax.naming.AuthenticationNotSupportedException;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception e) {
        var errorResponse = new ErrorResponseBody("Exception", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<Object> handleException(ResourceAccessException e) {
        var errorResponse = new ErrorResponseBody("ResourceAccessException", e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY.value());
        return new ResponseEntity<>(errorResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(AuthenticationNotSupportedException.class)
    public ResponseEntity<Object> handleException(AuthenticationNotSupportedException e) {
        var errorResponse = new ErrorResponseBody("AuthenticationNotSupportedException", e.getMessage(), HttpStatus.METHOD_NOT_ALLOWED.value());
        return new ResponseEntity<>(errorResponse, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleException(AccessDeniedException e) {
        var errorResponse = new ErrorResponseBody("AccessDeniedException", e.getMessage(), HttpStatus.NON_AUTHORITATIVE_INFORMATION.value());
        return new ResponseEntity<>(errorResponse, HttpStatus.NON_AUTHORITATIVE_INFORMATION);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Object> handle(UsernameNotFoundException e) {
        var errorResponseBody = new ErrorResponseBody("UsernameNotFoundException",e.getMessage(),HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(errorResponseBody, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handle(BadCredentialsException e) {
        return new ResponseEntity<>(new ErrorResponseBody("BadCredentialsException",e.getMessage(),HttpStatus.UNAUTHORIZED.value()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleArgumentNotValid(MethodArgumentNotValidException e) {
        var errorResponse = new ErrorResponseBody("MethodArgumentNotValidException",e.getMessage(),HttpStatus.METHOD_NOT_ALLOWED.value());
        return new ResponseEntity<>(errorResponse, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Object> handleException(HttpRequestMethodNotSupportedException e) {
        var errorResponse = new ErrorResponseBody("HttpRequestMethodNotSupportedException",e.getMessage(),HttpStatus.UNSUPPORTED_MEDIA_TYPE.value());
        return new ResponseEntity<>(errorResponse, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(ValidateFailedException.class)
    public ResponseEntity<Object> handleException(ValidateFailedException e) {
        var errorResponse = new ErrorResponseBody(e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }
}
