package com.blog.dao;

import com.blog.dto.TagDto;
import com.blog.po.TagPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TagPoRepository extends JpaRepository<TagPo, Long>, JpaSpecificationExecutor<TagPo> {
    Optional<TagPo> findByName(String name);

}