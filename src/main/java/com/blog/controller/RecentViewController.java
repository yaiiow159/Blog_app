package com.blog.controller;

import com.blog.dto.RecentViewDto;
import com.blog.response.ResponseBody;
import com.blog.service.RecentViewService;
import com.blog.vo.PostVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
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
    public ResponseBody<Page<PostVo>> getRecentView (
            @Parameter(description = "頁數", example = "0") @RequestParam(name = "page", defaultValue = "1") Integer page,
            @Parameter(description = "每頁筆數", example = "10") @RequestParam(name = "size", defaultValue = "10") Integer size,
            @Parameter(description = "使用者名稱", example = "Timmy") @RequestParam(name = "username", required = false) String username,
            @Parameter(description = "作者名稱", example = "Timmy") @RequestParam(name = "authorName", required = false) String authorName,
            @Parameter(description = "作者郵箱", example = "Timmy@qq.com") @RequestParam(name = "authorEmail", required = false) String authorEmail,
            @Parameter(description = "文章名稱", example = "文章名稱") @RequestParam(name = "title", required = false) String title) {
        try {
            Page<PostVo> postVos = recentViewService.getRecentView(username,authorName, authorEmail, title, page, size);
            return new ResponseBody<>(true, "查詢成功", postVos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseBody<>(false, "查無資料" + e.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "取得最近瀏覽紀錄", description = "取得最近瀏覽紀錄", tags = {"瀏覽紀錄"})
    public ResponseBody<PostVo> getRecentView(@PathVariable(name = "id") Long id) {
        try {
            PostVo postVo = recentViewService.getRecentViewById(id);
            return new ResponseBody<>(true, "查詢成功", postVo, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseBody<>(false, "查無資料" + e.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "新增最近瀏覽紀錄", description = "新增最近瀏覽紀錄", tags = {"瀏覽紀錄"})
    @PostMapping
    public ResponseBody<String> createRecentView (@RequestBody @Validated RecentViewDto recentViewDto){
        try {
            recentViewService.createRecentView(recentViewDto);
        } catch (Exception e) {
            return new ResponseBody<>(false, "新增瀏覽紀錄失敗" + e.getMessage(), null, HttpStatus.BAD_REQUEST);
            }
        return new ResponseBody<>(true, "新增成功", null, HttpStatus.CREATED);
    }
}
