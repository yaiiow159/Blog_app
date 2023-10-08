package com.blog.controller;

import com.blog.dto.CommentDto;
import com.blog.exception.ResourceNotFoundException;
import com.blog.service.CommentService;
import com.blog.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RequestMapping("/api/v1/")
@RestController
@Slf4j
@Tag(name = "評論相關功能", description = "評論相關功能")
public class CommentController {
    @Resource
    private CommentService commentService;

    @Resource
    private PostService postService;

    @GetMapping("/posts/{postId}/comments/{id}")
    @Operation(summary = "查詢評論",description = "查詢一篇文章底下的評論")
    public ResponseEntity<CommentDto> getComment(@Parameter(description = "文章id",example = "1")@PathVariable Long postId,
                                                 @Parameter(description = "評論id",example = "1")@PathVariable Long id) throws ResourceNotFoundException {
        CommentDto commentDto = commentService.findComment(postId,id);
        if (ObjectUtils.isEmpty(commentDto))
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(commentDto, HttpStatus.OK);
    }

    @GetMapping("/posts/{postId}/comments")
    @Operation(summary = "查詢評論",description = "查詢一篇文章底下的評論")
    public ResponseEntity<Page<CommentDto>> getComments(@Parameter(description = "文章id",example = "1")@PathVariable Long postId,
                                                        @Parameter(description = "頁數",example = "1") @RequestParam(name = "page",defaultValue = "0") int page,
                                                        @Parameter(description = "每頁筆數",example = "10") @RequestParam(name = "size",defaultValue = "10") int size,
                                                        @Parameter(description = "排序",example = "id/name/email") @RequestParam(name = "sort",defaultValue = "id") String sort) throws ResourceNotFoundException {
        Page<CommentDto> commentDtoList = commentService.findAllComments(postId, page, size, sort);
        if (CollectionUtils.isEmpty(commentDtoList.getContent()))
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(commentDtoList, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/create/{postId}")
    @Operation(summary = "創建評論",description = "創建一篇文章底下的評論")
    public ResponseEntity<CommentDto> createComment(@Parameter(description = "文章id",example = "1") @PathVariable Long postId,
                                                    @Parameter(description = "評論內容",example = "評論內容")@RequestBody CommentDto commentDto) throws ResourceNotFoundException {
        CommentDto comment = commentService.createComment(postId, commentDto);
        if (ObjectUtils.isEmpty(comment))
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(comment, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping("/update/{postId}")
    @Operation(summary = "更新評論",description = "更新一篇文章底下的評論")
    public ResponseEntity<CommentDto> updateComment(@Parameter(description = "文章id",example = "1")@PathVariable Long postId,
                                                    @Parameter(description = "評論內容",example = "評論內容") @RequestBody CommentDto commentDto) throws ResourceNotFoundException {
        CommentDto comment = commentService.updateComment(postId, commentDto);
        if (ObjectUtils.isEmpty(comment))
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(comment, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "刪除評論",description = "刪除一篇文章底下的評論")
    public ResponseEntity<String> deleteComment(@Parameter(description = "評論id",example = "1")@PathVariable Long id) throws ResourceNotFoundException {
        return new ResponseEntity<>( commentService.deleteComment(id), HttpStatus.OK);
    }

}
