package com.blog.po;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "recent_view")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class RecentViewPo implements java.io.Serializable {

    @Serial
    private final static long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id",foreignKey = @ForeignKey(name = "fk_recent_view_user_id"),referencedColumnName = "id")
    @ToString.Exclude
    private UserPo user;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "recent_view_post",
            joinColumns = @JoinColumn(name = "recent_view_id"),
            inverseJoinColumns = @JoinColumn(name = "post_id"),
            foreignKey = @ForeignKey(name = "fk_recent_view_post_recent_view_id"),
            inverseForeignKey = @ForeignKey(name = "fk_recent_view_post_post_id"))
    @ToString.Exclude
    private Set<PostPo> posts = new HashSet<>();

    @Column(name = "create_time", nullable = false)
    @CreationTimestamp
    private LocalDateTime createTime;
}
