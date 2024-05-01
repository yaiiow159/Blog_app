package com.blog.dao;

import com.blog.po.MailNotificationPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MailNotificationPoRepository extends JpaRepository<MailNotificationPo, Long>, JpaSpecificationExecutor<MailNotificationPo> {
    @Query("SELECT COUNT(m)" +
            "FROM MailNotificationPo m " +
            "WHERE m.isRead = false")
    Long countByIsReadFalse();
}