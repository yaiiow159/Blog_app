package com.blog.dao;

import com.blog.po.UserPo;
import com.blog.vo.CommentVo;
import com.blog.vo.PostVo;
import com.blog.vo.UserCommentLikeVo;
import com.blog.vo.UserPostLikeCountVo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserPoRepository extends JpaRepository<UserPo, Long>, JpaSpecificationExecutor<UserPo> {

    @Query(value = "SELECT users.username,users.email FROM users " +
            "INNER JOIN user_roles ON users.id = user_roles.user_id " +
            "AND (user_roles.role_id = :roleId)", nativeQuery = true)
    Optional<List<UserPo>> findUsersByRoleId(@Param("roleId") long roleId);

    @Query(value = "SELECT * FROM users WHERE username = ?1", nativeQuery = true)
    Optional<UserPo> findByUserName(String userName);

    @Query(value = "SELECT * FROM users WHERE email = ?1", nativeQuery = true)
    Optional<UserPo> findByEmail(String email);

    @Query(value = "SELECT * FROM users INNER JOIN user_roles ON users.id = user_roles.user_id WHERE user_roles.role_id = :id", nativeQuery = true)
    List<UserPo> findUsersByRoleName(@Param("id") long id);

    @Query(value = "SELECT * FROM users WHERE locked = :locked AND is_deleted = false", nativeQuery = true)
    List<UserPo> findByLocked(@Param("locked") boolean locked);

    @Query(value = "SELECT NEW com.blog.vo.UserCommentLikeVo(SUM(c.likes),COUNT(c)) FROM CommentPo c "
            + "WHERE c.name = :username")
    UserCommentLikeVo getCommentLikeCount(@Param("username") String username);

    @Query(value = "SELECT NEW com.blog.vo.UserPostLikeCountVo(SUM(p.likes),COUNT(p)) FROM PostPo p "
            + "WHERE p.authorName = :username")
    UserPostLikeCountVo getPostLikeCount(@Param("username") String username);

    @Query(value = "SELECT NEW com.blog.vo.CommentVo(c.id,c.content,c.name,c.email,c.createDate) FROM CommentPo c WHERE c.name = :username")
    Set<CommentVo> getComments(@Param("username") String username);

    @Query(value = "SELECT NEW com.blog.vo.PostVo(p.id,p.title,p.authorName,p.authorEmail,p.content,p.createDate) FROM PostPo p WHERE p.authorName = :username")
    Set<PostVo> getPosts(@Param("username") String username);

    @Modifying
    @Query(value = "UPDATE users SET password = :newPassword WHERE username = :username", nativeQuery = true)
    void changePassword(@Param("newPassword") String newPassword,@Param("username") String username);

    @Query(value = "SELECT * FROM users WHERE username = :userName OR email = :email", nativeQuery = true)
    Optional<UserPo> findByUserNameOrEmail(@Param("userName") String userName,@Param("email") String email);

    @Modifying
    @Query(value = "UPDATE UserPo SET imageUrl = :avatarUrl, imageName = :imageName WHERE userName = :username")
    @Transactional
    void updateUserAvatar(@Param("avatarUrl") String avatarUrl,@Param("imageName") String imageName, @Param("username") String username);
}