package com.blog.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
@Slf4j
public class ValidateCommentAspect {


    @Before("execution(* com.blog.service.CommentService.createComment(..))")
    public void validateCommentIfHasErrorContent(){
        log.info("validateCommentIfHasErrorContent starting...");

    }
}
