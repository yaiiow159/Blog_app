package com.blog.dao;

import com.blog.po.CommentPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentPoRepository extends JpaRepository<CommentPo, Long>, JpaSpecificationExecutor<CommentPo> {

    @Query("select c from CommentPo c inner join PostPo p on c.post.id = p.id where p.id = ?1")
    List<CommentPo> findAllByPostId(Long postId);

    List<CommentPo> findByNameOrEmail(String name, String email);
}