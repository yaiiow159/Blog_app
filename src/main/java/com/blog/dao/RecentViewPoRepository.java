package com.blog.dao;

import com.blog.po.RecentViewPo;

import com.blog.vo.PostVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;


public interface RecentViewPoRepository extends JpaRepository<RecentViewPo, Long>, JpaSpecificationExecutor<RecentViewPo> {
    // 查詢特定使用者所瀏覽過的所有文章紀錄
    @Query("SELECT " +
            "NEW com.blog.vo.PostVo(p.id, p.title, p.authorName, p.authorEmail, p.content, rv.createTime) " +
            "FROM RecentViewPo rv " +
            "JOIN rv.posts p " +
            "WHERE rv.user.id = :userId ")
    Page<PostVo> findPostPoByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT " +
            "NEW com.blog.vo.PostVo(p.id, p.title, p.authorName, p.authorEmail, p.content, rv.createTime) " +
            "FROM RecentViewPo rv " +
            "JOIN rv.posts p " +
            "WHERE p.id = :id ")
    Optional<PostVo> findPostVoById(@Param("id") Long id);

    @Query("SELECT " +
            "NEW com.blog.vo.PostVo(p.id, p.title, p.authorName, p.authorEmail, p.content, rv.createTime) " +
            "FROM RecentViewPo rv " +
            "JOIN rv.posts p " +
            "WHERE rv.user.id = :userId " +
            "AND p.title LIKE %:title% " +
            "AND p.authorName LIKE %:authorName% " +
            "AND p.authorEmail LIKE %:authorEmail% ")
    Page<PostVo> findPostPoByAuthorNameAndAuthorEmailAndTitleAndUserId(@Param("authorName") String authorName,
                                                                       @Param("authorEmail") String authorEmail,
                                                                       @Param("title") String title,
                                                                       @Param("userId") Long userId,
                                                                       Pageable pageable);
}