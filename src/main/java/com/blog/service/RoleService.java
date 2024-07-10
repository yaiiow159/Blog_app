package com.blog.service;

import com.blog.dto.RoleDto;
import org.springframework.data.domain.Page;

public interface RoleService extends BaseService<RoleDto> {
    Page<RoleDto> findAll(Integer page, Integer pageSize, String name);
}
