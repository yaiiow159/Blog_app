package com.blog.stragety;


import org.springframework.stereotype.Component;

@Component
public class PostMailContentStrategy implements MailContentStrategy {
    /**
     * 返回郵件內容
     *
     * @param title  文章標題
     */
    @Override
    public String generateMailContent(String title,String operation) {
        if(operation.equals("add")){
            return "你的文章 " + title + " 已經新增完成，請前去查看文章";
        } else if (operation.equals("edit")) {
            return "你的文章 " + title + " 已經變更完成，請前去查看文章";
        }
        return "";
    }
}
