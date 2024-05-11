package com.blog.controller;

import com.blog.dto.ApiResponse;
import com.blog.dto.RecentViewDto;
import com.blog.exception.ResourceNotFoundException;
import com.blog.service.RecentViewService;
import com.blog.vo.PostVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "瀏覽紀錄", description = "瀏覽紀錄相關API")
@RestController
@RequestMapping("/api/v1/recentViews")
@RequiredArgsConstructor
public class RecentViewController {

    private final RecentViewService recentViewService;

    @GetMapping
    @Operation(summary = "查詢瀏覽紀錄", description = "查詢近期瀏覽紀錄 (可用日期時間查詢)", tags = {"瀏覽紀錄"})
    public ApiResponse<Page<PostVo>> getRecentView (
            @Parameter(description = "頁數", example = "0") @RequestParam(name = "page", defaultValue = "1") Integer page,
            @Parameter(description = "每頁筆數", example = "10") @RequestParam(name = "size", defaultValue = "10") Integer size,
            @Parameter(description = "使用者名稱", example = "timmy") @RequestParam(name = "username", required = false) String username,
            @Parameter(description = "文章id", example = "1") @RequestParam(name = "postId", required = false) Long postId) {

        Page<PostVo> postVos = recentViewService.getRecentView(postId,username,page, size);
        if (CollectionUtils.isEmpty(postVos.getContent()))
            return new ApiResponse<>(false, "查無資料", null, HttpStatus.NO_CONTENT);
        return new ApiResponse<>(true, "查詢成功", postVos, HttpStatus.OK);
    }

    @Operation(summary = "新增最近瀏覽紀錄", description = "新增最近瀏覽紀錄", tags = {"瀏覽紀錄"})
    @PostMapping
    public ApiResponse<String> createRecentView (@Validated @RequestBody RecentViewDto recentViewDto) throws ResourceNotFoundException {
        try {
            recentViewService.createRecentView(recentViewDto);
        } catch (Exception e) {
            return new ApiResponse<>(false, e.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
        return new ApiResponse<>(true, "新增成功", null, HttpStatus.CREATED);
    }
}
