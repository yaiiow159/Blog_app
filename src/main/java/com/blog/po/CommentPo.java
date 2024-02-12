package com.blog.po;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


@Getter
@Setter
@Entity
@Table(name = "comments")
public class CommentPo extends BasicPo implements Serializable {
    public CommentPo() {
        this.likes = 0L;
        this.dislikes = 0L;
        this.isReport = false;
    }

    @Column(name = "body")
    private String body;

    @Column(name = "email")
    @Email(message = "Email 格式錯誤")
    private String email;

    @Column(name = "name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_comment_post"),
            referencedColumnName = "id")
    private PostPo post;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_comment_user"),
            referencedColumnName = "id")
    private UserPo user;

    @Column(name = "is_report", columnDefinition = "boolean default false", nullable = false)
    private Boolean isReport;

    @Column(name = "likes", columnDefinition = "bigint default 0", nullable = false)
    private Long likes;

    @Column(name = "dislikes", columnDefinition = "bigint default 0", nullable = false)
    private Long dislikes;

}