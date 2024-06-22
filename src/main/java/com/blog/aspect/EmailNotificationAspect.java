package com.blog.aspect;

import com.blog.annotation.SendMail;
import com.blog.dao.PostPoRepository;
import com.blog.dto.CommentDto;
import com.blog.dto.EmailNotification;
import com.blog.dto.PostDto;
import com.blog.dto.SubscriptionDto;
import com.blog.exception.ResourceNotFoundException;
import com.blog.po.PostPo;
import com.blog.producer.NotificationProducer;
import com.blog.service.SubscriptionService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Aspect
@Component
@Slf4j
public class EmailNotificationAspect {
    @Resource
    private NotificationProducer emailNotificationProducer;
    @Resource
    private SubscriptionService subscriptionService;
    @Resource
    private PostPoRepository postPoRepository;

    @Pointcut("@annotation(sendMail)")
    public void notifyByEmailPointcut(SendMail sendMail) {}
    @After(value = "notifyByEmailPointcut(sendMail)", argNames = "joinPoint,sendMail")
    public void after(JoinPoint joinPoint, SendMail sendMail) throws ResourceNotFoundException {
        final String type = sendMail.type();
        final String operation = sendMail.operation();
        log.info("notifyEmail afterReturning starting...");

        Object[] args = joinPoint.getArgs();
        if(ObjectUtils.isEmpty(args)) {
            return;
        }
        for (Object arg : args) {
            if (arg instanceof PostDto postDto) {
                EmailNotification emailNotification = sendToEmailWhenArticleChange(postDto, operation, type);
                if(!ObjectUtils.isEmpty(emailNotification)) {
                    emailNotificationProducer.sendMailNotification(emailNotification);
                }
            }
            if(arg instanceof CommentDto commentDto) {
                EmailNotification emailNotification = sendToEmailWhenCommentChange(commentDto, operation, type);
                if(!ObjectUtils.isEmpty(emailNotification)) {
                    emailNotificationProducer.sendMailNotification(emailNotification);
                }
            }
        }
        log.info("notifyEmail afterReturning ending...");
    }
    private EmailNotification sendToEmailWhenArticleChange(PostDto postDto, String operation, String type) {
        EmailNotification emailNotification = new EmailNotification();
        emailNotification.setSendTo(postDto.getAuthorEmail());
        emailNotification.setSubject("文章通知");
        emailNotification.setSendBy(postDto.getAuthorName());
        emailNotification.setOperation(operation);
        // 當前使用者有收藏 文章時，發送通知
        List<SubscriptionDto> subscriptionDtos = subscriptionService.findByAuthorNameOrAuthorEmail(postDto.getAuthorName(), postDto.getAuthorEmail());
        if(!CollectionUtils.isEmpty(subscriptionDtos)) {
            if("add".equals(operation)) {
                // 新增文章通知
                emailNotification.setMessage("您的訂閱作者新增了一篇文章，請前往查看");
            }
            if("edit".equals(operation)) {
                emailNotification.setMessage("您的訂閱文章已經更新，請前往查看");
            }
        }
        return emailNotification;
    }

    private EmailNotification sendToEmailWhenCommentChange(CommentDto commentDto, String operation, String type) throws ResourceNotFoundException {
        PostPo postPo = postPoRepository.findById(commentDto.getPostId()).orElseThrow(() -> new ResourceNotFoundException("文章不存在"));
        EmailNotification emailNotification = new EmailNotification();
        if("comment".equals(type)) {
            // 新增留言通知
            emailNotification.setSendTo(postPo.getAuthorEmail());
            emailNotification.setSubject("留言通知");
            emailNotification.setSendBy(commentDto.getName());
            emailNotification.setOperation(operation);
            emailNotification.setEmailAddress(postPo.getAuthorEmail());
            if ("add".equals(operation)) {
                // 新增留言通知
                emailNotification.setMessage("您的文章 " + postPo.getTitle() + "新增了一則留言，請前往查看");
            }
        }
        return emailNotification;
    }
}

