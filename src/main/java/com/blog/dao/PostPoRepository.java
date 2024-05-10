package com.blog.dao;

import com.blog.enumClass.PostStatus;
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



    // 查詢創建時間最新的前五筆文章
    @Query(" SELECT p FROM PostPo p " +
           " LEFT JOIN SubscriptionPo s ON p.id = s.post.id " +
           " GROUP BY p.id ORDER BY COUNT(s.id) DESC LIMIT 5 ")
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

    @Query("SELECT p FROM PostPo p ORDER BY p.createDate DESC limit 5")
    List<PostPo> findTop5ByOrderByIdDesc();

    @Modifying
    @Query("UPDATE PostPo p SET p.imageName = ?1 WHERE p.id = ?2")
    void updateImageName(String s, Long postId);

    @Modifying
    @Query("UPDATE PostPo p SET p.status = ?1 WHERE p.id = ?2")
    void addBookmark(Long id);

    @Modifying
    @Query("DELETE FROM PostPo p WHERE p.id = ?1")
    void deleteBookmark(Long id);

    @Query("SELECT p FROM PostPo p INNER JOIN SubscriptionPo s ON p.id = s.post.id WHERE s.user.userName = ?1")
    List<PostPo> getBookmarksByUsername(String username);
}