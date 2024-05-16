package com.blog.dao;

import com.blog.po.RolePo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RolePoRepository extends JpaRepository<RolePo, Long>, JpaSpecificationExecutor<RolePo> {

    @Query(value = "SELECT * FROM roles WHERE name = ?1", nativeQuery = true)
    Optional<RolePo> findByName(String roleName);

    @Query(value = "SELECT * FROM roles JOIN blog_app.user_roles ur on roles.id = ur.role_id WHERE ur.user_id = ?1", nativeQuery = true)
    List<RolePo> findByUserId(long userId);

    Optional<RolePo> findByRoleName(String string);


}