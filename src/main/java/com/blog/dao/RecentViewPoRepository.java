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


public interface RecentViewPoRepository extends JpaRepository<RecentViewPo, Long>, JpaSpecificationExecutor<RecentViewPo> {
    // 查詢特定使用者所瀏覽過的所有文章紀錄
    @Query("SELECT " +
            "NEW com.blog.vo.PostVo(p.id, p.title, p.authorName, p.authorEmail, p.content, rv.createTime) " +
            "FROM RecentViewPo rv " +
            "JOIN rv.posts p " +
            "WHERE rv.user.id = :userId " +
            "AND (:createTime IS NULL OR rv.createTime < :createTime)")
    Page<PostVo> findPostPoByUserIdAndCreateTimeBefore(@Param("userId") Long userId, @Param("createTime") LocalDateTime createTime, Pageable pageable);
}