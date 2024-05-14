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
                                                    @Parameter(description = "評論內容",example = "評論內容")@Validated @RequestBody CommentDto commentDto){
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
                                                 @Parameter(description = "評論內容",example = "評論內容")@Validated @RequestBody CommentDto commentDto){
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
            @Parameter(description = "評論id",example = "1")@PathVariable Long id){
        try {
            commentService.delete(postId, id);
        } catch (Exception e) {
            return new ApiResponse<>(false, "刪除失敗", HttpStatus.BAD_REQUEST);
        }
        return new ApiResponse<>(true, "刪除成功",HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/posts/{postId}/comments/report/{id}")
    @Operation(summary = "檢舉評論",description = "檢舉一篇文章底下的評論")
    public ApiResponse<String> reportComment(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "被檢舉評論")@Validated @RequestBody
            CommentDto commentDto) {
        try {
            commentService.reportComment(commentDto);
        } catch (Exception e) {
            return new ApiResponse<>(false, "檢舉失敗", HttpStatus.BAD_REQUEST);
        }
        return new ApiResponse<>(true, "檢舉成功",HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/posts/{postId}/comments/{id}/likes")
    @Operation(summary = "按讚評論",description = "按讚一篇文章底下的評論")
    public ApiResponse<String> likeComment(
            @Parameter(description = "文章id",example = "1")@PathVariable Long postId,
            @Parameter(description = "評論id",example = "1")@PathVariable Long id){
        try {
            commentService.likeComment(postId, id);
        } catch (Exception e) {
            return new ApiResponse<>(false, "按讚失敗", HttpStatus.BAD_REQUEST);
        }
        return new ApiResponse<>(true, "按讚成功",HttpStatus.OK);
    }

    @DeleteMapping("/posts/{postId}/comments/{id}/likes")
    @Operation(summary = "取消按讚評論",description = "取消按讚一篇文章底下的評論")
    public ApiResponse<String> cancelLikeComment(
            @Parameter(description = "文章id",example = "1")@PathVariable Long postId,
            @Parameter(description = "評論id",example = "1")@PathVariable Long id){
        try {
            commentService.cancelLikeComment(postId, id);
        } catch (Exception e) {
            return new ApiResponse<>(false, "取消按讚失敗", HttpStatus.BAD_REQUEST);
        }
        return new ApiResponse<>(true, "取消按讚成功",HttpStatus.OK);
    }

    // 查詢按讚數
    @GetMapping("/posts/{postId}/comments/{id}/likeCount")
    @Operation(summary = "查詢評論按讚數",description = "查詢評論按讚數")
    public ApiResponse<Integer> likeCountComment(
            @Parameter(description = "文章id",example = "1")@PathVariable Long postId,
            @Parameter(description = "評論id",example = "1")@PathVariable Long id){
        try {
            return new ApiResponse<>(true, "查詢成功",commentService.findLikeCount(postId, id),HttpStatus.OK);
        } catch (Exception e) {
            return new ApiResponse<>(false, "查詢失敗",HttpStatus.BAD_REQUEST);
        }
    }

}
