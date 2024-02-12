package com.blog.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.blog.dao.CategoryPoRepository;
import com.blog.dao.PostPoRepository;
import com.blog.dto.CategoryDto;
import com.blog.exception.ValidateFailedException;
import com.blog.mapper.CategoryPoMapper;
import com.blog.po.CategoryPo;
import com.blog.service.CategorieService;

import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;



@Service
public class CategorieServiceImpl implements CategorieService {
    @Resource
    private CategoryPoRepository categoryPoRepository;

    @Resource
    private PostPoRepository postPoRepository;
    @Override
    public Page<CategoryDto> findAllCategories(int page, int size, String sort, String desc) {
        Page<CategoryPo> categoryPos = categoryPoRepository.findAllByIsDeletedFalse(PageRequest.of(page - 1, size, Sort.by(Sort.Direction.fromString(desc), sort)));
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
        JSONObject jsonObject = new JSONObject();
        validateCategory(categoryId);
        CategoryPo categoryPo = categoryPoRepository.findById(categoryId).get();
        categoryPo.setIsDeleted(true);
        categoryPo = categoryPoRepository.save(categoryPo);
        if (categoryPo.getIsDeleted().equals(Boolean.FALSE)){
            jsonObject.put("message", "刪除分類失敗");
            return jsonObject.toJSONString();
        }
        jsonObject.put("message", "刪除分類成功");
        return jsonObject.toJSONString();
    }

    private void validateCategory(Long categoryId) throws ValidateFailedException {
        CategoryPo categoryPo = categoryPoRepository.findById(categoryId).orElse(null);
        if(categoryPo == null)
            throw new ValidateFailedException(ValidateFailedException.DomainErrorStatus.RESOURCE_NOT_FOUND);
        if(!categoryPo.getPosts().isEmpty())
            throw new ValidateFailedException(ValidateFailedException.DomainErrorStatus.RESOURCE_IS_EMPTY);
    }

    @Override
    public CategoryDto updateCategory(long categoryId,CategoryDto categoryDto) throws ValidateFailedException {
        CategoryPo categoryPo = categoryPoRepository.findById(categoryId).orElseThrow(
                () -> new ValidateFailedException(ValidateFailedException.DomainErrorStatus.RESOURCE_NOT_FOUND));
        categoryPo = CategoryPoMapper.INSTANCE.partialUpdate(categoryDto, categoryPo);
        CategoryPo po = categoryPoRepository.save(categoryPo);
        return CategoryPoMapper.INSTANCE.toDto(po);
    }

    @Override
    public CategoryDto findById(Long id) {
        return CategoryPoMapper.INSTANCE.toDto(categoryPoRepository.findById(id).orElse(null));
    }
}
