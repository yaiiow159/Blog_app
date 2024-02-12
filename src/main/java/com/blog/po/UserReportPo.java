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

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id",foreignKey = @ForeignKey(name = "fk_user_report_user_id"),referencedColumnName = "id")
    @ToString.Exclude
    private UserPo user;

    @Column(name = "reason", nullable = false)
    private String reason;

    @Column(name = "report_time", nullable = false)
    @CreationTimestamp
    private LocalDateTime reportTime;
}
