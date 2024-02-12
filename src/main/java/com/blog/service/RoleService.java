package com.blog.service;

import com.blog.dto.RoleDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface RoleService {

    RoleDto findByRoleName(String roleName);

    RoleDto createRole(RoleDto roleDto);

    RoleDto updateRole(RoleDto roleDto);

    Page<RoleDto> findAll(String name,int page, int size, String sort, String direction);

    void saveAll(List<RoleDto> list);
}
