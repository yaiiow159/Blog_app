package com.blog.dao;

import com.blog.po.PostPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;

public interface PostPoRepository extends JpaRepository<PostPo, Long>, JpaSpecificationExecutor<PostPo> {
    // 增加讚數 + 1
    @Modifying
    @Query("UPDATE PostPo p SET p.likes =+ 1 WHERE p.id = :id")
    void addLike(@Param("id") long postId);
    // 減少讚數 - 1
    @Modifying
    @Query("UPDATE PostPo p SET p.dislikes =+ 1 WHERE p.id = :id")
    void disLike(@Param("id") long postId);

    @Query("SELECT p.likes FROM PostPo p WHERE p.id = :id")
    Integer getLikeCount(@Param("id") long postId);

    @Query("SELECT p.views FROM  PostPo p WHERE p.id = :id")
    Long getViewsCountById(@Param("id") Long postId);


    @Query("SELECT p FROM PostPo p ORDER BY p.createDate DESC limit 10")
    List<PostPo> findTop10ByOrderByIdDesc();


    @Query("SELECT p.dislikes FROM PostPo p WHERE p.id = :id")
    Integer getDislikeCount(@Param("id") Long postId);

    @Modifying
    @Query("UPDATE PostPo p SET p.bookmarks =+ 1 WHERE p.id = :id")
    void addBookmark(@Param("id") Long id);

    @Modifying
    @Query("UPDATE PostPo p SET p.bookmarks =- 1 WHERE p.id = :id")
    void deleteBookmark(@Param("id") Long id);

    @Query("SELECT p.bookmarks FROM PostPo p WHERE p.id = :id")
    Integer getBookmarkCount(@Param("id") Long postId);

    @Modifying
    @Query("UPDATE PostPo p SET p.views =+ 1 WHERE p.id = :id")
    void addPostView(@Param("id") Long id);

    @Query("SELECT p FROM PostPo p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.tags WHERE p.views > 0 ORDER BY p.views DESC")
    List<PostPo> findPopularPost();

    @Query("SELECT p FROM PostPo p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.tags " +
            "INNER JOIN SubscriptionPo s ON p.authorName = s.authorName "+
            "WHERE s.user.userName = :username")
    List<PostPo> getFavoritePost(@Param("username") String username);

    @Query("SELECT p FROM PostPo p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.tags WHERE p.authorName = :username")
    List<PostPo> getPersonalPost(@Param("username") String username);

    @Query("SELECT DISTINCT p FROM PostPo p JOIN p.tags t WHERE t.id = :tagId")
    List<PostPo> findByTag(@Param("tagId") Long id);

    @Modifying
    @Query("UPDATE PostPo p SET p.imageUrl = :imageUrl,p.imageName = :imageName WHERE p.authorName = :currentUser")
    void updatePostImage(@Param("imageUrl") String imageUrl,@Param("imageName") String imageName,@Param("currentUser") String currentUser);
}