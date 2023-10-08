package com.blog.dao;

import com.blog.po.PostPo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface PostPoRepository extends JpaRepository<PostPo, Long>, JpaSpecificationExecutor<PostPo> {

    PostPo findByIdAndIsDeletedFalse(long id);

    List<PostPo> findByIsDeletedFalse();

    Page<PostPo> findByIsDeletedFalse(Pageable pageable);

    List<PostPo> findByAuthorNameOrAuthorEmail(String authorName, String authorEmail);
}