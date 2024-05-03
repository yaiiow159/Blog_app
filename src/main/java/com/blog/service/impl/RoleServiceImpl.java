package com.blog.service.impl;

import com.blog.dao.RolePoRepository;
import com.blog.dto.RoleDto;
import com.blog.mapper.RolePoMapper;
import com.blog.po.RolePo;
import com.blog.service.RoleService;
import com.blog.utils.SpringSecurityUtils;

import jakarta.annotation.Resource;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.support.PageableUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;


@Service
@Transactional
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
    public void add(RoleDto roleDto) {
        RolePo po = RolePoMapper.INSTANCE.toPo(roleDto);
        po.setCreateDate(LocalDateTime.now(ZoneId.of("Asia/Taipei")));
        po.setCreatUser(SpringSecurityUtils.getCurrentUser());
        rolePoRepository.saveAndFlush(po);
    }

    @Override
    public void edit(RoleDto roleDto) {
        rolePoRepository.findByName(roleDto.getRoleName()).ifPresent(rolePo -> {
            RolePo po = RolePoMapper.INSTANCE.partialUpdate(roleDto, rolePo);
            po.setUpdDate(LocalDateTime.now(ZoneId.of("Asia/Taipei")));
            po.setUpdateUser(SpringSecurityUtils.getCurrentUser());
            rolePoRepository.saveAndFlush(po);
        });
    }

    @Override
    public String delete(Long id) {
        rolePoRepository.deleteById(id);
        return "刪除成功";
    }

    @Override
    public Page<RoleDto> findAll(String name,int page, int size) {
        Specification<RolePo> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> list = new ArrayList<>();
            if(!ObjectUtils.isEmpty(name)){
                Predicate predicate = criteriaBuilder.like(root.get("name"), "%" + name + "%");
                list.add(predicate);
            }
            Predicate[] p = new Predicate[list.size()];
            return criteriaBuilder.and(list.toArray(p));
        };
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<RolePo> rolePos = rolePoRepository.findAll(specification, pageable);
        if(!rolePos.isEmpty()){
            return new PageImpl<>(RolePoMapper.INSTANCE.toDtoList(rolePos.getContent()), pageable, rolePos.getTotalElements());
        } else{
            return Page.empty();
        }
    }

    @Override
    public List<RoleDto> findAll() {
        return rolePoRepository.findAll().stream().map(RolePoMapper.INSTANCE::toDto).toList();
    }

    @Override
    public List<RoleDto> getRoleByUserId(long id) {
        return RolePoMapper.INSTANCE.toDtoList(rolePoRepository.findByUserId(id));
    }

}
