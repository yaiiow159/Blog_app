package com.blog.dao;

import com.blog.po.SubscriptionPo;

import org.apache.lucene.index.DocIDMerger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SubscriptionPoRepository extends JpaRepository<SubscriptionPo, Long>, JpaSpecificationExecutor<SubscriptionPo> {
    @Query("select subscriptionPo from SubscriptionPo subscriptionPo where subscriptionPo.authorName = :authorName or subscriptionPo.email = :authorEmail")
    List<SubscriptionPo> findByAuthorNameOrEmail(@Param("authorName") String authorName,@Param("authorEmail") String authorEmail);

    @Query("SELECT s FROM SubscriptionPo s " +
            "JOIN s.user u " +
            "JOIN s.post p " +
            "WHERE u.userName = :username AND p.id = :postId")
    Optional<SubscriptionPo> findByAuthorNameAndPostId(@Param("username") String username, @Param("postId") Long postId);

    @Modifying
    @Query("delete from SubscriptionPo subscriptionPo where subscriptionPo.authorName = :authorName and subscriptionPo.post.id = :postId")
    void deleteByAuthorNameAndPostId(@Param("authorName") String authorName,@Param("postId") Long postId);
}