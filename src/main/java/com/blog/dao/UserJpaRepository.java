package com.blog.dao;

import com.blog.po.UserPo;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface UserJpaRepository extends CrudRepository<UserPo, Long> , JpaSpecificationExecutor<UserPo> {

    @Query(value = "SELECT users.username,users.email FROM users " +
            "INNER JOIN user_roles ON users.id = user_roles.user_id " +
            "AND (user_roles.role_id = :roleId)", nativeQuery = true)
    Optional<List<UserPo>> findUsersByRoleId(@Param("roleId") long roleId);

    Optional<UserPo> findByUserName(String userName);

    Optional<UserPo> findByEmail(String email);

    Optional<UserPo> findByIdAndIsDeletedIsFalse(Long id);

    Optional<UserPo> findByIsDeletedIsFalse();
}