package com.blog.service;

import com.blog.dto.CommentDto;
import com.blog.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CommentService {
    CommentDto createComment(Long postId, CommentDto commentDto) throws ResourceNotFoundException;
    CommentDto findComment(Long postId,Long id) throws ResourceNotFoundException;
    CommentDto updateComment(Long postId,Long id ,CommentDto commentDto) throws ResourceNotFoundException;
    String deleteComment(Long postId ,Long id) throws ResourceNotFoundException;

    List<CommentDto> findAllComments(Long postId) throws ResourceNotFoundException;

    String reportComment(CommentDto commentDto) throws ResourceNotFoundException;

    void addCommentLike(Long postId, Long commentId);

    void addCommentDisLike(Long postId, Long commentId);

    Long getCommentLikeCount(Long commentId);

    Long getCommentDisLikeCount(Long commentId);
}
