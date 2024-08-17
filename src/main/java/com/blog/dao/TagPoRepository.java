package com.blog.dao;

import com.blog.dto.TagDto;
import com.blog.po.TagPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TagPoRepository extends JpaRepository<TagPo, Long>, JpaSpecificationExecutor<TagPo> {
    @Query(value = "SELECT t.* FROM tag t WHERE t.id IN (SELECT tp.tag_id FROM post_tag tp WHERE tp.post_id = :id)", nativeQuery = true)
    List<TagPo> findAllTagsByPostId(@Param("id") Long postId);
    @Query("SELECT t FROM TagPo t ORDER BY t.createDate DESC LIMIT 10")
    List<TagPo> findHotTags();

    @Query(value = "SELECT COUNT(1) FROM tag INNER JOIN post_tag on tag.id = post_tag.tag_id WHERE tag.id = ?",nativeQuery = true)
    int countByPostIfExist(Long id);
}