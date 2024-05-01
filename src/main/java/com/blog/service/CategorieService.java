package com.blog.service;

import com.blog.dto.CategoryDto;
import com.blog.exception.ValidateFailedException;
import org.springframework.data.domain.Page;

import java.util.List;


public interface CategorieService {
    void add(CategoryDto categoryDto);

    void edit(long categoryId, CategoryDto categoryDto) throws ValidateFailedException;

    String delete(Long categoryId) throws ValidateFailedException;

    CategoryDto findById(Long id);
    Page<CategoryDto> findAll(Integer page, Integer size, String name);

    List<CategoryDto> findAll();
}
