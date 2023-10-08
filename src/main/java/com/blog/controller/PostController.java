package com.blog.controller;

import com.blog.dto.PostDto;
import com.blog.exception.ResourceNotFoundException;
import com.blog.service.PostService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Tag(name = "文章相關功能", description = "文章相關功能")
@Slf4j
@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    @Resource
    private PostService postService;

    @GetMapping
    @Operation(summary = "查詢所有文章",description = "查詢所有文章")
    public ResponseEntity<List<PostDto>> getAllPost() {
        return new ResponseEntity<>(postService.getAllPosts(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "查詢文章",description = "查詢文章")
    public ResponseEntity<PostDto> getPost(@Parameter(description = "發布文章id",example = "1") @PathVariable long id) {
        return new ResponseEntity<>(postService.getOnePost(id), HttpStatus.OK);
    }

    @GetMapping("/searchBySpec")
    @Operation(summary = "查詢文章",description = "查詢文章")
    public ResponseEntity<Page<PostDto>> getPostByPage(@Parameter(description = "標題",example = "這是一個標題")@RequestParam(name = "title") String title,
                                                       @Parameter(description = "作者",example = "Timmy") @RequestParam(name = "content",required = false) String content,
                                                       @Parameter(description = "描述",example = "這是一個描述" ) @RequestParam(name = "描述",required = false) String description,
                                                       @Parameter(description = "頁數") @RequestParam(name = "page", defaultValue = "0",required = false) int page,
                                                       @Parameter(description = "大小",example = "10" ) @RequestParam(name = "size",defaultValue = "10",required = false ) int size,
                                                       @Parameter(description = "排序",example = "id") @RequestParam(name = "sort",defaultValue = "id",required = false) String sort){
        return new ResponseEntity<>(postService.getAllPosts(title, content, description ,page, size, sort), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/categories/{categoryId}/create")
    @Operation(summary = "創建發布文章API",description = "創建發布文章API")
    public ResponseEntity<PostDto> createPost(@Parameter(description = "分類id",example = "1") @PathVariable long categoryId,
                                              @Parameter(description = "發布文章") @Validated @RequestBody PostDto postDto) throws ResourceNotFoundException {
        return new ResponseEntity<>(postService.createPost(categoryId,postDto), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping("/categories/{categoryId}/update")
    @Operation(summary = "更新發布文章API",description = "更新發布文章API")
    public ResponseEntity<PostDto> updatePost(@Parameter(description = "分類id",example = "1") @PathVariable long categoryId,
                                              @Parameter(description = "發布文章") @Validated @RequestBody PostDto postDto) throws ResourceNotFoundException {
        return new ResponseEntity<>(postService.updatePost(categoryId,postDto), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping("/categories/delete/{id}")
    @Operation(summary = "刪除發布文章API",description = "刪除發布文章API")
    public ResponseEntity<String> deletePost(@Parameter(description = "刪除文章id",example = "1") @PathVariable long id) throws ResourceNotFoundException {
        return new ResponseEntity<>(postService.deletePost(id), HttpStatus.OK);
    }
}
