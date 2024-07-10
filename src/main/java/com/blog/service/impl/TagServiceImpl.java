package com.blog.service.impl;

import com.blog.dao.CategoryPoRepository;
import com.blog.dao.TagPoRepository;
import com.blog.dto.TagDto;
import com.blog.mapper.TagPoMapper;
import com.blog.po.CategoryPo;
import com.blog.po.TagPo;
import com.blog.service.TagService;
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
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagPoRepository tagPoRepository;
    private final CategoryPoRepository categoryPoRepository;
    private static final Logger logger = LoggerFactory.getLogger(TagServiceImpl.class);

    /**
     * 新增標籤
     *
     * @param tagDto 標籤資訊
     * @throws Exception 遭遇異常時拋出
     */
    @Override
    public void save(TagDto tagDto) throws Exception {
        logger.debug("新增標籤... " + tagDto);
        TagPo tagPo = TagPoMapper.INSTANCE.toPo(tagDto);
        tagPoRepository.saveAndFlush(tagPo);
        // 與 category 建立關聯關係
        if(tagDto.getCategoryId() == null) {
            throw new IllegalArgumentException("請選擇標籤所屬分類");
        }
        CategoryPo categoryPo = categoryPoRepository.findById(tagDto.getCategoryId()).orElse(null);
        if (categoryPo == null) {
            throw new EntityNotFoundException("找不到該分類序號" + tagDto.getCategoryId() + "的資料");
        }
        tagPo.setCategory(categoryPo);
        tagPoRepository.saveAndFlush(tagPo);
    }

    /**
     * 更新標籤
     *
     * @param tagDto 標籤資訊
     * @throws Exception 遭遇異常時拋出
     */
    @Override
    public void update(TagDto tagDto) throws Exception {
        TagPo tagPo = tagPoRepository.findById(tagDto.getId()).orElseThrow(() -> new EntityNotFoundException("找不到標籤序號" + tagDto.getId() + "的資料"));
        TagPoMapper.INSTANCE.partialUpdate(tagDto, tagPo);
        // 與 category 建立關聯關係
        if(tagDto.getCategoryId() == null) {
            throw new IllegalArgumentException("請選擇標籤所屬分類");
        }
        CategoryPo categoryPo = categoryPoRepository.findById(tagDto.getCategoryId()).orElseThrow(() -> new EntityNotFoundException("找不到該分類序號" + tagDto.getCategoryId() + "的資料"));
        tagPo.setCategory(categoryPo);
        logger.debug("更新標籤... " + tagPo);
        tagPoRepository.saveAndFlush(tagPo);
    }

    /**
     * @param tagDto 標籤資訊
     * @throws Exception 遭遇異常時拋出
     */
    @Override
    public void delete(TagDto tagDto) throws Exception {
        throw new MethodNotSupportedException("不支援該種刪除方式");
    }

    /**
     * 刪除標籤
     *
     * @param id 標籤序號
     * @throws Exception 遭遇異常時拋出
     */
    @Override
    public void delete(Long id) throws Exception {
        logger.debug("刪除標籤... " + id);
        if(id == null){
            throw new IllegalArgumentException("標籤序號不能為空");
        }
        if(!tagPoRepository.existsById(id)){
            throw new EntityNotFoundException("找不到該標籤序號" + id + "的資料");
        }
        tagPoRepository.deleteById(id);
    }

    /**
     * 搜尋指定序號的標籤
     *
     * @param id 標籤序號
     * @return 標籤資訊
     * @throws EntityNotFoundException 找不到物件時拋出該異常
     */
    @Override
    public TagDto findById(Long id) throws EntityNotFoundException {
        TagPo tagPo = tagPoRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("找不到該標籤序號" + id + "的資料"));
        return TagPoMapper.INSTANCE.toDto(tagPo);
    }

    /**
     * 搜尋所有標籤物件
     *
     * @return  標籤資訊集合
     * @throws Exception 遭遇異常時拋出
     */
    @Override
    public List<TagDto> findAll() throws Exception {
        logger.debug("搜尋標籤... ");
        return tagPoRepository.findAll().stream().map(TagPoMapper.INSTANCE::toDto).toList();
    }

    /**
     * 搜尋全部分頁物件
     *
     * @param page 當前頁數
     * @param pageSize 每頁筆數
     * @return Page<TagDto> 標籤資訊集合
     * @throws Exception 遭遇異常時拋出
     */
    @Override
    public Page<TagDto> findAll(Integer page, Integer pageSize) throws Exception {
        return tagPoRepository.findAll(PageRequest.of(page - 1, pageSize)).map(TagPoMapper.INSTANCE::toDto);
    }



    /*
     * 搜尋當前熱門標籤
     *
     * @return  近十筆熱門標籤
     * @throws Exception 遭遇異常時拋出
     */
    @Override
    public List<TagDto> findHotTags() throws Exception {
        List<TagPo> hotTags = tagPoRepository.findHotTags();
        logger.debug("搜尋標籤... 標籤內容為 {}", hotTags);
        return hotTags.stream().map(TagPoMapper.INSTANCE::toDto).toList();
    }

    /**
     * 搜尋符合條件的標籤分頁集合
     *
     * @param page  當前頁數
     * @param pageSize 每頁筆數
     * @param name 標籤名抽
     * @return Page<TagDto> 標籤資訊集合
     */
    @Override
    public Page<TagDto> findAll(Integer page, Integer pageSize, String name) {
        Specification<TagPo> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(name)) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + name + "%"));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        return tagPoRepository.findAll(spec, PageRequest.of(page - 1, pageSize)).map(TagPoMapper.INSTANCE::toDto);
    }
}

