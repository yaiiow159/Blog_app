package com.blog.dao;

import com.blog.mapper.UserGroupPoMapper;
import com.blog.po.UserGroupPo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface UserGroupPoRepository extends JpaRepository<UserGroupPo, Long>, JpaSpecificationExecutor<UserGroupPo> {

    Optional<UserGroupPo> findByGroupName(String groupName);
    Optional<UserGroupPo> findByIdAndIsDeletedFalse(Long id);

    List<UserGroupPo> findByIsDeletedFalse();
}