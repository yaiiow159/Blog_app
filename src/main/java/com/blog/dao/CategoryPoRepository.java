package com.blog.dao;

import com.blog.po.CategoryPo;
import com.blog.po.PostPo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryPoRepository extends JpaRepository<CategoryPo, Long>, JpaSpecificationExecutor<CategoryPo> {
    Optional<CategoryPo> findByName(String name);

    Page<CategoryPo> findAllByIsDeletedFalse(Pageable pageable);

    Optional<CategoryPo> findByIdAndIsDeletedFalse(Long categoryId);
}