package com.blog.controller;

import com.blog.dto.ApiResponse;
import com.blog.dto.CommentDto;
import com.blog.exception.ResourceNotFoundException;
import com.blog.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    public ApiResponse<CommentDto> getComment(@Parameter(description = "文章id",example = "1")@PathVariable Long postId,
                                                  @Parameter(description = "評論id",example = "1")@PathVariable Long id) throws ResourceNotFoundException {
        CommentDto commentDto = commentService.findComment(postId,id);
        if (ObjectUtils.isEmpty(commentDto))
            return new ApiResponse<>(false, "查無資料", commentDto, HttpStatus.NO_CONTENT);
        return new ApiResponse<>(true, "查詢成功", commentDto, HttpStatus.OK);
    }

    @GetMapping("/posts/{postId}/comments")
    @Operation(summary = "查詢評論",description = "查詢一篇文章底下的評論")
    public ApiResponse<List<CommentDto>> getComments(@Parameter(description = "文章id",example = "1")@PathVariable Long postId) throws ResourceNotFoundException {
        List<CommentDto> commentDtoList = commentService.findAllComments(postId);
        if (CollectionUtils.isEmpty(commentDtoList))
            return new ApiResponse<>(false, "查無資料", commentDtoList, HttpStatus.NO_CONTENT);
        return new ApiResponse<>(true, "查詢成功", commentDtoList, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/posts/{postId}/comments")
    @Operation(summary = "創建評論",description = "創建一篇文章底下的評論")
    public ApiResponse<CommentDto> createComment(@Parameter(description = "文章id",example = "1") @PathVariable Long postId,
                                                    @Parameter(description = "評論內容",example = "評論內容")@Validated @RequestBody CommentDto commentDto) throws ResourceNotFoundException {
        try {
            commentService.add(postId, commentDto);
        } catch (Exception e) {
            return new ApiResponse<>(false, "創建失敗", HttpStatus.BAD_REQUEST);
        }
        return new ApiResponse<>(true, "創建成功", HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PutMapping("/posts/{postId}/comments/{id}")
    @Operation(summary = "更新評論",description = "更新一篇文章底下的評論")
    public ApiResponse<CommentDto> updateComment(@Parameter(description = "文章id",example = "1")@PathVariable Long postId,
                                                 @Parameter(description = "評論id",example = "1")@PathVariable Long id,
                                                 @Parameter(description = "評論內容",example = "評論內容")@Validated @RequestBody CommentDto commentDto) throws ResourceNotFoundException {
        try {
            commentService.edit(postId, id, commentDto);
        } catch (Exception e) {
            return new ApiResponse<>(false, "更新失敗", HttpStatus.BAD_REQUEST);
        }
        return new ApiResponse<>(true, "更新成功", HttpStatus.OK);
    }
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DeleteMapping("/posts/{postId}/comments/{id}")
    @Operation(summary = "刪除評論",description = "刪除一篇文章底下的評論")
    public ApiResponse<String> deleteComment(
            @Parameter(description = "文章id",example = "1")@PathVariable Long postId,
            @Parameter(description = "評論id",example = "1")@PathVariable Long id) throws ResourceNotFoundException {
        commentService.delete(postId, id);
        return new ApiResponse<>(true, "刪除成功",HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/posts/{postId}/comments/report/{id}")
    @Operation(summary = "檢舉評論",description = "檢舉一篇文章底下的評論")
    public ApiResponse<String> reportComment(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "被檢舉評論")@Validated @RequestBody
            CommentDto commentDto) throws ResourceNotFoundException {
        commentService.reportComment(commentDto);
        return new ApiResponse<>(true, "檢舉成功",HttpStatus.OK);
    }

}
