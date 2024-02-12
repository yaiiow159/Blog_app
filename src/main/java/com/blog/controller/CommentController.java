package com.blog.controller;

import com.blog.dto.CommentDto;
import com.blog.exception.ResourceNotFoundException;
import com.blog.service.CommentService;
import com.blog.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/")
@RestController
@Slf4j
@Tag(name = "評論相關功能", description = "評論相關功能")
public class CommentController {
    @Resource
    private CommentService commentService;

    @GetMapping("/posts/{postId}/comments/{id}")
    @Operation(summary = "查詢評論",description = "查詢一篇文章底下的特定評論")
    public ResponseEntity<CommentDto> getComment(@Parameter(description = "文章id",example = "1")@PathVariable Long postId,
                                                 @Parameter(description = "評論id",example = "1")@PathVariable Long id) throws ResourceNotFoundException {
        CommentDto commentDto = commentService.findComment(postId,id);
        if (ObjectUtils.isEmpty(commentDto))
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok(commentDto);
    }

    @GetMapping("/posts/{postId}/comments")
    @Operation(summary = "查詢評論",description = "查詢一篇文章底下的評論")
    public ResponseEntity<List<CommentDto>> getComments(@Parameter(description = "文章id",example = "1")@PathVariable Long postId) throws ResourceNotFoundException {
        List<CommentDto> commentDtoList = commentService.findAllComments(postId);
        if (CollectionUtils.isEmpty(commentDtoList))
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok(commentDtoList);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping("/posts/{postId}/comments/create")
    @Operation(summary = "創建評論",description = "創建一篇文章底下的評論")
    public ResponseEntity<CommentDto> createComment(@Parameter(description = "文章id",example = "1") @PathVariable Long postId,
                                                    @Parameter(description = "評論內容",example = "評論內容")@Validated @RequestBody CommentDto commentDto) throws ResourceNotFoundException {
        CommentDto comment = commentService.createComment(postId, commentDto);
        if (ObjectUtils.isEmpty(comment))
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok(comment);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PutMapping("/posts/{postId}/comments/update/{id}")
    @Operation(summary = "更新評論",description = "更新一篇文章底下的評論")
    public ResponseEntity<CommentDto> updateComment(@Parameter(description = "文章id",example = "1")@PathVariable Long postId,
                                                    @Parameter(description = "評論id",example = "1")@PathVariable Long id,
                                                    @Parameter(description = "評論內容",example = "評論內容") @Validated @RequestBody CommentDto commentDto) throws ResourceNotFoundException {
        CommentDto comment = commentService.updateComment(postId, id, commentDto);
        if (ObjectUtils.isEmpty(comment))
            ResponseEntity.noContent().build();
        return ResponseEntity.ok(comment);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @DeleteMapping("/posts/{postId}/comments/delete/{id}")
    @Operation(summary = "刪除評論",description = "刪除一篇文章底下的評論")
    public ResponseEntity<String> deleteComment(
            @Parameter(description = "文章id",example = "1")@PathVariable Long postId,
            @Parameter(description = "評論id",example = "1")@PathVariable Long id) throws ResourceNotFoundException {
        return ResponseEntity.ok(commentService.deleteComment(postId, id));
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping("/posts/{postId}/comments/report/{id}")
    @Operation(summary = "檢舉評論",description = "檢舉一篇文章底下的評論")
    public ResponseEntity<String> reportComment(
            @Parameter(description = "文章id",example = "1")@PathVariable Long postId,
            @Parameter(description = "評論id",example = "1")@PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "被檢舉評論")@Validated @RequestBody
            CommentDto commentDto) throws ResourceNotFoundException {
        return ResponseEntity.ok(commentService.reportComment(commentDto));
    }

}
