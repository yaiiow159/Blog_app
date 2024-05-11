package com.blog.dao;

import com.blog.po.SubscriptionPo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SubscriptionPoRepository extends JpaRepository<SubscriptionPo, Long>, JpaSpecificationExecutor<SubscriptionPo> {
    @Query("select subscriptionPo from SubscriptionPo subscriptionPo where subscriptionPo.authorName = :authorName or subscriptionPo.email = :authorEmail")
    List<SubscriptionPo> findByAuthorNameOrEmail(@Param("authorName") String authorName,@Param("authorEmail") String authorEmail);

    @Modifying
    @Query("delete from SubscriptionPo subscriptionPo where subscriptionPo.user.userName = :username")
    void deleteByUsername(@Param("username") String username);

    @Query("select count(*) from SubscriptionPo subscriptionPo where subscriptionPo.user.userName = :username")
    Integer existsByUsername(@Param("username") String username);

}