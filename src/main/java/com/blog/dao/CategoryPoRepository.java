package com.blog.dao;

import com.blog.po.CategoryPo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface CategoryPoRepository extends JpaRepository<CategoryPo, Long>, JpaSpecificationExecutor<CategoryPo> {
    CategoryPo findByName(String name);

    List<CategoryPo> findAllByIsDeletedFalse();

    Optional<CategoryPo> findByIdAndIsDeletedFalse(Long categoryId);
}