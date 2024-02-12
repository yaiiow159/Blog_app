package com.blog.controller;

import com.blog.dto.PostDto;
import com.blog.dto.PostHistoryPoDto;
import com.blog.enumClass.PostStatus;
import com.blog.exception.ResourceNotFoundException;
import com.blog.service.PostHistoryService;
import com.blog.service.PostService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Tag(name = "文章相關功能", description = "文章相關功能")
@Slf4j
@RestController
@RequestMapping("/api/v1/posts")
public class PostController {
    @Resource
    private PostService postService;
    @Resource
    private PostHistoryService postHistoryService;

    @GetMapping
    @Operation(summary = "查詢所有文章",description = "查詢所有文章")
    public ResponseEntity<List<PostDto>> getAllPost() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @GetMapping("/history")
    @Operation(summary = "查詢歷史文章",description = "查詢歷史文章")
    public ResponseEntity<Page<PostHistoryPoDto>> getHistoryPost(@Parameter(description = "查詢開始時間",example = "2022-01-01 00:00:00") @RequestParam(name = "startTime", required = false) String startTime,
                                                                 @Parameter(description = "查詢結束時間",example = "2022-01-01 00:00:00") @RequestParam(name = "endTime", required = false) String endTime,
                                                                 @Parameter(description = "頁數",example = "1") @RequestParam(name = "page",defaultValue = "1",required = false) int page,
                                                                 @Parameter(description = "每頁筆數",example = "10") @RequestParam(name = "size",defaultValue = "10",required = false) int size,
                                                                 @Parameter(description = "排序",example = "id") @RequestParam(name = "sort",defaultValue = "id" ,required = false) String sort,
                                                                 @Parameter(description = "排序方向",example = "asc/desc") @RequestParam(name = "direction",defaultValue = "asc",required = false) String direction,
                                                                 @Parameter(description = "文章標題",example = "文章標題") @RequestParam(name = "title", required = false) String title,
                                                                 @Parameter(description = "文章作者",example = "作者") @RequestParam(name = "authorName", required = false) String authorName) throws ResourceNotFoundException {
        return new ResponseEntity<>(postHistoryService.getHistoryPosts(title, authorName, startTime, endTime, page, size, sort, direction), HttpStatus.OK);
    }

    @GetMapping("/latest")
    @Operation(summary = "查詢最新文章",description = "查詢最新文章")
    public ResponseEntity<List<PostDto>> getLatestPost(){
        return ResponseEntity.ok(postService.getLatestPost());
    }

    @GetMapping("/popular")
    @Operation(summary = "查詢熱門文章",description = "查詢熱門文章")
    public ResponseEntity<List<PostDto>> getHotPost(){
        return ResponseEntity.ok(postService.getHotPost());
    }
    @GetMapping("/{id}")
    @Operation(summary = "查詢文章",description = "利用id查詢文章")
    public ResponseEntity<PostDto> getPost(@Parameter(description = "文章id",example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(postService.getOnePost(id));
    }

    @GetMapping("/searchByKeyword")
    @Operation(summary = "關鍵字查詢文章",description = "關鍵字全文查詢文章")
    public ResponseEntity<List<PostDto>> searchByKeyword(@Parameter(description = "關鍵字",example = "關鍵字")@RequestParam(name = "keyword") String keyword) {
        List<PostDto> postDtoList = postService.searchByKeyword(keyword);
        if (postDtoList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(postDtoList);
    }

    @GetMapping("/searchBySpec")
    @Operation(summary = "查詢文章",description = "查詢文章")
    public ResponseEntity<Page<PostDto>> getPostByPage(@Parameter(description = "標題",example = "這是一個標題")@RequestParam(name = "title",required = false) String title,
                                                       @Parameter(description = "作者",example = "Timmy") @RequestParam(name = "content",required = false) String content,
                                                       @Parameter(description = "作者",example = "Timmy") @RequestParam(name = "authorName",required = false) String authorName,
                                                       @Parameter(description = "頁數") @RequestParam(name = "page", defaultValue = "1",required = false) int page,
                                                       @Parameter(description = "大小",example = "10" ) @RequestParam(name = "size",defaultValue = "10",required = false ) int size,
                                                       @Parameter(description = "排序",example = "id") @RequestParam(name = "sort",defaultValue = "id",required = false) String sort,
                                                       @Parameter(description = "排序方向",example = "desc/asc") @RequestParam(name = "direction",required = false) String direction) {
        return ResponseEntity.ok(postService.getAllPosts(title, content, authorName, page, size, sort, direction));
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping(value = "/createDraft", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "創建草稿API",description = "創建草稿API")
    public ResponseEntity<PostDto> createDraft(
                    @Parameter(description = "圖片",example = "圖片") @RequestPart(required = false) MultipartFile multipartFile,
                    @Parameter(description = "標題",example = "標題") @RequestPart String title,
                    @Parameter(description = "內容",example = "內容") @RequestPart(required = false) String content,
                    @Parameter(description = "說明",example = "說明") @RequestPart(required = false) String description,
                    @Parameter(description = "狀態",example = "狀態") @RequestPart String authorName,
                    @Parameter(description = "信箱",example = "信箱") @RequestPart(required = false) String authorEmail,
                    @Parameter(description = "分類id",example = "1") @PathVariable Long categoryId) {
        PostDto postDto = new PostDto();
        postDto.setCategoryId(String.valueOf(categoryId));
        postDto.setTitle(title);
        postDto.setContent(content);
        postDto.setDescription(description);
        postDto.setAuthorName(authorName);
        postDto.setAuthorEmail(authorEmail);
        postDto.setMultipartFile(multipartFile);
        postDto.setStatus("草稿");
        return ResponseEntity.ok(postService.createDraft(postDto));

    }


    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping(value = "/categories/{categoryId}/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "創建發布文章API",description = "創建發布文章API")
    public ResponseEntity<PostDto> createPost(
                    @Parameter(description = "分類id",example = "1") @PathVariable Long categoryId,
                    @Parameter(description = "圖片",example = "圖片") @RequestPart(required = false) MultipartFile multipartFile,
                    @Parameter(description = "標題",example = "標題") @RequestPart String title,
                    @Parameter(description = "內容",example = "內容") @RequestPart(required = false) String content,
                    @Parameter(description = "說明",example = "說明") @RequestPart(required = false) String description,
                    @Parameter(description = "狀態",example = "狀態") @RequestPart String authorName,
                    @Parameter(description = "信箱",example = "信箱") @RequestPart String authorEmail) throws ResourceNotFoundException, IOException, ExecutionException, InterruptedException {
        PostDto postDto = new PostDto();
        if(multipartFile != null){
            postDto.setMultipartFile(multipartFile);
        }
        postDto.setTitle(title);
        postDto.setContent(content);
        postDto.setDescription(description);
        postDto.setAuthorEmail(authorEmail);
        postDto.setAuthorName(authorName);
        return ResponseEntity.ok(postService.createPost(categoryId, postDto));
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping(value = "/categories/{categoryId}/update/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "更新發布文章API",description = "更新發布文章API")
    public ResponseEntity<PostDto> updatePost(@Parameter(description = "分類id",example = "1") @PathVariable Long categoryId,
                                              @Parameter(description = "文章id",example = "1") @PathVariable Long postId,
                                              @Parameter(description = "圖片",example = "圖片") @RequestPart(required = false) MultipartFile multipartFile,
                                              @Parameter(description = "標題",example = "標題") @RequestPart String title,
                                              @Parameter(description = "內容",example = "內容") @RequestPart(required = false) String content,
                                              @Parameter(description = "說明",example = "說明") @RequestPart(required = false) String description,
                                              @Parameter(description = "狀態",example = "狀態") @RequestPart(required = false) String authorName,
                                              @Parameter(description = "分類名稱",example = "分類名稱") @RequestPart String authorEmail) throws ResourceNotFoundException, IOException, ExecutionException, InterruptedException {
        PostDto postDto = new PostDto();
        postDto.setTitle(title);
        postDto.setContent(content);
        postDto.setDescription(description);
        postDto.setAuthorName(authorName);
        postDto.setAuthorEmail(authorEmail);
        if (multipartFile != null) {
            postDto.setMultipartFile(multipartFile);
        }
        return ResponseEntity.ok(postService.updatePost(categoryId, postId, postDto));
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @DeleteMapping("/categories/delete/{id}")
    @Operation(summary = "刪除發布文章API",description = "刪除發布文章API")
    public ResponseEntity<String> deletePost(@Parameter(description = "刪除文章id",example = "1") @PathVariable Long id) throws ResourceNotFoundException {
        return ResponseEntity.ok(postService.deletePost(id));
    }
}
