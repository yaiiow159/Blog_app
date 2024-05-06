package com.blog.service;

import com.blog.dto.RoleDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface RoleService {

    RoleDto findByRoleName(String roleName);

    void add(RoleDto roleDto);

    void edit(RoleDto roleDto);

    String delete(Long id);

    Page<RoleDto> findAll(String name,int page, int size);

    List<RoleDto> findAll();

    List<RoleDto> getRoleByUserId(long id);

    RoleDto findById(Long id);
}
