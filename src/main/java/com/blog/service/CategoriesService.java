package com.blog.service;

import com.blog.dto.CategoryDto;
import org.springframework.data.domain.Page;

import java.util.List;


public interface CategoriesService extends BaseService<CategoryDto> {
    List<CategoryDto> findAll(String name) throws Exception;
    Page<CategoryDto> findAll(Integer page, Integer pageSize, String name) throws Exception;
}
