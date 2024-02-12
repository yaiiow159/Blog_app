package com.blog.dao;

import com.blog.po.MailNotificationPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MailNotificationPoRepository extends JpaRepository<MailNotificationPo, Long>, JpaSpecificationExecutor<MailNotificationPo> {
    @Query("SELECT m " +
            "FROM MailNotificationPo m " +
            "WHERE (:name IS NULL OR m.name = :name) " +
            "AND (:email IS NULL OR m.email = :email) " +
            "AND (:subject IS NULL OR m.subject = :subject) " +
            "AND (:isRead IS NULL OR m.isRead = :isRead)")
    List<MailNotificationPo> findByNameOrContentOrEmailOrSubjectAndIsRead(@Param("name") String name,
                                                                          @Param("email") String email,
                                                                          @Param("subject") String subject,
                                                                          @Param("isRead") boolean isRead);

    @Query("SELECT COUNT(m)" +
            "FROM MailNotificationPo m " +
            "WHERE m.isRead = false")
    Long countByIsReadFalse();
}