package com.blog.handler;

import com.blog.exception.ResourceNotFoundException;
import com.blog.response.ResponseBody;
import com.blog.exception.ValidateFailedException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
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
    public ResponseBody<Exception> handleException(Exception e) {
        return new ResponseBody<>(false,e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(ResourceAccessException.class)
    public ResponseBody<ResourceAccessException> handleException(ResourceAccessException e) {
        return new ResponseBody<>(false,e.getMessage(),HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AuthenticationNotSupportedException.class)
    public ResponseBody<AuthenticationNotSupportedException> handleException(AuthenticationNotSupportedException e) {
        return new ResponseBody<>(false,e.getMessage(),HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseBody<AccessDeniedException> handleException(AccessDeniedException e) {
        return new ResponseBody<>(false,e.getMessage(),HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseBody<UsernameNotFoundException> handle(UsernameNotFoundException e) {
        return new ResponseBody<>(false,e.getMessage(),HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseBody<BadCredentialsException> handle(BadCredentialsException e) {
        return new ResponseBody<>(false,e.getMessage(),HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseBody<MethodArgumentNotValidException> handleArgumentNotValid(MethodArgumentNotValidException e) {
        return new ResponseBody<>(false,e.getMessage(),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseBody<HttpRequestMethodNotSupportedException> handleException(HttpRequestMethodNotSupportedException e) {
        return new ResponseBody<>(false,e.getMessage(),HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(ValidateFailedException.class)
    public ResponseBody<ValidationException> handleException(ValidateFailedException e) {
        List<String> errors = e.getErrors().stream().map(ValidateFailedException.DomainErrorStatus::getMessage).toList();
        if(errors.isEmpty()){
            return new ResponseBody<>(false,e.getMessage(),HttpStatus.BAD_REQUEST);
        }
        String message = errors.get(0);
        if (errors.size() > 1) {
            message = String.join(",", errors);
        }
        return new ResponseBody<>(false, message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseBody<ResourceNotFoundException> handleException(ResourceNotFoundException e) {
        return new ResponseBody<>(false,e.getMessage(),HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseBody<EntityNotFoundException> handleException(EntityNotFoundException e) {
        return new ResponseBody<>(false,e.getMessage(),HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseBody<ValidationException> handleException(ValidationException e) {
        return new ResponseBody<>(false,e.getMessage(),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseBody<IllegalArgumentException> handleException(IllegalArgumentException e) {
        return new ResponseBody<>(false,e.getMessage(),HttpStatus.BAD_REQUEST);
    }
}
