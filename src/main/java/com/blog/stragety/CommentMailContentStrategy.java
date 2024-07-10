package com.blog.stragety;

import org.springframework.stereotype.Component;

@Component
public class CommentMailContentStrategy implements MailContentStrategy {
    /**
     * 返回郵件內容
     *
     * @param title  文章標題
     * @param operation 操作
     */
    @Override
    public String generateMailContent(final String title,final String operation) {
        // 如果是新增 評論
        return "你的文章 " + title + "有一則新留言請前去查看";
    }

}
