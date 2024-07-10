package com.blog.service.impl;

import com.blog.dao.CategoryPoRepository;
import com.blog.dto.CategoryDto;
import com.blog.exception.ValidateFailedException;
import com.blog.mapper.CategoryPoMapper;
import com.blog.po.CategoryPo;

import com.blog.service.CategoriesService;

import jakarta.mail.MethodNotSupportedException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class CategoriesServiceImpl implements CategoriesService {

    private final Logger logger = LoggerFactory.getLogger(CategoriesServiceImpl.class);
    private final CategoryPoRepository categoryPoRepository;

    /*
     * 用於檢查分類下文章是否為空
     *
     * @param  categoryId 分類序號
     * @throws ValidateFailedException
    **/
    private void validateCategory(Long categoryId) throws ValidateFailedException {
        CategoryPo categoryPo = categoryPoRepository.findById(categoryId).orElse(null);
        if (categoryPo == null)
            throw new ValidateFailedException("找不到該分類序號" + categoryId + "的資料");
        if (!categoryPo.getPosts().isEmpty())
            throw new ValidateFailedException("該分類下還有文章無法刪除");
    }

    /**
     * 新增分類
     *
     * @param  categoryDto 新增分類資訊
     * @see CategoryDto
     * @throws IllegalArgumentException 遇到操作錯誤則拋到
     */
    @Override
    public void save(CategoryDto categoryDto) throws IllegalArgumentException {
        if (categoryDto == null) {
            throw new ValidateFailedException("請輸入分類資訊");
        }
        try {
            CategoryPo categoryPo = CategoryPoMapper.INSTANCE.toPo(categoryDto);
            logger.info("新增分類中.... {}", categoryPo);
            categoryPoRepository.saveAndFlush(categoryPo);
            logger.info("新增分類成功: {}", categoryPo);
        } catch (Exception e) {
            logger.error("新增分類時遭遇錯誤: {}", e.getMessage());
            throw new IllegalArgumentException("新增分類時遭遇錯誤");
        }
    }

    /**
     * 更新分類
     *
     * @param  categoryDto 更新分類資訊
     * @see CategoryDto
     * @throws IllegalArgumentException 遇到操作錯誤則拋到
     */
    @Override
    public void update(CategoryDto categoryDto) throws IllegalArgumentException {
        validateCategory(categoryDto.getId());
        try {
            CategoryPo categoryPo = CategoryPoMapper.INSTANCE.toPo(categoryDto);
            logger.debug("更新分類中.... {}", categoryPo);
            categoryPoRepository.saveAndFlush(categoryPo);
            logger.debug("更新分類成功: {}", categoryPo);
        } catch (Exception e) {
            logger.error("更新分類時遭遇錯誤: {}", e.getMessage());
            throw new IllegalArgumentException("更新分類時遭遇錯誤");
        }
    }

    /**
     * 刪除分類
     *
     * @param  categoryDto 刪除分類資訊
     * @see CategoryDto 分類資訊
     * @throws IllegalArgumentException 遇到操作錯誤則拋到
     */
    @Override
    public void delete(CategoryDto categoryDto) throws MethodNotSupportedException {
        throw new MethodNotSupportedException("刪除分類不支援此方法");
    }

    /**
     * @param id  分類序號
     */
    @Override
    public void delete(Long id) {
        if(id == null) {
            throw new IllegalArgumentException("請輸入分類序號");
        }
        try {
            logger.info("刪除分類中.... {}", id);
            categoryPoRepository.deleteById(id);
        } catch (Exception e) {
            logger.error("刪除分類時遭遇錯誤: {}", e.getMessage());
            throw new IllegalArgumentException("刪除分類時遭遇錯誤");
        }
    }

    /**
     * 取得分類
     *
     * @param  id 分類序號
     * @see CategoryDto
     * @return CategoryDto
     * @throws EntityNotFoundException 找不到資料拋出該異常
     */
    @Override
    public CategoryDto findById(Long id) throws EntityNotFoundException {
        CategoryPo categoryPo = categoryPoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("找不到該分類序號" + id + "的資料"));
        return CategoryPoMapper.INSTANCE.toDto(categoryPo);
    }

    /**
     * 取得所有分類
     * @return List<CategoryDto> 分類集合
     */
    @Override
    public List<CategoryDto> findAll() {
        List<CategoryPo> categoryPoList = categoryPoRepository.findAll();
        return categoryPoList.stream()
                .map(CategoryPoMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 查詢分類
     *
     * @param  name 查詢條件
     * @see CategoryDto
     *
     * @throws Exception 遇到操作錯誤則拋到
     * @return List<CategoryDto> 分類集合
     *
     * */
    @Override
    public List<CategoryDto> findAll(String name) throws Exception {
        // 實現動態查詢
        Specification<CategoryPo> categoryPoSpecification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (StringUtils.hasText(name)) {
                Predicate predicate = criteriaBuilder.like(root.get("name"), "%" + name + "%");
                predicateList.add(predicate);
            }
            return criteriaBuilder.and(predicateList.toArray(new Predicate[0]));
        };
        List<CategoryPo> categoryPoList = categoryPoRepository.findAll(categoryPoSpecification);
        return categoryPoList.stream().map(CategoryPoMapper.INSTANCE::toDto).collect(Collectors.toList());
    }


    /**
     * 分頁查詢
     *
     * @param  page 頁碼
     * @param  pageSize 每頁幾筆資料
     * @see CategoryDto
     *
     * @throws Exception 遇到操作錯誤則拋到
     * @return Page<CategoryDto> 分頁資料
     *
     * */
    @Override
    public Page<CategoryDto> findAll(Integer page, Integer pageSize) throws Exception {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<CategoryPo> categoryPoPage = categoryPoRepository.findAll(pageable);
        return categoryPoPage.map(CategoryPoMapper.INSTANCE::toDto);
    }

    /**
     * 分頁查詢
     *
     * @param  page 頁碼
     * @param  pageSize 每頁幾筆資料
     * @see CategoryDto
     *
     * @throws Exception 遇到操作錯誤則拋到
     * @return Page<CategoryDto> 分頁資料
     *
     * */
    @Override
    public Page<CategoryDto> findAll(Integer page, Integer pageSize, String name) throws Exception {
        // 實現動態查詢
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Specification<CategoryPo> categoryPoSpecification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (StringUtils.hasText(name)) {
                Predicate predicate = criteriaBuilder.like(root.get("name"), "%" + name + "%");
                predicateList.add(predicate);
            }
            return criteriaBuilder.and(predicateList.toArray(new Predicate[0]));
        };
        Page<CategoryPo> categoryPoPage = categoryPoRepository.findAll(categoryPoSpecification, pageable);
        return categoryPoPage.map(CategoryPoMapper.INSTANCE::toDto);
    }

}
