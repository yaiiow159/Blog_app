package com.blog.service;

import com.blog.dto.PostDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PostService extends CrudLikeService<PostDto>  {
    List<PostDto> findLatestPost();

    List<PostDto> findPopularPost();

    List<PostDto> getPersonalPost();

    List<PostDto> findFavoritePost();

    List<PostDto> findByKeyword(String keyword);

    List<PostDto> findByTag(Long id);

    Page<PostDto> findAll(Integer page, Integer pageSize, String title, String authorName,String authorEmail);
    
    void deleteBookmark(Long id);

    void addBookmark(Long id);

    void saveDraft(PostDto postDto);

    void addView(Long id);

    Integer getDislikesCount(Long postId);

    Integer getBookmarksCount(Long postId);

    Long getViewsCount(Long postId);

    Integer getLikesCount(Long postId);
}
