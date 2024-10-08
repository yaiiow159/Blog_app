package com.blog.dao;

import com.blog.po.UserGroupPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserGroupPoRepository extends JpaRepository<UserGroupPo, Long>, JpaSpecificationExecutor<UserGroupPo> {
    Optional<UserGroupPo> findByGroupName(String groupName);

    @Query("select ug from UserGroupPo ug join ug.userPoList u on u.id = ?1 and u.userGroupPo = ug")
    UserGroupPo findByUserPoListContaining(Long id);

    @Query("select count(ug) from UserGroupPo ug join ug.userPoList u on u.id = ?1 and u.userGroupPo = ug")
    int countByUserGroupId(Long id);
}