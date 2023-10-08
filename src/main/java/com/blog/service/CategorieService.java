package com.blog.service;

import com.blog.dto.CategoryDto;
import com.blog.exception.ValidateFailedException;
import org.springframework.data.domain.Page;


public interface CategorieService {
    Page<CategoryDto> findAllCategories(int page, int size, String sort);

    CategoryDto createCategory(CategoryDto categoryDto);

    String deleteCategory(Long categoryId) throws ValidateFailedException;

    CategoryDto updateCategory(CategoryDto categoryDto) throws ValidateFailedException;

    CategoryDto findById(Long id);
}
