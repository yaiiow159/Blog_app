package com.blog.dao;

import com.blog.po.UserPo;
import com.blog.vo.CommentVo;
import com.blog.vo.PostVo;
import com.blog.vo.UserCommentLikeVo;
import com.blog.vo.UserPostLikeCountVo;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserJpaRepository extends CrudRepository<UserPo, Long> , JpaSpecificationExecutor<UserPo> {

    @Query(value = "SELECT users.username,users.email FROM users " +
            "INNER JOIN user_roles ON users.id = user_roles.user_id " +
            "AND (user_roles.role_id = :roleId)", nativeQuery = true)
    Optional<List<UserPo>> findUsersByRoleId(@Param("roleId") long roleId);

    @Query(value = "SELECT * FROM users WHERE username = ?1", nativeQuery = true)
    Optional<UserPo> findByUserName(String userName);

    @Query(value = "SELECT * FROM users WHERE email = ?1", nativeQuery = true)
    Optional<UserPo> findByEmail(String email);

    @Query(value = "SELECT * FROM users WHERE id = :id AND is_deleted = false", nativeQuery = true)
    Optional<UserPo> findByIdAndIsDeletedIsFalse(Long id);

    @Query(value = "SELECT * FROM users INNER JOIN user_roles ON users.id = user_roles.user_id WHERE user_roles.role_id = :id", nativeQuery = true)
    List<UserPo> findUsersByRoleName(@Param("id") long id);

    @Query(value = "SELECT * FROM users WHERE locked = :locked AND is_deleted = false", nativeQuery = true)
    List<UserPo> findByLocked(@Param("locked") boolean locked);

    @Query(value = "SELECT NEW com.blog.vo.UserCommentLikeVo(SUM(c.likes),COUNT(c)) FROM CommentPo c "
            + "WHERE c.name = :username AND c.isDeleted = false")
    UserCommentLikeVo getCommentLikeCount(@Param("username") String username);

    @Query(value = "SELECT NEW com.blog.vo.UserPostLikeCountVo(SUM(p.likes),COUNT(p)) FROM PostPo p "
            + "WHERE p.authorName = :username AND p.isDeleted = false")
    UserPostLikeCountVo getPostLikeCount(@Param("username") String username);

    @Query(value = "SELECT NEW com.blog.vo.CommentVo(c.id,c.body,c.name,c.email,c.createDate) FROM CommentPo c WHERE c.name = :username AND c.isDeleted = false")
    Set<CommentVo> getComments(@Param("username") String username);

    @Query(value = "SELECT NEW com.blog.vo.PostVo(p.id,p.title,p.authorName,p.authorEmail,p.content,p.createDate) FROM PostPo p WHERE p.authorName = :username AND p.isDeleted = false")
    Set<PostVo> getPosts(@Param("username") String username);
}