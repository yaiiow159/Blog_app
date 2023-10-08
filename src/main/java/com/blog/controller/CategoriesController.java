package com.blog.controller;

import com.blog.dto.CategoryDto;
import com.blog.dto.PostDto;
import com.blog.exception.ResourceNotFoundException;
import com.blog.exception.ValidateFailedException;
import com.blog.po.PostPo;
import com.blog.service.CategorieService;
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
    public ResponseEntity<Page<CategoryDto>> findAll(@Parameter(description = "頁數",example = "0") @RequestParam(name = "page",defaultValue = "0",required = false) int page,
                                                     @Parameter(description = "每頁筆數",example = "10") @RequestParam(name = "size",defaultValue = "10",required = false) int size,
                                                     @Parameter(description = "排序",example = "id") @RequestParam(name = "sort",defaultValue = "id" ,required = false) String sort){
        Page<CategoryDto> categoryDtos = categorieService.findAllCategories(page, size, sort);
        if(CollectionUtils.isEmpty(categoryDtos.getContent()))
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(categoryDtos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "查詢單筆分類",description = "用id查詢分類")
    public ResponseEntity<CategoryDto> findById(@Parameter(description = "分類id",example = "1") @PathVariable Long id) {
        CategoryDto categoryDto = categorieService.findById(id);
        if(ObjectUtils.isEmpty(categoryDto))
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(categoryDto, HttpStatus.OK);
    }

    @GetMapping("/{id}/posts")
    @Operation(summary = "查詢分類下的文章",description = "查詢分類下的文章")
    public ResponseEntity<Page<PostDto>> findPosts(@Parameter(description = "分類id",example = "1") @PathVariable Long id) throws ResourceNotFoundException {
        Page<PostDto> postDtos = postService.findPosts(id);
        if(CollectionUtils.isEmpty(postDtos.getContent()))
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(postDtos, HttpStatus.OK);
    }

    @GetMapping("/{id}/posts/{postId}")
    public ResponseEntity<PostDto> findTheOnePostsByCategory(@Parameter(description = "分類id",example = "1") @PathVariable Long id
            ,@Parameter(description = "文章id",example = "1") @PathVariable Long postId) throws ResourceNotFoundException {
        return new ResponseEntity<>(postService.findTheOnePostsByCategory(id,postId), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/create")
    @Operation(summary = "創建分類",description = "創建分類物件")
    public ResponseEntity<CategoryDto> createCategory(@Parameter(description = "分類內容") @RequestBody CategoryDto categoryDto) throws ResourceNotFoundException {
        CategoryDto category = categorieService.createCategory(categoryDto);
        if (ObjectUtils.isEmpty(category))
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(category, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/update")
    @Operation(summary = "更新分類",description = "更新分類物件")
    public ResponseEntity<CategoryDto> updateCategory(@Parameter(description = "分類內容") @RequestBody CategoryDto categoryDto) throws ResourceNotFoundException, ValidateFailedException {
        CategoryDto category = categorieService.updateCategory(categoryDto);
        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/delete/{categoryId}")
    @Operation(summary = "刪除分類",description = "用id刪除分類")
    public ResponseEntity<String> deleteCategory(@Parameter(description = "分類id",example = "1") @PathVariable Long categoryId) throws ResourceNotFoundException, ValidateFailedException {
        String result = categorieService.deleteCategory(categoryId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
