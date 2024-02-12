package com.blog.aspect;

import com.blog.dto.LogInfoBody;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * 紀錄Request日誌訊息
 */
@Aspect
@Component
@Slf4j
public class LogAspect {

    @Pointcut("within(com.blog.controller..*)")
    public void logPointcut() {}

    @Around("logPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attributes != null;
        HttpServletRequest request = attributes.getRequest();
        long endTime = System.currentTimeMillis();
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        // 紀錄訊息
        LogInfoBody logInfoBody = new LogInfoBody();
        logInfoBody.setIp(request.getRemoteAddr());
        if(request.getHeader("X-Forwarded-For") != null) {
            logInfoBody.setIp(request.getHeader("X-Forwarded-For"));
        }
        logInfoBody.setUserAgent(request.getHeader("User-Agent"));
        logInfoBody.setStartTime(transformTime(startTime));
        logInfoBody.setUrl(request.getRequestURI());
        logInfoBody.setBasePath(UrlUtils.buildRequestUrl(request));
        logInfoBody.setParameter(getParameter(method.getParameters()));
        logInfoBody.setSpendTime(endTime - startTime + "ms");
        log.info("request: {}", logInfoBody);
        return joinPoint.proceed();
    }
    private String[] getParameter(Parameter[] parameters) {
        String[] result = new String[parameters.length];
        int index = 0;
        for (Parameter parameter : parameters) {
            if (parameter.isNamePresent()) {
                result[index++] = parameter.getName();
            }
        }
        return result;
    }

    /**
     * 將系統時間轉換成yyyy-MM-dd HH:mm:ss
     *
     * @param time 系統時間
     * @return 返回字串時間格式
     */
    private String transformTime(long time) {
        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time);
    }
}


