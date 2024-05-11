package com.blog.service;

import com.blog.dto.PostDto;
import com.blog.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface PostService {
    void add(PostDto postDto) throws ResourceNotFoundException, IOException, ExecutionException, InterruptedException;

    void edit(Long postId, PostDto postDto) throws ResourceNotFoundException, IOException, ExecutionException, InterruptedException;

    String delete(Long id) throws ResourceNotFoundException, IOException;

    PostDto findPostById(Long id) throws ResourceNotFoundException;

    Page<PostDto> findAll(String title, String authorName, Integer page, Integer size);
    PostDto findPostByCategoryId(Long id, Long postId) throws ResourceNotFoundException;

    List<PostDto> getLatestPost();

    List<PostDto> getHotPost();
    
    List<PostDto> searchByKeyword(String keyword);

    void addLike(Long postId);
    
    void disLike(Long postId);

    void createDraft(PostDto postDto) throws ExecutionException, InterruptedException, IOException;

    Long getViewsCount(Long postId);
    
    Long getLikesCountById(Long postId);

    void upload(MultipartFile file,Long postId) throws IOException, ExecutionException, InterruptedException;

    void addBookmark(Long id);

    void deleteBookmark(Long id);

//    List<PostDto> getBookmarksList(String username);

    Integer getLikesCount(Long postId);

    Integer getBookmarksCount(Long postId);

    void addPostView(Long id);

    List<PostDto> searchByTag(Long id);
}
