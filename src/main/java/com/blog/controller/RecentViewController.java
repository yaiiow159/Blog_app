package com.blog.controller;

import com.blog.dto.RecentViewPoDto;
import com.blog.exception.ResourceNotFoundException;
import com.blog.service.RecentViewService;
import com.blog.vo.PostVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "瀏覽紀錄", description = "瀏覽紀錄相關API")
@RestController
@RequestMapping("/api/v1/recentView")
@RequiredArgsConstructor
public class RecentViewController {

    private final RecentViewService recentViewService;

    @GetMapping
    @Operation(summary = "查詢瀏覽紀錄", description = "查詢近期瀏覽紀錄 (可用日期時間查詢)", tags = {"瀏覽紀錄"})
    public ResponseEntity<Page<PostVo>> getRecentView(
            @Parameter(description = "頁數", example = "0") @RequestParam(name = "page", defaultValue = "1") Integer page,
            @Parameter(description = "每頁筆數", example = "10") @RequestParam(name = "size", defaultValue = "10") Integer size,
            @Parameter(description = "排序", example = "id") @RequestParam(name = "sort", defaultValue = "id") String sort,
            @Parameter(description = "排序方向", example = "asc") @RequestParam(name = "direction", defaultValue = "asc") String direction,
            @Parameter(description = "查詢日期時間", example = "2022-01-01 00:00:00") @RequestParam(name = "dateTime", required = false) String dateTime,
            @Parameter(description = "使用者名稱", example = "timmy") @RequestParam(name = "username", required = false) String username,
            @Parameter(description = "文章id", example = "1") @RequestParam(name = "postId", required = false) Long postId) {
         List<PostVo> postDtoList = recentViewService.getRecentView(dateTime, postId, username, page, size, sort, direction).getContent();
         if(CollectionUtils.isEmpty(postDtoList)){
             return ResponseEntity.noContent().build();
         }
         return ResponseEntity.ok(recentViewService.getRecentView(dateTime, postId, username, page, size, sort, direction));
    }

    @Operation(summary = "新增最近瀏覽紀錄", description = "新增最近瀏覽紀錄", tags = {"瀏覽紀錄"})
    @PostMapping("/create")
    public ResponseEntity<String> createRecentView (@Validated @RequestBody RecentViewPoDto recentViewPoDto) throws ResourceNotFoundException {
        return ResponseEntity.ok(recentViewService.createRecentView(recentViewPoDto));
    }
}
