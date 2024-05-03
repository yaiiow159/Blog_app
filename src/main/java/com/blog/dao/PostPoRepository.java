package com.blog.dao;

import com.blog.po.PostPo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.parameters.P;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PostPoRepository extends JpaRepository<PostPo, Long>, JpaSpecificationExecutor<PostPo> {

    @Query("SELECT p FROM PostPo p WHERE p.id = ?1 AND p.isDeleted = false")
    PostPo findByIdAndIsDeletedFalse(long id);


    // 查詢創建時間最新的前五筆文章
    @Query("SELECT p FROM PostPo p WHERE p.isDeleted = false ORDER BY p.createDate DESC LIMIT 5")
    List<PostPo> findTop5ByIsDeletedFalseAndAndCreateDate();

    @Query("SELECT p FROM PostPo p INNER JOIN CategoryPo c ON p.category.id = c.id WHERE p.id = ?1 AND p.isDeleted = false")
    Optional<PostPo> findByPostIdAndIsDeletedFalseAndCategoryId(Long postId, Long categoryId);

    @Query(" SELECT p FROM PostPo p " +
           " LEFT JOIN SubscriptionPo s ON p.id = s.post.id " +
           " WHERE p.isDeleted = false GROUP BY p.id ORDER BY COUNT(s.id) DESC LIMIT 5 ")
    List<PostPo> findTop5Posts();

    // 增加讚數 + 1
    @Modifying
    @Query("UPDATE PostPo p SET p.likes = p.likes + 1 WHERE p.id = ?1")
    void addLike(long postId);

    // 減少讚數 - 1
    @Modifying
    @Query("UPDATE PostPo p SET p.likes = p.likes - 1 WHERE p.id = ?1")
    void disLike(long postId);

    @Query("SELECT p.likes FROM PostPo p WHERE p.id = ?1")
    Long getLikeCount(long postId);

    @Modifying
    @Query("UPDATE PostPo p SET p.views = p.views + 1 WHERE p.id = ?1")
    void addView(long postId);

    @Query("SELECT p.views FROM  PostPo p WHERE p.id = ?1")
    Long getViewCount(long postId);

    @Query("SELECT p.views FROM  PostPo p WHERE p.id = ?1")
    Long getViewsCountById(Long postId);

    @Query("SELECT p.likes FROM  PostPo p WHERE p.id = ?1")
    Long getLikesCountById(Long postId);
}