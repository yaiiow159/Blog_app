package com.blog.service;

import com.blog.dto.TagDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TagService extends BaseService<TagDto> {
    List<TagDto> findHotTags() throws Exception;
    Page<TagDto> findAll(Integer page, Integer pageSize, String name);
}
