package com.blog.po;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Getter
@Setter
@Entity
@Table(name = "user_group")
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserGroupPo extends BasicPo implements Serializable {

    public static final String DEFAULT_GROUP_NAME = "admin";
    /**
     * 群組名稱
     */
    @Column(name = "group_name")
    private String groupName;

    /**
     * 群組描述
     */
    @Column(name = "description")
    private String description;

    /**
     * 覆核權限等級
     */
    @Column(name = "review_level")
    private String reviewLevel;

    @OneToMany(mappedBy = "userGroupPo",cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @ToString.Exclude
    private List<UserPo> userPoList = new ArrayList<>();

    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        UserGroupPo that = (UserGroupPo) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
