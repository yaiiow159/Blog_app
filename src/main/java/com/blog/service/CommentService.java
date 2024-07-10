package com.blog.service;


import com.blog.dto.CommentDto;

import java.util.List;

public interface CommentService extends CrudLikeService<CommentDto> {
    void report(CommentDto commentDto);

    List<CommentDto> findAll(Long postId);
}
