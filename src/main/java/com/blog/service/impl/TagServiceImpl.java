package com.blog.service.impl;

import com.blog.dao.CategoryPoRepository;
import com.blog.dao.TagPoRepository;
import com.blog.dto.TagDto;
import com.blog.mapper.TagPoMapper;
import com.blog.po.TagPo;
import com.blog.service.TagService;
import com.blog.utils.SpringSecurityUtil;
import jakarta.annotation.Resource;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class TagServiceImpl implements TagService {

    @Resource
    private TagPoRepository tagPoRepository;

    @Resource
    private CategoryPoRepository categoryPoRepository;

    @Override
    public Page<TagDto> findAll(Integer page, Integer size, String name) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Specification<TagPo> specification = (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            if (!ObjectUtils.isEmpty(name)) {
                predicate.getExpressions().add(criteriaBuilder.like(root.get("name"), "%" + name + "%"));
            }
            return predicate;
        };
        Page<TagPo> tagPos = tagPoRepository.findAll(specification, pageRequest);
        return TagPoMapper.INSTANCE.toDtoPage(tagPos);
    }

    @Override
    public List<TagDto> findAll() {
        return tagPoRepository.findAll()
                .stream()
                .map(TagPoMapper.INSTANCE::toDto).toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(TagDto tagDto) {
        TagPo tagPo = TagPoMapper.INSTANCE.toPo(tagDto);
        tagPo.setCreateDate(LocalDateTime.now(ZoneId.of("Asia/Taipei")));
        tagPo.setCreatUser(SpringSecurityUtil.getCurrentUser());
        tagPo.setCategory(categoryPoRepository.findById(tagDto.getCategoryId()).orElse(null));
        tagPoRepository.saveAndFlush(tagPo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(TagDto tagDto) {
        tagPoRepository.findById(tagDto.getId()).ifPresent(tagPo -> {
            TagPoMapper.INSTANCE.partialUpdate(tagDto, tagPo);
            tagPo.setCategory(categoryPoRepository.findById(tagDto.getCategoryId()).orElse(null));
            tagPo.setUpdDate(LocalDateTime.now(ZoneId.of("Asia/Taipei")));
            tagPo.setUpdateUser(SpringSecurityUtil.getCurrentUser());
            tagPoRepository.saveAndFlush(tagPo);
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String delete(Long id) {
        tagPoRepository.deleteById(id);
        return "刪除成功";
    }

    @Override
    public TagDto findById(Long id) {
        return tagPoRepository.findById(id).map(TagPoMapper.INSTANCE::toDto).orElse(null);
    }

}

