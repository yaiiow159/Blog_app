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
    

    // 增加讚數 + 1
    @Modifying
    @Query("UPDATE PostPo p SET p.likes =+ 1 WHERE p.id = ?1")
    void addLike(long postId);

    // 減少讚數 - 1
    @Modifying
    @Query("UPDATE PostPo p SET p.likes=- 1 WHERE p.id = ?1")
    void disLike(long postId);

    @Query("SELECT p.likes FROM PostPo p WHERE p.id = ?1")
    Integer getLikeCount(long postId);
    

    @Query("SELECT p.views FROM  PostPo p WHERE p.id = ?1")
    Long getViewsCountById(Long postId);

    @Query("SELECT p.likes FROM  PostPo p WHERE p.id = ?1")
    Long getLikesCountById(Long postId);

    @Query("SELECT p FROM PostPo p ORDER BY p.createDate DESC limit 10")
    List<PostPo> findTop5ByOrderByIdDesc();

    @Modifying
    @Query("UPDATE PostPo p SET p.imageName = ?1 WHERE p.id = ?2")
    void updateImageName(String imageName, Long postId);

    @Modifying
    @Query("UPDATE PostPo p SET p.bookmarks =+ 1 WHERE p.id = ?1")
    void addBookmark(Long id);

    @Modifying
    @Query("UPDATE PostPo p SET p.bookmarks =- 1 WHERE p.id = ?1")
    void deleteBookmark(Long id);

    @Query("SELECT p.bookmarks FROM PostPo p WHERE p.id = ?1")
    Integer getBookmarkCount(Long postId);

    @Modifying
    @Query("UPDATE PostPo p SET p.views =+ 1 WHERE p.id = ?1")
    void addPostView(Long id);
    

    @Query("SELECT p FROM PostPo p WHERE p.views > 0 ORDER BY p.views DESC LIMIT 10")
    List<PostPo> findPopularPost();


    @Query("SELECT p FROM PostPo p JOIN p.tags t WHERE t.id = ?1")
    List<PostPo> findAllByTagId(Long tagId);

//    List<PostPo> getBookmarkList(String username);
}