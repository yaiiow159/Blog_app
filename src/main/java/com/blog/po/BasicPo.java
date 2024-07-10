package com.blog.po;

import com.blog.utils.SpringSecurityUtil;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Getter
@Setter
@MappedSuperclass
public abstract class BasicPo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "create_user",updatable = false)
    String creatUser;// VARCHAR2(16)

    @Column(name = "create_date",updatable = false)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    LocalDateTime createDate;// TIMESTAMP

    @Column(name = "update_user")
    String updateUser;// VARCHAR2(16)

    @Column(name = "update_date")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    LocalDateTime updDate;// TIMESTAMP

    @PrePersist
    protected void onCreate() {
        this.creatUser = SpringSecurityUtil.getCurrentUser();
        this.createDate = LocalDateTime.now(ZoneId.of("Asia/Taipei"));
    }

    @PreUpdate
    protected void onUpdate() {
        this.updateUser = SpringSecurityUtil.getCurrentUser();
        this.updDate = LocalDateTime.now(ZoneId.of("Asia/Taipei"));
    }
}