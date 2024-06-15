package com.blog.po;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serial;
import java.time.LocalDateTime;


@Entity
@Table(name = "user_report")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class UserReportPo implements java.io.Serializable {

    @Serial
    private final static long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "user_id",foreignKey = @ForeignKey(name = "fk_user_report_user_id"),referencedColumnName = "id")
    @ToString.Exclude
    private UserPo user;

    @Column(name = "status", nullable = false)
    private boolean status;

    @Column(name = "reason", nullable = false)
    private String reason;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "comment_id",foreignKey = @ForeignKey(name = "fk_user_report_comment_id"),referencedColumnName = "id")
    @ToString.Exclude
    private CommentPo comment;

    @Column(name = "report_time", nullable = false)
    @CreationTimestamp
    private LocalDateTime reportTime;
}
