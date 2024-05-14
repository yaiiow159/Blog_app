package com.blog.po;


import com.blog.enumClass.PostStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

import java.io.Serial;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "posts", uniqueConstraints = {@UniqueConstraint(columnNames = {"title"})}, indexes = {
    @Index(name = "idx_posts", columnList = "title,author_name"),
    @Index(name = "idx_posts_author", columnList = "author_name")
})
@Getter
@Setter
@AllArgsConstructor
@Indexed(index = "idx_posts")
public class PostPo extends BasicPo implements java.io.Serializable {
    public PostPo() {
        this.likes = 0L;
        this.dislikes = 0L;
        this.views = 0L;
        this.bookmarks = 0L;
    }

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "image_name")
    private String imageName;

    @Column(name = "content")
    @FullTextField(name = "content")
    private String content;

    @Column(name = "title", nullable = false)
    @FullTextField(name = "title")
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    private Set<CommentPo> comments = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.MERGE)
    @JoinColumn(name = "category_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_post_category"))
    @ToString.Exclude
    private CategoryPo category;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "post_tag",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<TagPo> tags = new HashSet<>();

    //文章作者名稱
    @Column(name = "author_name")
    @FullTextField(name = "author_name")
    private String authorName;

    // 文章作者郵件 (收件用)
    @Email(message = "請輸入正確的郵件格式")
    @Column(name = "author_email")
    private String authorEmail;

    // 預設為0
    @Column(name = "likes",columnDefinition = "bigint default 0",nullable = false)
    private Long likes;

    @Column(name = "dislikes",columnDefinition = "bigint default 0",nullable = false)
    private Long dislikes;

    @Column(name = "bookmarks",columnDefinition = "bigint default 0",nullable = false)
    private Long bookmarks;

    // 預設為0
    @Column(name = "views",columnDefinition = "bigint default 0",nullable = false)
    private Long views;

    @Column(name = "status")
    private String status;

    @ManyToMany(mappedBy = "posts", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<RecentViewPo> recentViews = new HashSet<>();

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
         Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
         Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        PostPo postPo = (PostPo) o;
        return getId() != null && Objects.equals(getId(), postPo.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
