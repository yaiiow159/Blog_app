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

    @Query("SELECT p FROM PostPo p WHERE p.isDeleted = false")
    List<PostPo> findByIsDeletedFalse();

    @Query("SELECT p FROM PostPo p WHERE p.isDeleted = false")
    Page<PostPo> findByIsDeletedFalse(Pageable pageable);

    @Query("SELECT p FROM PostPo p WHERE p.authorName = ?1 OR p.authorEmail = ?2 AND p.isDeleted = false")
    List<PostPo> findByAuthorNameOrAuthorEmail(String authorName, String authorEmail);

    @Query("SELECT p FROM PostPo p WHERE p.createDate < ?1 AND p.isDeleted = false")
    List<PostPo> findByCreateDateBefore(LocalDateTime nowDate);

    @Query("SELECT p FROM PostPo p WHERE p.title = ?1 AND p.isDeleted = false")
    PostPo findByTitle(String name);

    // 使用SQL語法查詢
    @Query("SELECT p FROM PostPo p WHERE p.title LIKE %?1% AND p.isDeleted = false")
    List<PostPo> findByQuery(String query);

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
}