package com.blog.service;

import com.blog.dto.PostDto;
import com.blog.exception.ResourceNotFoundException;
import com.blog.po.PostPo;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface PostService {
    PostDto createPost(Long categoryId,PostDto postDto) throws ResourceNotFoundException, IOException, ExecutionException, InterruptedException;

    PostDto updatePost(Long categoryId,Long postId,PostDto postDto) throws ResourceNotFoundException, IOException, ExecutionException, InterruptedException;

    List<PostDto> getAllPosts();

    PostDto getOnePost(long id);

    Page<PostDto> getAllPosts(String title,String content,String authorName,int page, int size, String sort, String direction);

    String deletePost(long id) throws ResourceNotFoundException;

    Page<PostDto> findPosts(Long id) throws ResourceNotFoundException;

    PostDto findTheOnePostsByCategory(Long id, Long postId) throws ResourceNotFoundException;

    List<PostPo> findByCreateDateBefore(LocalDateTime nowDate);

    boolean existsByPostId(Long postId);

    List<PostDto> getLatestPost();

    List<PostDto> getHotPost();
    
    List<PostDto> searchByKeyword(String keyword);

    void addLike(String postId);
    
    void disLike(String postId);
    
    Long getLikeCount(String postId);

    void addView(String postId);

    Long getViewCount(String postId);

    PostDto createDraft(PostDto postDto);
}
