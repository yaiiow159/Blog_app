package com.blog.dao;

import com.blog.po.CommentPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CommentPoRepository extends JpaRepository<CommentPo, Long>, JpaSpecificationExecutor<CommentPo> {
    @Query("SELECT c FROM CommentPo c INNER JOIN PostPo p ON c.post.id = p.id WHERE p.id = ?1")
    List<CommentPo> findAllByPostId(Long postId);

    @Query("SELECT c FROM CommentPo c INNER JOIN PostPo p ON c.post.id = p.id WHERE p.id = ?1 AND c.id = ?2")
    Optional<CommentPo> findByPostIdAndId(Long postId,Long id);

    @Query("SELECT c FROM CommentPo c WHERE c.post.id = ?1 AND c.isDeleted = false")
    List<CommentPo> findAllByPostIdAndIsDeletedFalse(Long postId);

    @Query("SELECT c FROM CommentPo c WHERE c.id = ?1 AND c.isDeleted = false")
    Optional<CommentPo> findByIdAndIsDeletedFalse(Long id);

    // 增加特定文章的評論的按讚數
    @Modifying
    @Query("UPDATE CommentPo c SET c.likes = c.likes + 1 WHERE c.post.id = ?1 AND c.id = ?2")
    void addCommentLike(Long postId, Long commentId);

    @Modifying
    @Query("UPDATE CommentPo c SET c.dislikes = c.dislikes + 1 WHERE c.post.id = ?1 AND c.id = ?2")
    void addCommentDisLike(Long postId, Long commentId);

    @Query("SELECT c.likes FROM CommentPo c WHERE c.id = ?1")
    Long getCommentLike(Long id);

    @Query("SELECT c.dislikes FROM CommentPo c WHERE c.id = ?1")
    Long getCommentDisLike(Long id);
}