package com.blog.service.impl;

import com.blog.dao.CategoryPoRepository;
import com.blog.dto.CategoryDto;
import com.blog.exception.ValidateFailedException;
import com.blog.mapper.CategoryPoMapper;
import com.blog.po.CategoryPo;
import com.blog.service.CategorieService;

import com.blog.utils.SpringSecurityUtil;
import jakarta.annotation.Resource;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Service
public class CategorieServiceImpl implements CategorieService {
    @Resource
    private CategoryPoRepository categoryPoRepository;

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(CategoryDto categoryDto) {
        CategoryPo categoryPo = CategoryPoMapper.INSTANCE.toPo(categoryDto);
        categoryPo.setCreateDate(LocalDateTime.now());
        categoryPo.setCreatUser(SpringSecurityUtil.getCurrentUser());
        categoryPoRepository.saveAndFlush(categoryPo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String delete(Long categoryId) throws ValidateFailedException {
        validateCategory(categoryId);
        categoryPoRepository.deleteById(categoryId);
        return "刪除成功";
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(CategoryDto categoryDto) throws ValidateFailedException {
        CategoryPo categoryPo = categoryPoRepository.findById(categoryDto.getId()).orElseThrow(
                () -> new ValidateFailedException(ValidateFailedException.DomainErrorStatus.RESOURCE_NOT_FOUND));
        categoryPo = CategoryPoMapper.INSTANCE.partialUpdate(categoryDto, categoryPo);
        categoryPo.setUpdDate(LocalDateTime.now(ZoneId.of("Asia/Taipei")));
        categoryPo.setUpdateUser(SpringSecurityUtil.getCurrentUser());
        categoryPoRepository.saveAndFlush(categoryPo);
    }

    @Override
    public CategoryDto findById(Long id) {
        return CategoryPoMapper.INSTANCE.toDto(categoryPoRepository.findById(id).orElse(null));
    }

    @Override
    public Page<CategoryDto> findAll(Integer page, Integer size, String name) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Specification<CategoryPo> specification = (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            if (!ObjectUtils.isEmpty(name)) {
                predicate.getExpressions().add(criteriaBuilder.like(root.get("name"), "%" + name + "%"));
            }
            return predicate;
        };
        Page<CategoryPo> categoryPos = categoryPoRepository.findAll(specification, pageRequest);
        return CategoryPoMapper.INSTANCE.toDtoPage(categoryPos);
    }

    @Override
    public List<CategoryDto> findAll() {
        return categoryPoRepository.findAll()
                .stream()
                .map(CategoryPoMapper.INSTANCE::toDto).toList();
    }

    private void validateCategory(Long categoryId) throws ValidateFailedException {
        CategoryPo categoryPo = categoryPoRepository.findById(categoryId).orElse(null);
        if(categoryPo == null)
            throw new ValidateFailedException(ValidateFailedException.DomainErrorStatus.RESOURCE_NOT_FOUND);
        if(!categoryPo.getPosts().isEmpty())
            throw new ValidateFailedException(ValidateFailedException.DomainErrorStatus.CATEGORY_HAS_POSTS);
    }

}
