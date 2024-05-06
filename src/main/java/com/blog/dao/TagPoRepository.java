package com.blog.dao;

import com.blog.po.TagPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TagPoRepository extends JpaRepository<TagPo, Long>, JpaSpecificationExecutor<TagPo> {
    Optional<TagPo> findByName(String name);

    @Query(value = "SELECT t.* FROM tag t WHERE t.id IN (SELECT tp.tag_id FROM post_tag tp WHERE tp.post_id = :id)", nativeQuery = true)
    List<TagPo> findAllTagsByPostId(@Param("id") Long postId);
}