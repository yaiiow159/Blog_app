package com.blog.handler;

import com.blog.dto.ApiResponse;
import com.blog.exception.ValidateFailedException;
import jakarta.validation.ValidationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;

import javax.naming.AuthenticationNotSupportedException;
import java.util.List;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(Exception.class)
    public ApiResponse<Exception> handleException(Exception e) {
        return new ApiResponse<>(false,e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(ResourceAccessException.class)
    public ApiResponse<ResourceAccessException> handleException(ResourceAccessException e) {
        return new ApiResponse<>(false,e.getMessage(),HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AuthenticationNotSupportedException.class)
    public ApiResponse<AuthenticationNotSupportedException> handleException(AuthenticationNotSupportedException e) {
        return new ApiResponse<>(false,e.getMessage(),HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ApiResponse<AccessDeniedException> handleException(AccessDeniedException e) {
        return new ApiResponse<>(false,e.getMessage(),HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ApiResponse<UsernameNotFoundException> handle(UsernameNotFoundException e) {
        return new ApiResponse<>(false,e.getMessage(),HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ApiResponse<BadCredentialsException> handle(BadCredentialsException e) {
        return new ApiResponse<>(false,e.getMessage(),HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<MethodArgumentNotValidException> handleArgumentNotValid(MethodArgumentNotValidException e) {
        return new ApiResponse<>(false,e.getMessage(),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ApiResponse<HttpRequestMethodNotSupportedException> handleException(HttpRequestMethodNotSupportedException e) {
        return new ApiResponse<>(false,e.getMessage(),HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(ValidateFailedException.class)
    public ApiResponse<ValidationException> handleException(ValidateFailedException e) {
        List<String> errors = e.getErrors().stream().map(ValidateFailedException.DomainErrorStatus::getMessage).toList();
        String message = errors.get(0);
        if (errors.size() > 1) {
            message = String.join(",", errors);
        }
        return new ApiResponse<>(false, message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ValidationException.class)
    public ApiResponse<ValidationException> handleException(ValidationException e) {
        return new ApiResponse<>(false,e.getMessage(),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ApiResponse<DataIntegrityViolationException> handleException(DataIntegrityViolationException e) {
        return new ApiResponse<>(false,e.getMessage(),HttpStatus.BAD_REQUEST);
    }
}
