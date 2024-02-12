package com.blog.vo;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class CommentVo implements Serializable {
    private Long id;
    private String body;
    private String name;
    private String email;
    private LocalDateTime createTime;

    public CommentVo(Long id, String body, String name, String email, LocalDateTime createTime) {
        this.id = id;
        this.body = body;
        this.name = name;
        this.email = email;
        this.createTime = createTime;
    }
}
