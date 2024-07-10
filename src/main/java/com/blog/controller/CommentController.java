package com.blog.controller;

import com.blog.annotation.NoResubmit;
import com.blog.response.ResponseBody;
import com.blog.dto.CommentDto;
import com.blog.exception.ResourceNotFoundException;
import com.blog.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
@RestController
@Tag(name = "評論相關功能", description = "評論相關功能")
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/{id}")
    @Operation(summary = "查詢評論",description = "查詢一篇文章底下的特定評論")
    public ResponseBody<CommentDto> getComment(@Parameter(description = "評論id",example = "1")@PathVariable Long id){
        CommentDto commentDto;
        try {
            commentDto = commentService.findById(id);
        } catch (Exception e) {
            return new ResponseBody<>(false, "查詢時出錯誤, 錯誤原因" + e.getMessage(), null, HttpStatus.NO_CONTENT);
        }
        return new ResponseBody<>(true, "查詢成功", commentDto, HttpStatus.OK);
    }

    @GetMapping("/post/{postId}")
    @Operation(summary = "查詢評論",description = "查詢一篇評論")
    public ResponseBody<List<CommentDto>> getComments(@Parameter(description = "文章id",example = "1")@PathVariable Long postId){
        List<CommentDto> commentDtoList;
        try {
            commentDtoList = commentService.findAll(postId);
        } catch (Exception e) {
            return new ResponseBody<>(false, "查詢時出錯誤, 錯誤原因" + e.getMessage(), null, HttpStatus.NO_CONTENT);
        }
        return new ResponseBody<>(true, "查詢成功", commentDtoList, HttpStatus.OK);
    }

    @NoResubmit(delaySecond = 3)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "創建評論",description = "創建一篇文章底下的評論")
    public ResponseBody<CommentDto> createComment (@Parameter(description = "評論內容",example = "評論內容")@RequestBody @Validated CommentDto commentDto){
        try {
            commentService.save(commentDto);
        } catch (Exception e) {
            return new ResponseBody<>(false, "創建失敗, 失敗原因為: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseBody<>(true, "創建成功", HttpStatus.OK);
    }

    @NoResubmit(delaySecond = 3)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PutMapping
    @Operation(summary = "更新評論",description = "更新一篇文章底下的評論")
    public ResponseBody<CommentDto> updateComment(@Parameter(description = "評論內容",example = "評論內容")@RequestBody @Validated CommentDto commentDto){
        try {
            commentService.update(commentDto);
        } catch (Exception e) {
            return new ResponseBody<>(false, "更新失敗, 失敗原因為: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseBody<>(true, "更新成功", HttpStatus.OK);
    }
    @NoResubmit(delaySecond = 3)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "刪除評論",description = "刪除一篇文章底下的評論")
    public ResponseBody<String> deleteComment(@Parameter(description = "評論id",example = "1")@PathVariable Long id){
        try {
            commentService.delete(id);
        } catch (Exception e) {
            return new ResponseBody<>(false, "刪除失敗, 失敗原因為: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseBody<>(true, "刪除成功",HttpStatus.OK);
    }

    @NoResubmit(delaySecond = 3)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/report")
    @Operation(summary = "檢舉評論",description = "檢舉一篇文章底下的評論")
    public ResponseBody<String> reportComment(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "被檢舉評論") @RequestBody
            @Validated CommentDto commentDto) {
        try {
            commentService.report(commentDto);
        } catch (Exception e) {
            return new ResponseBody<>(false, "檢舉失敗, 失敗原因為: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseBody<>(true, "檢舉成功",HttpStatus.OK);
    }

    @NoResubmit(delaySecond = 3)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/{id}/like")
    @Operation(summary = "按讚評論",description = "按讚一篇文章底下的評論")
    public ResponseBody<String> likeComment(@Parameter(description = "評論id",example = "1")@PathVariable Long id){
        try {
            commentService.like(id);
        } catch (Exception e) {
            return new ResponseBody<>(false, "按讚失敗, 失敗原因為: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseBody<>(true, "按讚成功",HttpStatus.OK);
    }

    @NoResubmit(delaySecond = 3)
    @DeleteMapping("/{id}/like")
    @Operation(summary = "取消按讚評論",description = "取消按讚一篇文章底下的評論")
    public ResponseBody<String> cancelLikeComment(@Parameter(description = "評論id",example = "1")@PathVariable Long id){
        try {
            commentService.cancelLike(id);
        } catch (Exception e) {
            return new ResponseBody<>(false, "取消按讚失敗", HttpStatus.BAD_REQUEST);
        }
        return new ResponseBody<>(true, "取消按讚成功",HttpStatus.OK);
    }

    // 查詢按讚數
    @GetMapping("/{id}/likeCount")
    @Operation(summary = "查詢評論按讚數",description = "查詢評論按讚數")
    public ResponseBody<Integer> likeCountComment(@Parameter(description = "評論id",example = "1")@PathVariable Long id){
        try {
            Integer likeCount = commentService.queryLikeCount(id);
            return new ResponseBody<>(true, "查詢成功",likeCount,HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseBody<>(false, "查詢失敗, 失敗原因為: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
