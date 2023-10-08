package com.blog.service.impl;

import com.blog.dao.RolePoRepository;
import com.blog.dto.RoleDto;
import com.blog.mapper.RolePoMapper;
import com.blog.service.RoleService;
import com.blog.utils.LoginUtils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;


@Service
public class RoleServiceImpl implements RoleService {

    @Resource
    private RolePoRepository rolePoRepository;

    @Override
    public RoleDto findByRoleId(long roleId) {
        if(rolePoRepository.findById(roleId).isPresent()){
            return RolePoMapper.INSTANCE.toDto(rolePoRepository.findById(roleId).get());
        }
        return null;
    }

    @Override
    public RoleDto findByRoleName(String roleName) {
        if(rolePoRepository.findByName(roleName).isPresent()){
            return RolePoMapper.INSTANCE.toDto(rolePoRepository.findByName(roleName).get());
        }
        return null;
    }

    @Override
    public RoleDto createRole(RoleDto roleDto) {
        if(roleDto.getCreatUser() == null)
           roleDto.setCreatUser(LoginUtils.getCurrentUser());
        if(roleDto.getCreateDate() == null)
            roleDto.setCreateDate(LocalDateTime.now(ZoneId.of("Asia/Taipei")));
        return RolePoMapper.INSTANCE.toDto(rolePoRepository.save(RolePoMapper.INSTANCE.toPo(roleDto)));
    }

    @Override
    public Page<RoleDto> findAll(int page, int size, String sort,String direction) {
        return rolePoRepository.findAll(PageRequest.of(page, size, Sort.Direction.valueOf(direction), sort))
                .map(RolePoMapper.INSTANCE::toDto);
    }

    @Override
    public void saveAll(List<RoleDto> list) {
        list.forEach(roleDto -> {
            if(roleDto.getCreatUser() == null)
                roleDto.setCreatUser(LoginUtils.getCurrentUser());
            if(roleDto.getCreateDate() == null)
                roleDto.setCreateDate(LocalDateTime.now(ZoneId.of("Asia/Taipei")));
        });
        rolePoRepository.saveAll(RolePoMapper.INSTANCE.toPoList(list));
    }
}
