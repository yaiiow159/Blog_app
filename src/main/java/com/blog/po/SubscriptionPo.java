package com.blog.po;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Table(name = "subscription", schema = "blog_app", catalog = "blog_app")
@Entity
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "user_id",referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_subscription_user"))
    @ToString.Exclude
    private UserPo user;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "post_id",referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_subscription_post"))
    @ToString.Exclude
    private PostPo post;

    @Column(name = "authorName", length = 100)
    private String authorName;

    @Column(name = "email", length = 100)
    @Email(message = "Email格式錯誤",regexp = "^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\.[a-zA-Z0-9]{2,6}$")
    private String email;
}
