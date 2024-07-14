package com.blog.controller;

import com.blog.annotation.NoResubmit;
import com.blog.dto.TagDto;
import com.blog.response.ResponseBody;
import com.blog.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping
    @Operation(summary = "查詢標籤",description = "查詢標籤")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseBody<Page<TagDto>> getTags(@Parameter(description = "頁數",example = "1") @RequestParam(name = "page",defaultValue = "1",required = false) Integer page,
                                              @Parameter(description = "每頁筆數",example = "10") @RequestParam(name = "pageSize",defaultValue = "10",required = false) Integer pageSize,
                                              @Parameter(description = "名稱",example = "name") @RequestParam(name = "name",required = false) String name) {
        try {
            Page<TagDto> tags = tagService.findAll(page, pageSize, name);
            return new ResponseBody<>(true, "查詢成功", tags, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseBody<>(false, "搜尋標籤時遭遇異常 原因: " + e.getMessage(), null, HttpStatus.NO_CONTENT);
        }
    }

    @GetMapping("/findHotTag")
    @Operation(summary = "查詢所有熱門標籤",description = "查詢所有熱門標籤")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseBody<List<TagDto>> getTagList() {
        try {
            List<TagDto> hotTags = tagService.findHotTags();
            return new ResponseBody<>(true, "查詢成功", hotTags, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseBody<>(false, "搜尋熱門標籤時遭遇異常 原因: " + e.getMessage(), null, HttpStatus.NO_CONTENT);
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "查詢標籤",description = "利用id查詢標籤")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseBody<TagDto> getTag(@PathVariable("id") Long id) {
        try {
            TagDto tag = tagService.findById(id);
            return new ResponseBody<>(true, "查詢成功", tag, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseBody<>(false, "搜尋標籤時遭遇異常 原因: " + e.getMessage(), null, HttpStatus.NO_CONTENT);
        }
    }

    @NoResubmit(delaySecond = 3)
    @PostMapping
    @Operation(summary = "新增標籤",description = "新增標籤")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseBody<TagDto> create(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "標籤資料", required = true)
                                       @RequestBody @Validated TagDto tagDto) {
        try {
            tagService.save(tagDto);
        } catch (Exception e) {
            return new ResponseBody<>(false, "新增失敗 原因: " + e.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseBody<>(true, "新增成功", HttpStatus.OK);
    }

    @NoResubmit(delaySecond = 3)
    @PutMapping
    @Operation(summary = "更新標籤",description = "更新標籤")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseBody<TagDto> update(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "標籤資料", required = true)
            @RequestBody @Validated TagDto tagDto) {
        try {
            tagService.update(tagDto);
        } catch (Exception e) {
            return new ResponseBody<>(false, "更新失敗 原因: " + e.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseBody<>(true, "更新成功", HttpStatus.OK);
    }

    @NoResubmit(delaySecond = 3)
    @DeleteMapping("/{id}")
    @Operation(summary = "刪除標籤",description = "刪除標籤")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseBody<String> delete(@PathVariable("id") Long id) {
        try {
            tagService.delete(id);
        } catch (Exception e) {
            return new ResponseBody<>(false, "刪除失敗 原因: " + e.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseBody<>(true, "刪除成功", HttpStatus.OK);
    }
}