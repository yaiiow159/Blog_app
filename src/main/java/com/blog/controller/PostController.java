package com.blog.controller;

import com.blog.dto.ApiResponse;
import com.blog.dto.PostDto;
import com.blog.exception.ResourceNotFoundException;
import com.blog.service.PostService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@Tag(name = "文章相關功能", description = "文章相關功能")
@Slf4j
@RestController
@RequestMapping("/api/v1/posts")
public class PostController {
    @Resource
    private PostService postService;

    @GetMapping("/latest")
    @Operation(summary = "查詢最新文章",description = "查詢最新文章")
    public ApiResponse<List<PostDto>> getLatestPost(){
        return new ApiResponse<>(true, "查詢成功", postService.getLatestPost(), HttpStatus.OK);
    }

    @GetMapping("/popular")
    @Operation(summary = "查詢熱門文章",description = "查詢熱門文章")
    public ApiResponse<List<PostDto>> getHotPost(){
        return new ApiResponse<>(true, "查詢成功", postService.getHotPost(), HttpStatus.OK);
    }
    @GetMapping("/{id}")
    @Operation(summary = "查詢文章",description = "利用id查詢文章")
    public ApiResponse<PostDto> getPost(@Parameter(description = "文章id",example = "1") @PathVariable Long id) throws ResourceNotFoundException {
        return new ApiResponse<>(true, "查詢成功", postService.findPostById(id), HttpStatus.OK);
    }

    @GetMapping("/{postId}/viewsCount")
    @Operation(summary = "查詢文章瀏覽次數",description = "查詢文章瀏覽次數")
    public ApiResponse<Long> getViewsCount(@Parameter(description = "文章id",example = "1") @PathVariable Long postId) {
        return new ApiResponse<>(true, "查詢成功", postService.getViewsCount(postId), HttpStatus.OK);
    }

    @GetMapping("/searchByKeyword")
    @Operation(summary = "關鍵字查詢文章",description = "關鍵字全文查詢文章")
    public ApiResponse<List<PostDto>> searchByKeyword(@Parameter(description = "關鍵字",example = "關鍵字")@RequestParam(name = "keyword") String keyword) {
        List<PostDto> postDtoList = postService.searchByKeyword(keyword);
        if (postDtoList.isEmpty()) {
            return new ApiResponse<>(false, "查無資料", null, HttpStatus.NO_CONTENT);
        }
        return new ApiResponse<>(true, "查詢成功", postDtoList, HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "查詢文章",description = "查詢文章")
    public ApiResponse<Page<PostDto>> getPostByPage(@Parameter(description = "標題",example = "這是一個標題")@RequestParam(name = "title",required = false) String title,
                                                       @Parameter(description = "作者",example = "Timmy") @RequestParam(name = "authorName",required = false) String authorName,
                                                       @Parameter(description = "頁數") @RequestParam(name = "page", defaultValue = "1",required = false) Integer page,
                                                       @Parameter(description = "大小",example = "10" ) @RequestParam(name = "pageSize",defaultValue = "10",required = false) Integer pageSize) {
        Page<PostDto> posts = postService.findAll(title, authorName, page, pageSize);
        if (posts.isEmpty() || CollectionUtils.isEmpty(posts.getContent())) {
            return new ApiResponse<>(false, "查無資料", posts, HttpStatus.NO_CONTENT);
        }
        return new ApiResponse<>(true, "查詢成功", posts, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/upload")
    @Operation(summary = "上傳圖片API",description = "上傳圖片API")
    public ApiResponse<String> upload(@RequestParam("file") MultipartFile file,
                                      @RequestParam("id") Long id) {
        try {
            postService.upload(file,id);
        } catch (Exception e) {
            return new ApiResponse<>(false, "上傳失敗", null, HttpStatus.BAD_REQUEST);
        }
        return new ApiResponse<>(true, "上傳成功" , HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping(value = "/draft", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "創建草稿API",description = "創建草稿API")
    public ApiResponse<PostDto> createDraft(@Validated @RequestBody PostDto postDto) {
        try {
            postService.createDraft(postDto);
        } catch (Exception e) {
            return new ApiResponse<>(false, "創建失敗", null, HttpStatus.BAD_REQUEST);
        }
        return new ApiResponse<>(true, "創建成功", HttpStatus.OK);
    }


    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "創建發布文章API",description = "創建發布文章API")
    public ApiResponse<PostDto> createPost(@Validated @RequestBody PostDto postDto) {
        try {
            postService.add(postDto);
        } catch (Exception e) {
            return new ApiResponse<>(false, "創建失敗", null, HttpStatus.BAD_REQUEST);
        }
        return new ApiResponse<>(true, "創建成功", HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PutMapping( "/{postId}")
    @Operation(summary = "更新發布文章API",description = "更新發布文章API")
    public ApiResponse<PostDto> updatePost(
            @Parameter(description = "文章id",example = "1") @PathVariable Long postId,
            @Validated @RequestBody PostDto postDto)  {
        try {
            postService.edit(postId,postDto);
        } catch (Exception e) {
            return new ApiResponse<>(false, "更新失敗", null, HttpStatus.BAD_REQUEST);
        }
        return new ApiResponse<>(true, "更新成功", HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "刪除發布文章API",description = "刪除發布文章API")
    public ApiResponse<String> deletePost(@Parameter(description = "刪除文章id",example = "1") @PathVariable Long id) throws ResourceNotFoundException, IOException {
        return new ApiResponse<>(true, "刪除成功", postService.delete(id), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DeleteMapping("/{id}/bookmarks")
    @Operation(summary = "取消收藏文章API",description = "取消收藏文章API")
    public ApiResponse<String> deleteBookmark(@Parameter(description = "取消收藏文章id",example = "1") @PathVariable Long id) throws ResourceNotFoundException {
        try {
            postService.deleteBookmark(id);
        } catch (Exception e) {
            return new ApiResponse<>(false, "取消收藏失敗", null, HttpStatus.BAD_REQUEST);
        }
        return new ApiResponse<>(true, "取消收藏成功", HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/{postId}/like")
    @Operation(summary = "按讚文章API",description = "按讚文章API")
    public ApiResponse<String> addLike(@Parameter(description = "按讚文章id",example = "1") @PathVariable Long postId) throws ResourceNotFoundException {
        try {
            postService.addLike(postId);
        } catch (Exception e) {
            return new ApiResponse<>(false, "按讚失敗", null, HttpStatus.BAD_REQUEST);
        }
        return new ApiResponse<>(true, "按讚成功", HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DeleteMapping("/{postId}/like")
    @Operation(summary = "取消按讚文章API",description = "取消按讚文章API")
    public ApiResponse<String> deleteLike(@Parameter(description = "取消按讚文章id",example = "1") @PathVariable Long postId) throws ResourceNotFoundException {
        try {
            postService.disLike(postId);
        } catch (Exception e) {
            return new ApiResponse<>(false, "取消按讚失敗", null, HttpStatus.BAD_REQUEST);
        }
        return new ApiResponse<>(true, "取消按讚成功", HttpStatus.OK);
    }


    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/bookmarks/{username}")
    @Operation(summary = "收藏文章列表API",description = "收藏文章列表API")
    public ApiResponse<List<PostDto>> getBookmarks(@Parameter(description = "收藏文章列表",example = "1") @PathVariable String username) {
        return new ApiResponse<>(true, "收藏文章列表", postService.getBookmarks(username), HttpStatus.OK);
    }
}
