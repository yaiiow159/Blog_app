package com.blog.vo;

import com.blog.po.PostPo;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;


@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class PostVo implements Serializable {
    private Long id;
    private String title;
    private String authorName;
    private String authorEmail;
    private String content;
    private LocalDateTime createTime;
    private String createTimeStr;
    public PostVo(Long id, String title, String authorName, String authorEmail, String content,LocalDateTime createTime) {
        this.id = id;
        this.title = title;
        this.authorName = authorName;
        this.authorEmail = authorEmail;
        this.content = content;
        this.createTime = createTime;
    }
}
