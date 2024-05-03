package com.blog.po;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "mail_notification")
@Getter
@Setter
@ToString
@AllArgsConstructor
public class MailNotificationPo implements Serializable {

    public MailNotificationPo() {
        this.isRead = false;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "username")
    @NotBlank(message = "使用者名稱不得為空")
    private String username;

    @Column(name = "action")
    @NotBlank(message = "動作不得為空")
    private String action;

    @Column(name = "name")
    @NotBlank(message = "名稱不得為空")
    private String name;

    @Column(name = "send_by")
    @NotBlank(message = "發送人不得為空")
    private String sendBy;

    @Column(name = "email")
    @NotBlank(message = "電子郵件不得為空")
    @Email(message = "電子郵件格式錯誤")
    private String email;

    @Column(name = "content")
    private String content;

    @Column(name = "subject")
    private String subject;

    @Column(name = "send_time")
    @CreationTimestamp
    private LocalDateTime sendTime;

    @Column(name = "is_read",columnDefinition = "boolean default false",nullable = false)
    private boolean isRead = false;

    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        MailNotificationPo that = (MailNotificationPo) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
