package com.blog.aspect;

import com.blog.dto.LogInfoBody;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

/**
 * @author TimmyChung
 *
 * 記錄請求
 */
@Aspect
@Component
@Slf4j
public class LogAspect {

    @Pointcut("execution(public * com.blog.controller.*.*(..))")
    public void logPointcut() {
    }

    @Around("logPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        HttpServletRequest request = getCurrentHttpRequest();
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();

        logRequestInfo(joinPoint, request, startTime, endTime);
        return result;
    }

    private HttpServletRequest getCurrentHttpRequest() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attributes != null;
        return attributes.getRequest();
    }

    private void logRequestInfo(ProceedingJoinPoint joinPoint, HttpServletRequest request, long startTime, long endTime) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        LogInfoBody logInfoBody = new LogInfoBody();
        logInfoBody.setIp(request.getHeader("X-Forwarded-For") != null ? request.getHeader("X-Forwarded-For") : request.getRemoteAddr());
        logInfoBody.setUserAgent(request.getHeader("User-Agent"));
        logInfoBody.setStartTime(transformTime(startTime));
        logInfoBody.setUrl(request.getRequestURI());
        logInfoBody.setBasePath(request.getRequestURL().toString());
        logInfoBody.setParameter(getParameter(method.getParameters()));
        logInfoBody.setSpendTime((endTime - startTime) + "ms");

        log.info("Request Info: {}", logInfoBody);
    }

    private String[] getParameter(Parameter[] parameters) {
        return Arrays.stream(parameters)
                .map(Parameter::getName)
                .toArray(String[]::new);
    }

    private String transformTime(long time) {
        return Instant.ofEpochMilli(time)
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
    }
}


