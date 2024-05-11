package com.blog.service;

import com.blog.dto.CommentDto;
import com.blog.exception.ResourceNotFoundException;

import java.util.List;

public interface CommentService {
    void add(Long postId, CommentDto commentDto) throws ResourceNotFoundException;
    void edit(Long postId, Long id , CommentDto commentDto) throws ResourceNotFoundException;
    void delete(Long postId , Long id) throws ResourceNotFoundException;
    CommentDto findComment(Long postId,Long id) throws ResourceNotFoundException;

    List<CommentDto> findAllComments(Long postId) throws ResourceNotFoundException;

    void reportComment(CommentDto commentDto) throws ResourceNotFoundException;

    void likeComment(Long postId, Long id);

    void cancelLikeComment(Long postId, Long id);

    Integer findLikeCount(Long postId, Long id);
}
