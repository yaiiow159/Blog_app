package com.blog.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.jpa.repository.EntityGraph;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@ToString
@Entity
@Table(name = "users")
@AllArgsConstructor
public class UserPo extends BasicPo implements Serializable {

    public static final String DEFAULT_USER_NAME = "admin";
    public UserPo() {
        this.isLocked = false;
        roles = new HashSet<>();
    }

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "username")
    private String userName;

    @Column(name = "nickname")
    private String nickName;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    @Email(message = "Email格式錯誤")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "address")
    private String address;

    // 圖片存儲地址
    @Column(name = "avatar_name")
    private String avatarName;

    @Column(name = "avatar_path")
    private String avatarPath;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_group_po_id",nullable = false,
            foreignKey = @ForeignKey(name = "fk_user_group_user"),
            referencedColumnName = "id")
    @ToString.Exclude
    private UserGroupPo userGroupPo;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id",
                    referencedColumnName = "id",
                    foreignKey = @ForeignKey(name = "fk_user_role_user")),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id",
                    foreignKey = @ForeignKey(name = "fk_user_role_role")))
    @ToString.Exclude
    private Set<RolePo> roles;

    @Column(name = "locked",nullable = false ,columnDefinition = "tinyint(1) default 0")
    private boolean isLocked;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<RecentViewPo> recentViews;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        UserPo userPo = (UserPo) o;
        return getId() != null && Objects.equals(getId(), userPo.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
