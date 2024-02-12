package com.blog.service.impl;

import com.blog.dao.RolePoRepository;
import com.blog.dto.RoleDto;
import com.blog.mapper.RolePoMapper;
import com.blog.po.RolePo;
import com.blog.service.RoleService;
import com.blog.utils.SpringSecurityUtils;

import jakarta.annotation.Resource;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;


@Service
public class RoleServiceImpl implements RoleService {

    @Resource
    private RolePoRepository rolePoRepository;

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
           roleDto.setCreatUser(SpringSecurityUtils.getCurrentUser());
        RolePo po = RolePoMapper.INSTANCE.toPo(roleDto);
        RolePo po1 = rolePoRepository.save(po);
        return RolePoMapper.INSTANCE.toDto(po1);
    }

    @Override
    public RoleDto updateRole(RoleDto roleDto) {
        if (roleDto.getCreatUser() == null)
            roleDto.setCreatUser(SpringSecurityUtils.getCurrentUser());

        rolePoRepository.findByName(roleDto.getRoleName()).ifPresent(rolePo -> {
            rolePo.setRoleName(roleDto.getRoleName());
            rolePo.setUpdateUser(SpringSecurityUtils.getCurrentUser());
            RolePo po1 = rolePoRepository.save(rolePo);
            roleDto.setRoleName(po1.getRoleName());
            roleDto.setUpdateUser(po1.getUpdateUser());
        });
        return roleDto;
    }

    @Override
    public Page<RoleDto> findAll(String name,int page, int size, String sort,String direction) {
        Specification<RolePo> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (!ObjectUtils.isEmpty(name)) {
                Predicate predicate = criteriaBuilder.like(root.get("roleName"), "%" + name + "%");
                predicates.add(predicate);
            }
            return query.where(predicates.toArray(new Predicate[0])).getRestriction();
        };
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(direction.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sort));
        Page<RolePo> rolePos = rolePoRepository.findAll(specification, pageable);
        return rolePos.map(RolePoMapper.INSTANCE::toDto);
    }

    @Override
    public void saveAll(List<RoleDto> list) {
        list.forEach(roleDto -> {
            if(roleDto.getCreatUser() == null)
                roleDto.setCreatUser(SpringSecurityUtils.getCurrentUser());
        });
        rolePoRepository.saveAll(RolePoMapper.INSTANCE.toPoList(list));
    }
}
