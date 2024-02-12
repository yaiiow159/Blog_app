package com.blog.aspect;

import com.blog.annotation.NotifyByEmail;
import com.blog.dao.PostPoRepository;
import com.blog.dto.EmailNotification;
import com.blog.dto.PostDto;
import com.blog.dto.SubscriptionDto;
import com.blog.exception.ResourceNotFoundException;
import com.blog.mapper.PostPoMapper;
import com.blog.po.PostPo;
import com.blog.producer.EmailNotificationProducer;
import com.blog.service.PostService;
import com.blog.service.SubscriptionService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Aspect
@Component
@Slf4j
public class EmailNotificationAspect {
    @Resource
    private EmailNotificationProducer emailNotificationProducer;
    @Resource
    private SubscriptionService subscriptionService;

    @Resource
    private PostPoRepository postPoRepository;

    @Pointcut("@annotation(notifyByEmail)")
    public void notifyByEmailPointcut(NotifyByEmail notifyByEmail) {}
    @AfterReturning(value = "notifyByEmailPointcut(notifyByEmail)", returning = "result", argNames = "joinPoint,notifyByEmail,result")
    public void afterReturning(JoinPoint joinPoint, NotifyByEmail notifyByEmail, Object result) throws ResourceNotFoundException {
        final String action = notifyByEmail.value();
        Object[] args = joinPoint.getArgs();
        log.info("notifyEmail afterReturning starting...");
        if(action.equals("post")) {
            for (Object arg : args) {
                if (arg instanceof PostDto postDto) {
                    String authorName = postDto.getAuthorName();
                    String authorEmail = postDto.getAuthorEmail();
                    // 如果該篇作者有被目前使用者收藏 則發送通知給使用者
                    List<SubscriptionDto> subscriptionDtoList = subscriptionService.findByAuthorNameOrAuthorEmail(authorName, authorEmail);
                    if (CollectionUtils.isEmpty(subscriptionDtoList)) {
                        return;
                    }
                    emailNotificationProducer.sendMailNotification(getEmailNotification(postDto, joinPoint.getSignature().getName()));
                }
            }
        }
        if(action.equals("comment")) {
            for (Object arg : args) {
                // 取得方法參數
                if(arg instanceof Long postId) {
                    PostPo postPo = postPoRepository.findById(postId).orElseThrow(ResourceNotFoundException::new);
                    PostDto postDto = PostPoMapper.INSTANCE.toDto(postPo);
                    emailNotificationProducer.sendMailNotification(getEmailNotification(postDto, joinPoint.getSignature().getName()));
                }
            }
        }
        log.info("notifyEmail afterReturning ending...");
    }

    private static EmailNotification getEmailNotification(PostDto postDto, String methodName) {
        EmailNotification emailNotification = new EmailNotification();
        emailNotification.setConsumer(postDto.getAuthorName());
        emailNotification.setEmailAddress(postDto.getAuthorEmail());
        if (methodName.contains("create")) {
            if(methodName.contains("comment")) {
                emailNotification.setAction("的文章有了新評論");
                emailNotification.setSubObject(postDto.getTitle() + "的有新動態");
            } else {
                emailNotification.setAction("發布了文章");
                emailNotification.setSubObject(postDto.getTitle());
            }
        } else if (methodName.contains("update")) {
            emailNotification.setAction("更新了文章");
            emailNotification.setSubObject(postDto.getTitle());
        }
        if(methodName.contains("Post")){
            emailNotification.setContent(postDto.getContent());
        } else if (methodName.contains("Comment")) {
            emailNotification.setContent("有人留了新評論，快去看看");
        }
        return emailNotification;
    }
}

