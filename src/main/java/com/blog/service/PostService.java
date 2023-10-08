package com.blog.service;

import com.blog.dto.PostDto;
import com.blog.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PostService {
    PostDto createPost(Long categoryId,PostDto postDto) throws ResourceNotFoundException;

    PostDto updatePost(Long categoryId,PostDto postDto) throws ResourceNotFoundException;

    List<PostDto> getAllPosts();

    PostDto getOnePost(long id);

    Page<PostDto> getAllPosts(String title,String content,String description,int page, int size, String sort);

    String deletePost(long id) throws ResourceNotFoundException;

    Page<PostDto> findPosts(Long id) throws ResourceNotFoundException;

    PostDto findTheOnePostsByCategory(Long id, Long postId) throws ResourceNotFoundException;
}
