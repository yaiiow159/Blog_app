package com.blog.service;

import com.blog.dto.PostDto;
import com.blog.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface PostService {
    void add(PostDto postDto) throws ResourceNotFoundException, IOException, ExecutionException, InterruptedException;

    void edit(Long postId, PostDto postDto) throws ResourceNotFoundException, IOException, ExecutionException, InterruptedException;

    String delete(Long id) throws ResourceNotFoundException;

    PostDto findPostById(Long id) throws ResourceNotFoundException;

    Page<PostDto> findAll(String title, String authorName, Integer page, Integer size);
    PostDto findPostByCategoryId(Long id, Long postId) throws ResourceNotFoundException;

    List<PostDto> getLatestPost();

    List<PostDto> getHotPost();
    
    List<PostDto> searchByKeyword(String keyword);

    void addLike(String postId);
    
    void disLike(String postId);
    
    Long getLikeCount(String postId);

    void addView(String postId);

    Long getViewCount(String postId);

    void createDraft(PostDto postDto) throws ExecutionException, InterruptedException;

    Long getViewsCount(Long postId);
    
    Long getLikesCountById(Long postId);
}
