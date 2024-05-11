package com.blog.controller;

import com.blog.dto.ApiResponse;
import com.blog.dto.CategoryDto;
import com.blog.dto.PostDto;
import com.blog.exception.ResourceNotFoundException;
import com.blog.exception.ValidateFailedException;
import com.blog.service.CategorieService;
import com.blog.service.PostService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@Slf4j
@Tag(name = "分類相關功能", description = "分類相關功能")
public class CategoriesController {
    @Resource
    private CategorieService categorieService;
    @Resource
    private PostService postService;

    @GetMapping
    @Operation(summary = "查詢所有分類",description = "查詢所有分類")
    public ApiResponse<Page<CategoryDto>> findAll(@Parameter(description = "頁數",example = "0") @RequestParam(name = "page",defaultValue = "1",required = false) Integer page,
                                               @Parameter(description = "每頁筆數",example = "10") @RequestParam(name = "pageSize",defaultValue = "10",required = false) Integer pageSize,
                                               @Parameter(description = "名稱",example = "name") @RequestParam(name = "name",required = false) String name) {
        Page<CategoryDto> categoryDtoPage = categorieService.findAll(page,pageSize,name);
        if(categoryDtoPage.isEmpty()|| CollectionUtils.isEmpty(categoryDtoPage.getContent()))
            return new ApiResponse<>(false, "查無資料", categoryDtoPage, HttpStatus.NO_CONTENT);
        return new ApiResponse<>(true, "查詢成功", categoryDtoPage, HttpStatus.OK);
    }
    @GetMapping("/{id}")
    @Operation(summary = "查詢單筆分類",description = "用id查詢分類")
    public ApiResponse<CategoryDto> findById(@Parameter(description = "分類id",example = "1") @PathVariable Long id) {
        CategoryDto categoryDto = categorieService.findById(id);
        if(ObjectUtils.isEmpty(categoryDto))
            return new ApiResponse<>(false, "查無資料", categoryDto, HttpStatus.NO_CONTENT);
        return new ApiResponse<>(true, "查詢成功", categoryDto, HttpStatus.OK);
    }

    @GetMapping("/findList")
    @Operation(summary = "查詢所有分類",description = "查詢所有分類")
    public ApiResponse<List<CategoryDto>> findAllList() {
        List<CategoryDto> categoryDtoList = categorieService.findAll();
        if(CollectionUtils.isEmpty(categoryDtoList))
            return new ApiResponse<>(false, "查無資料", categoryDtoList, HttpStatus.NO_CONTENT);
        return new ApiResponse<>(true, "查詢成功", categoryDtoList, HttpStatus.OK);
    }

    @GetMapping("/{id}/posts/{postId}")
    @Operation(summary = "查詢分類下的單筆文章",description = "查詢分類下的單筆文章")
    public ApiResponse<PostDto> findTheOnePostsByCategory(@Parameter(description = "分類id",example = "1") @PathVariable Long id,
                                                             @Parameter(description = "文章id",example = "1") @PathVariable Long postId) throws ResourceNotFoundException {
        PostDto postDto = postService.findPostByCategoryId(id, postId);
        if(ObjectUtils.isEmpty(postDto))
            return new ApiResponse<>(false, "查無資料", postDto, HttpStatus.NO_CONTENT);
        return new ApiResponse<>(true, "查詢成功", postDto, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping()
    @Operation(summary = "創建分類",description = "創建分類物件")
    public ApiResponse<CategoryDto> createCategory(@Parameter(description = "分類內容") @Validated @RequestBody CategoryDto categoryDto) {
        try {
            categorieService.add(categoryDto);
        } catch (Exception e) {
            return new ApiResponse<>(false, "創建失敗", null, HttpStatus.BAD_REQUEST);
        }
        return new ApiResponse<>(true, "創建成功", HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping
    @Operation(summary = "更新分類",description = "更新分類物件")
    public ApiResponse<CategoryDto> updateCategory(
            @Parameter(description = "分類內容") @Validated @RequestBody CategoryDto categoryDto) throws ValidateFailedException {
        try {
            categorieService.edit(categoryDto);
        }  catch (Exception e) {
            return new ApiResponse<>(false, "更新失敗", null, HttpStatus.BAD_REQUEST);
        }
        return new ApiResponse<>(true, "更新成功", HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("{id}")
    @Operation(summary = "刪除分類",description = "用id刪除分類")
    public ApiResponse<String> deleteCategory(@Parameter(description = "分類id",example = "1") @PathVariable Long id) throws ResourceNotFoundException, ValidateFailedException {
        return new ApiResponse<>(true, categorieService.delete(id),HttpStatus.OK);
    }
}
