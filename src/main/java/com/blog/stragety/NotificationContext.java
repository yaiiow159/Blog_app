package com.blog.stragety;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

/**
 * 通知上下文
 */
public class NotificationContext {
    private final MailContentStrategy mailContentStrategy;
    public NotificationContext(MailContentStrategy mailContentStrategy) {
        this.mailContentStrategy = mailContentStrategy;
    }
    public String execute(String title,String operation) {
        return mailContentStrategy.generateMailContent(title,operation);
    }
}
