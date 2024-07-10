package com.blog.aspect;

import com.blog.annotation.NoResubmit;
import com.blog.response.ResponseBody;
import com.blog.utils.RedisLockUtil;
import jakarta.annotation.Resource;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

/**
 * @author TimmyChung
 * @version 1.0
 *
 * 使用redis lock 避免重複提交
 */
@Aspect
@Component
public class NoReSubmitAspect {
    @Resource
    private RedisLockUtil redisLockUtil;

    @Pointcut("execution(public * com.blog.controller.*.*(..)) && @annotation(com.blog.annotation.NoResubmit)')")
    public void isResubmit() {}

    @Around("isResubmit()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        NoResubmit noResubmit = method.getAnnotation(NoResubmit.class);

        final String key = generateKey(joinPoint);
        final boolean success = redisLockUtil.getLock(key, null, noResubmit.delaySecond(), TimeUnit.SECONDS);
        if(!success){
            return new ResponseBody<>(false, "重複提交，請稍後 "+ noResubmit.delaySecond() + "秒在試", null, HttpStatus.BAD_REQUEST);
        }
        return joinPoint.proceed();
    }

    private String generateKey(ProceedingJoinPoint joinPoint) {
        StringBuilder sb = new StringBuilder();
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        sb.append(joinPoint.getTarget().getClass().getName())
                .append(method.getName());
        for (Object o : joinPoint.getArgs()) {
            sb.append(o.toString());
        }
        return DigestUtils.md5DigestAsHex(sb.toString().getBytes(Charset.defaultCharset()));
    }


}
