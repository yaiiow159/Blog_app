package com.blog.dao;

import com.blog.dto.PostHistoryPoDto;
import com.blog.po.PostHistoryPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PostHistoryPoRepository extends JpaRepository<PostHistoryPo, Long>, JpaSpecificationExecutor<PostHistoryPo> {
    @Modifying
    @Query("DELETE FROM PostHistoryPo p WHERE p.postId IN :postIds")
    void deleteWherePostIdIn(@Param("postIds") List<Long> postIds);
    List<PostHistoryPo> getHistoryByAuthorName(String authorName);
}