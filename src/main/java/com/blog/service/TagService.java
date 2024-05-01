package com.blog.service;

import com.blog.dto.TagDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TagService {
    Page<TagDto> findAll(Integer page, Integer size, String name);

    List<TagDto> findAll();

    void add(TagDto tagDto);

    void edit(TagDto tagDto);

    String delete(Long id);

    TagDto findById(Long id);
}
