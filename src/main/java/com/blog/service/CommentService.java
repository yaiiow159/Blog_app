package com.blog.service;

import com.blog.dto.CommentDto;
import com.blog.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;

public interface CommentService {
    CommentDto createComment(Long postId, CommentDto commentDto) throws ResourceNotFoundException;
    CommentDto findComment(Long postId,Long id) throws ResourceNotFoundException;
    CommentDto updateComment(Long postId, CommentDto commentDto) throws ResourceNotFoundException;
    String deleteComment(Long id) throws ResourceNotFoundException;

    Page<CommentDto> findAllComments(Long postId, int page, int size, String sort) throws ResourceNotFoundException;
}
