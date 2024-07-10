package com.blog.controller;

import com.blog.annotation.NoResubmit;
import com.blog.response.ResponseBody;
import com.blog.dto.CategoryDto;

import com.blog.service.CategoriesService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@Slf4j
@Tag(name = "分類相關功能", description = "分類相關功能")
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoriesController {
    private final CategoriesService categoriesService;

    @GetMapping
    @Operation(summary = "查詢所有分類",description = "查詢所有分類")
    public ResponseBody<Page<CategoryDto>> findAll(@Parameter(description = "頁數",example = "0") @RequestParam(name = "page",defaultValue = "1",required = false) Integer page,
                                                   @Parameter(description = "每頁筆數",example = "10") @RequestParam(name = "pageSize",defaultValue = "10",required = false) Integer pageSize,
                                                   @Parameter(description = "名稱",example = "name") @RequestParam(name = "name",required = false) String name) {
        Page<CategoryDto> categoryDtoPage;
        try {
            categoryDtoPage = categoriesService.findAll(page,pageSize,name);
        } catch (Exception e) {
            return new ResponseBody<>(false, "查詢失敗, 失敗原因: " + e.getMessage(), HttpStatus.NO_CONTENT);
        }
        return new ResponseBody<>(true, "查詢成功", categoryDtoPage, HttpStatus.OK);
    }
    @GetMapping("/{id}")
    @Operation(summary = "查詢單筆分類",description = "用id查詢分類")
    public ResponseBody<CategoryDto> findById(@Parameter(description = "分類id",example = "1") @PathVariable Long id) {
        CategoryDto categoryDto;
        try {
            categoryDto = categoriesService.findById(id);
        } catch (Exception e) {
            return new ResponseBody<>(false, "查詢失敗, 失敗原因: " + e.getMessage(), HttpStatus.NO_CONTENT);
        }
        return new ResponseBody<>(true, "查詢成功", categoryDto, HttpStatus.OK);
    }

    @GetMapping("/findList")
    @Operation(summary = "查詢所有分類",description = "查詢所有分類")
    public ResponseBody<List<CategoryDto>> findAllList() {
        List<CategoryDto> categoryDtoList;
        try {
            categoryDtoList = categoriesService.findAll();
        } catch (Exception e) {
            return new ResponseBody<>(false, "查詢失敗, 失敗原因: " + e.getMessage(),HttpStatus.NO_CONTENT);
        }
        return new ResponseBody<>(true, "查詢成功", categoryDtoList, HttpStatus.OK);
    }
    @NoResubmit(delaySecond = 3)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping()
    @Operation(summary = "創建分類",description = "創建分類物件")
    public ResponseBody<CategoryDto> createCategory(@Parameter(description = "分類內容") @Validated @RequestBody CategoryDto categoryDto) {
        try {
            categoriesService.save(categoryDto);
        } catch (Exception e) {
            return new ResponseBody<>(false, "創建失敗, 失敗原因: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseBody<>(true, "創建成功", HttpStatus.OK);
    }

    @NoResubmit(delaySecond = 3)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping
    @Operation(summary = "更新分類",description = "更新分類物件")
    public ResponseBody<CategoryDto> updateCategory(
            @Parameter(description = "分類內容") @Validated @RequestBody CategoryDto categoryDto) {
        try {
            categoriesService.update(categoryDto);
        }  catch (Exception e) {
            return new ResponseBody<>(false, "更新失敗, 失敗原因: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseBody<>(true, "更新成功", HttpStatus.OK);
    }
    @NoResubmit(delaySecond = 3)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("{id}")
    @Operation(summary = "刪除分類",description = "用id刪除分類")
    public ResponseBody<String> deleteCategory(@Parameter(description = "分類id",example = "1") @PathVariable Long id) {
        try {
            categoriesService.delete(id);
        }  catch (Exception e) {
            return new ResponseBody<>(false, "刪除失敗, 失敗原因: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseBody<>(true, "刪除成功", HttpStatus.OK);
    }
}
