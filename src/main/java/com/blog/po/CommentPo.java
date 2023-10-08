package com.blog.po;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.io.Serializable;


@Getter
@Setter
@Entity
@Table(name = "comments")
public class CommentPo extends BasicPo implements Serializable {

    @Column(name = "body")
    private String body;

    @Column(name = "email")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$",message = "電子郵件格式錯誤")
    private String email;

    @Column(name = "name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private PostPo post;

}