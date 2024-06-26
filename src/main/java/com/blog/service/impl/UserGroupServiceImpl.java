package com.blog.service.impl;


import com.blog.dao.UserGroupPoRepository;
import com.blog.dto.UserGroupDto;
import com.blog.exception.ValidateFailedException;
import com.blog.mapper.UserGroupPoMapper;
import com.blog.po.UserGroupPo;
import com.blog.service.UserGroupService;
import com.blog.utils.SpringSecurityUtil;


import jakarta.annotation.Resource;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;


import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserGroupServiceImpl implements UserGroupService {
    @Resource
    private UserGroupPoRepository userGroupRepository;

    @Override
    public UserGroupDto findById(Long userGroupId) {
        return userGroupRepository.findById(userGroupId)
                .map(UserGroupPoMapper.INSTANCE::toDto)
                .orElse(null);
    }

    @Override
    public Page<UserGroupDto> findAll(int page, int size, String groupName,String reviewLevel) {
        Specification<UserGroupPo> specification = (root, query, criteriaBuilder) -> {;
            List<Predicate> predicates = new ArrayList<>();
            if (!ObjectUtils.isEmpty(groupName)) {
                predicates.add(criteriaBuilder.equal(root.get("groupName"), groupName));
            }
            return query.where(predicates.toArray(new Predicate[0])).getRestriction();
        };
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return userGroupRepository.findAll(specification, pageRequest).map(UserGroupPoMapper.INSTANCE::toDto);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<UserGroupDto> findAll() {
        return userGroupRepository.findAll()
                .stream()
                .map(UserGroupPoMapper.INSTANCE::toDto)
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(UserGroupDto userGroupDto) throws ValidateFailedException {
        validateUserGroup(userGroupDto);
        UserGroupPo userGroupPo = UserGroupPoMapper.INSTANCE.toPo(userGroupDto);
        userGroupPo.setCreateDate(LocalDateTime.now(ZoneId.of("Asia/Taipei")));
        userGroupPo.setCreatUser(SpringSecurityUtil.getCurrentUser());
        userGroupRepository.saveAndFlush(userGroupPo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(UserGroupDto userGroupDto) throws ValidateFailedException {
        validateUserGroup(userGroupDto);
        userGroupRepository.findById(userGroupDto.getId()).ifPresent(userGroupPo -> {
            UserGroupPoMapper.INSTANCE.partialUpdate(userGroupDto, userGroupPo);
            userGroupPo.setUpdDate(LocalDateTime.now(ZoneId.of("Asia/Taipei")));
            userGroupPo.setUpdateUser(SpringSecurityUtil.getCurrentUser());
            userGroupRepository.saveAndFlush(userGroupPo);
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class,isolation = Isolation.REPEATABLE_READ)
    public String delete(Long userGroupId) throws ValidateFailedException {
        Optional<UserGroupPo> optional = userGroupRepository.findById(userGroupId);
        UserGroupPo userGroupPo = optional.orElseThrow(() -> new ValidateFailedException("群組不存在"));
        // 如果群組底下有任何使用者，則不能刪除
        if(!userGroupPo.getUserPoList().isEmpty()){
            throw new ValidateFailedException(ValidateFailedException.DomainErrorStatus.RESOURCE_CANNOT_DELETE);
        }
        userGroupRepository.deleteById(userGroupId);
        return "群組刪除成功";
    }

    private void validateUserGroup(UserGroupDto userGroupDto) throws ValidateFailedException {
        if (userGroupDto == null) {
            throw new ValidateFailedException(ValidateFailedException.DomainErrorStatus.RESOURCE_IS_EMPTY);
        }
        if (ObjectUtils.isEmpty(userGroupDto.getGroupName())) {
            throw new ValidateFailedException(ValidateFailedException.DomainErrorStatus.RESOURCE_IS_EMPTY);
        }
    }

}
