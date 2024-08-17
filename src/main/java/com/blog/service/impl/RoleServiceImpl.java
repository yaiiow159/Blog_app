package com.blog.service.impl;

import com.blog.dao.RolePoRepository;
import com.blog.dto.RoleDto;
import com.blog.exception.ValidateFailedException;
import com.blog.mapper.RolePoMapper;
import com.blog.po.RolePo;
import com.blog.service.RoleService;

import jakarta.mail.MethodNotSupportedException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RolePoRepository rolePoRepository;
    private final Logger logger = LoggerFactory.getLogger(RoleServiceImpl.class);

    /**
     * 新增角色
     *
     * @param  roleDto 新增角色資訊
     * @see RoleDto
     */
    @Override
    public void save(RoleDto roleDto) {
        logger.info("新增角色資訊: {}", roleDto);
        if(roleDto == null) {
            throw new IllegalArgumentException("新增角色資訊不得為空");
        }
        if(rolePoRepository.existsByName(roleDto.getRoleName())) {
            throw new IllegalArgumentException("角色名稱重複");
        }
        RolePo rolePo = RolePoMapper.INSTANCE.toPo(roleDto);
        rolePoRepository.saveAndFlush(rolePo);
    }

    /**
     * 更新角色
     *
     * @param  roleDto 更新角色資訊
     * @see RoleDto
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void update(RoleDto roleDto) {
        logger.debug("更新角色資訊: {}", roleDto);
        if(roleDto == null) {
            throw new IllegalArgumentException("更新角色資訊不得為空");
        }
        if(rolePoRepository.existsByName(roleDto.getRoleName())) {
            throw new IllegalArgumentException("角色名稱重複");
        }
        RolePo rolePo = rolePoRepository.findById(roleDto.getId()).orElseThrow(() -> new EntityNotFoundException("找不到該角色序號" + roleDto.getId() + "的資料"));
        RolePoMapper.INSTANCE.partialUpdate(roleDto, rolePo);
        rolePoRepository.saveAndFlush(rolePo);
    }

    /**
     * 刪除角色
     *
     * @param  roleDto 刪除角色資訊
     * @see RoleDto
     */
    @Override
    public void delete(RoleDto roleDto) throws Exception {
        throw new MethodNotSupportedException("該刪除角色方法不支援");
    }

    /**
     * 依照角色序號刪除角色
     *
     * @param id  角色id
     * @throws Exception 遇到異常則抛到
     */
    @Override
    public void delete(Long id) throws Exception {
        logger.debug("刪除角色序號: {}", id);
        if(id == null) {
            throw new IllegalArgumentException("刪除角色資訊不得為空");
        }
        if(!rolePoRepository.existsById(id)) {
            throw new EntityNotFoundException("找不到該角色序號" + id + "的資料");
        }
        // 驗證是否有其他使用者正在使用該角色，有則不能刪
        if(rolePoRepository.countByIdIfUserUse(id) > 0) {
            throw new ValidateFailedException("該角色已被使用，無法刪除");
        }
        rolePoRepository.deleteById(id);
    }


    /**
     * 搜尋對應序號的角色資訊
     *
     * @param id 角色id
     * @return RoleDto 角色資訊
     * @throws EntityNotFoundException 遇到異常則抛到
     */
    @Override
    public RoleDto findById(Long id) throws EntityNotFoundException {
        logger.debug("查詢角色序號: {}", id);
        if(id == null) {
            throw new IllegalArgumentException("查詢角色資訊不得為空");
        }
        RolePo rolePo = rolePoRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("找不到該角色序號" + id + "的資料"));
        return RolePoMapper.INSTANCE.toDto(rolePo);
    }

    @Override
    public List<RoleDto> findAll() {
        logger.debug("查詢所有角色");
        List<RolePo> rolePos = rolePoRepository.findAll();
        return RolePoMapper.INSTANCE.toDtoList(rolePos);
    }

    /**
     * @param page 當前頁數
     * @param pageSize 每頁筆數
     * @return Page<RoleDto> 分頁物件
     *
     * @throws Exception 遇到異常則抛到
     */
    @Override
    public Page<RoleDto> findAll(Integer page, Integer pageSize) throws Exception {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<RolePo> rolePos = rolePoRepository.findAll(pageable);
        return rolePos.map(RolePoMapper.INSTANCE::toDto);
    }

    /**
     * 搜尋符合條件的分頁角色內容
     *
     * @param page     頁碼
     * @param pageSize 每頁筆數
     * @param name   查詢條件(可傳空)
     * @return Page<RoleDto> 分頁物件
     */
    @Override
    public Page<RoleDto> findAll(Integer page, Integer pageSize, String name) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Specification<RolePo> specification = (root, query, criteriaBuilder) -> {
            if (name != null) {
                query.where(criteriaBuilder.equal(root.get("roleName"), name));
            }
            return query.getRestriction();
        };
        Page<RolePo> rolePos = rolePoRepository.findAll(specification, pageable);
        return rolePos.map(RolePoMapper.INSTANCE::toDto);
    }
}
