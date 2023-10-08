package com.blog.service.impl;

import com.blog.dao.CategoryPoRepository;
import com.blog.dao.PostPoRepository;
import com.blog.dto.CategoryDto;
import com.blog.exception.ValidateFailedException;
import com.blog.mapper.CategoryPoMapper;
import com.blog.po.CategoryPo;
import com.blog.service.CategorieService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;


@Service
public class CategorieServiceImpl implements CategorieService {
    @Resource
    private CategoryPoRepository categoryPoRepository;

    @Resource
    private PostPoRepository postPoRepository;
    @Override
    public Page<CategoryDto> findAllCategories(int page, int size, String sort) {
        Page<CategoryPo> categoryPos = categoryPoRepository.findAll(PageRequest.of(page, size, Sort.by(sort)));
        if(CollectionUtils.isEmpty(categoryPos.getContent()))
            return null;
        return categoryPos.map(CategoryPoMapper.INSTANCE::toDto);
    }

    @Override
    public CategoryDto createCategory(CategoryDto categoryDto) {
        CategoryPo categoryPo = CategoryPoMapper.INSTANCE.toPo(categoryDto);
        CategoryPo po = categoryPoRepository.save(categoryPo);
        return CategoryPoMapper.INSTANCE.toDto(po);
    }

    @Override
    public String deleteCategory(Long categoryId) throws ValidateFailedException {
        validateCategory(categoryId);
        CategoryPo categoryPo = categoryPoRepository.findById(categoryId).get();
        categoryPo.setIsDeleted(true);
        categoryPo = categoryPoRepository.save(categoryPo);
        if(categoryPo.getIsDeleted().equals(Boolean.FALSE))
            return "fail";
        return "success";
    }

    private void validateCategory(Long categoryId) throws ValidateFailedException {
        CategoryPo categoryPo = categoryPoRepository.findById(categoryId).orElse(null);
        if(categoryPo == null)
            throw new ValidateFailedException(ValidateFailedException.DomainErrorStatus.RESOURCE_NOT_FOUND);
        if(!categoryPo.getPosts().isEmpty())
            throw new ValidateFailedException(ValidateFailedException.DomainErrorStatus.RESOURCE_IS_EMPTY);
    }

    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto) throws ValidateFailedException {
        CategoryPo categoryPo = categoryPoRepository.findByName(categoryDto.getName()).orElse(null);
        if(categoryPo == null)
            throw new ValidateFailedException(ValidateFailedException.DomainErrorStatus.RESOURCE_NOT_FOUND);

        categoryPo = CategoryPoMapper.INSTANCE.partialUpdate(categoryDto, categoryPo);
        CategoryPo po = categoryPoRepository.save(categoryPo);
        return CategoryPoMapper.INSTANCE.toDto(po);
    }

    @Override
    public CategoryDto findById(Long id) {
        return CategoryPoMapper.INSTANCE.toDto(categoryPoRepository.findById(id).orElse(null));
    }
}
