package com.blog.aspect;

import com.blog.annotation.Notification;
import com.blog.dao.PostPoRepository;
import com.blog.dto.CommentDto;
import com.blog.dto.EmailNotification;
import com.blog.dto.PostDto;
import com.blog.po.CommentPo;
import com.blog.po.PostPo;
import com.blog.service.MailService;
import com.blog.service.SubscriptionService;
import com.blog.stragety.CommentMailContentStrategy;
import com.blog.stragety.NotificationContext;
import com.blog.stragety.PostMailContentStrategy;
import com.blog.utils.SpringSecurityUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * @author TimmyChung
 * <p>
 * 處理Email通知 的切面
 */
@Aspect
@Component
@RequiredArgsConstructor
public class EmailNotificationAspect {

    private final SubscriptionService subscriptionService;
    private final PostPoRepository postPoRepository;
    private final MailService mailService;
    private static final Logger logger = LoggerFactory.getLogger(EmailNotificationAspect.class);

    @Pointcut("@annotation(notification)")
    public void notifyByEmailPointcut(Notification notification) {
    }

    @After(value = "notifyByEmailPointcut(sendMail)", argNames = "joinPoint,sendMail")
    public void after(JoinPoint joinPoint, Notification sendMail) {
        Class<?> operatedClass = sendMail.operatedClass();
        String operation = sendMail.operation();

        PostDto postDto = null;
        CommentDto commentDto = null;
        // 從 joinPoint 取得 參數 如 PostPo CommentPo
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof PostDto) {
                postDto = (PostDto) arg;
            } else if (arg instanceof CommentDto) {
                commentDto = (CommentDto) arg;
            }
        }
        logger.info("生產者通知郵件寄送訊息...");
        if (sendMail.operatedClass() == null || sendMail.operation().isEmpty()) {
            logger.error("註解參數配置錯誤...");
            throw new IllegalArgumentException("請確認註解參數是否正確");
        }
        if (postDto == null && commentDto == null) {
            logger.error("找不到欲處理的類型參數");
            throw new IllegalArgumentException("切面執行參數未查找到正確參數");
        }
        // step1. 確認 通類類型
        if (operatedClass.equals(PostPo.class)) {
            // 執行 文章新增通知
            assert postDto != null;
            final String content = getMailContentStrategy(PostPo.class, operation);
            // 生產者 負責傳遞 郵件通知 給消費者 進行郵件傳遞 並在db紀錄
            EmailNotification emailNotification = EmailNotification.builder()
                    .sendTo(postDto.getAuthorEmail())
                    .sendBy("system").message(content)
                    .operation(operation)
                    .emailAddress(postDto.getAuthorEmail())
                    .subject("文章變動通知")
                    .build();
            CompletableFuture<Void> future = mailService.sendMailAsync(emailNotification);
            future.thenRun(() -> logger.debug("生產者進行文章新增通知 完成"));
        } else if (operatedClass.equals(CommentPo.class)) {
            // 執行 留言新增通知
            assert commentDto != null;
            CommentDto finalCommentDto = commentDto;
            PostPo postPo = postPoRepository.findById(commentDto.getPostId()).orElseThrow(() -> new EntityNotFoundException("找不到 id為" + finalCommentDto.getPostId() + "的文章"));
            final String content = getMailContentStrategy(CommentPo.class, operation);
            // 生產者 負責傳遞 郵件通知 給消費者 進行郵件傳遞 並在db紀錄
            EmailNotification emailNotification = EmailNotification.builder()
                    .sendTo(postPo.getAuthorEmail())
                    .sendBy("system").message(content)
                    .operation(operation)
                    .emailAddress(postPo.getAuthorEmail())
                    .subject("文章變動通知")
                    .build();
            CompletableFuture<Void> future = mailService.sendMailAsync(emailNotification);
            future.thenRun(() -> logger.debug("生產者進行收藏文章通知 完成"));
        }
        // 收藏文章者 可收到 收藏文章變動通知 (如果該作者有更動文章 則會收到郵件通知)
        if (operatedClass.equals(PostPo.class) && subscriptionService.checkSubscription(SpringSecurityUtil.getCurrentUser(), postDto.getId())) {
            final String content = "收藏文章" + postDto.getTitle() + "有發生變動,請查看更動內容";
            // 生產者 負責傳遞 郵件通知 給消費者 進行郵件傳遞 並在db紀錄
            EmailNotification emailNotification = EmailNotification.builder()
                    .sendTo(postDto.getAuthorEmail())
                    .sendBy("system").message(content)
                    .operation(operation)
                    .emailAddress(postDto.getAuthorEmail())
                    .subject("文章變動通知")
                    .build();
            CompletableFuture<Void> future = mailService.sendMailAsync(emailNotification);
            future.thenRun(() -> logger.debug("生產者進行收藏文章通知 完成"));
        }
        logger.info("生產者通知郵件寄送結束...");
    }

    // 取得 郵件內容
    private String getMailContentStrategy(Class<?> operatedClass, String operation) {
        if (operatedClass.equals(PostPo.class)) {
            NotificationContext notificationContext = new NotificationContext(new PostMailContentStrategy());
            return notificationContext.execute(operatedClass.getName(), operation);
        } else if (operatedClass.equals(CommentPo.class)) {
            NotificationContext notificationContext = new NotificationContext(new CommentMailContentStrategy());
            return notificationContext.execute(operatedClass.getName(), operation);
        }
        throw new IllegalArgumentException("不支援的郵件通知類別: " + operatedClass.getName());
    }
}

