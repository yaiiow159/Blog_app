package com.blog.dao;

import com.blog.po.CategoryPo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryPoRepository extends JpaRepository<CategoryPo, Long>, JpaSpecificationExecutor<CategoryPo> {
    @Query(value = "SELECT COUNT(1) FROM categories INNER JOIN posts ON categories.id = posts.category_id WHERE categories.id = ?",nativeQuery = true)
    int countByPostIfExist(Long id);
}