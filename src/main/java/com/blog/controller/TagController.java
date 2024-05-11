package com.blog.controller;

import com.blog.dto.ApiResponse;
import com.blog.dto.TagDto;
import com.blog.service.TagService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/tags")
public class TagController {

    @Resource
    private TagService tagService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Page<TagDto>> getTags(@Parameter(description = "頁數",example = "1") @RequestParam(name = "page",defaultValue = "1",required = false) Integer page,
                                             @Parameter(description = "每頁筆數",example = "10") @RequestParam(name = "pageSize",defaultValue = "10",required = false) Integer pageSize,
                                             @Parameter(description = "名稱",example = "name") @RequestParam(name = "name",required = false) String name) {
        Page<TagDto> tagDtoPage = tagService.findAll(page, pageSize, name);
        if(tagDtoPage.isEmpty()|| CollectionUtils.isEmpty(tagDtoPage.getContent()))
            return new ApiResponse<>(false, "查無資料", null, HttpStatus.NO_CONTENT);
        return new ApiResponse<>(true, "查詢成功", tagDtoPage, HttpStatus.OK);
    }

    @GetMapping("/findList")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<TagDto>> getTagList() {
        List<TagDto> tagDtoList = tagService.findAll();
        if(CollectionUtils.isEmpty(tagDtoList))
            return new ApiResponse<>(false, "查無資料", null, HttpStatus.NO_CONTENT);
        return new ApiResponse<>(true, "查詢成功", tagDtoList, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<TagDto> getTag(@PathVariable("id") Long id) {
        TagDto tagDto = tagService.findById(id);
        if(ObjectUtils.isEmpty(tagDto))
            return new ApiResponse<>(false, "查無資料", null, HttpStatus.NO_CONTENT);
        return new ApiResponse<>(true, "查詢成功", tagDto, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<TagDto> create(@Validated @RequestBody TagDto tagDto) {
        try {
            tagService.add(tagDto);
        } catch (Exception e) {
            return new ApiResponse<>(false, "新增失敗", null, HttpStatus.BAD_REQUEST);
        }
        return new ApiResponse<>(true, "新增成功", HttpStatus.CREATED);
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<TagDto> update(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "標籤資料", required = true)
            @Validated @RequestBody TagDto tagDto) {
        try {
            tagService.edit(tagDto);
        } catch (Exception e) {
            return new ApiResponse<>(false, "更新失敗", null, HttpStatus.BAD_REQUEST);
        }
        return new ApiResponse<>(true, "更新成功", HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> delete(@PathVariable("id") Long id) {
        return new ApiResponse<>(true, tagService.delete(id),HttpStatus.OK);
    }
}