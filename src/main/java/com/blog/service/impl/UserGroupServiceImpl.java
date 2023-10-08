package com.blog.service.impl;

import com.blog.dao.UserGroupPoRepository;
import com.blog.dao.UserJpaRepository;
import com.blog.dto.UserGroupDto;
import com.blog.enumClass.ReviewLevel;
import com.blog.exception.ValidateFailedException;
import com.blog.mapper.UserGroupPoMapper;
import com.blog.po.UserGroupPo;
import com.blog.po.UserPo;
import com.blog.service.UserGroupService;
import com.blog.utils.LoginUtils;


import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import javax.persistence.criteria.Predicate;
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
        Optional<UserGroupPo> userGroupPo = userGroupRepository.findById(userGroupId);
        return userGroupPo.map(UserGroupPoMapper.INSTANCE::toDto).orElse(null);
    }

    @Override
    public UserGroupDto findByGroupName(String groupName) {
        Optional<UserGroupPo> userGroupPo = userGroupRepository.findByGroupName(groupName);
        return userGroupPo.map(UserGroupPoMapper.INSTANCE::toDto).orElse(null);
    }

    @Override
    public Page<UserGroupDto> findAll(int page, int size, String sort) {
        List<UserGroupPo> userGroupPoList = userGroupRepository.findByIsDeletedFalse();
        return new PageImpl<>(UserGroupPoMapper.INSTANCE.toDtoList(userGroupPoList), PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")), userGroupPoList.size());
    }

    @Override
    public Page<UserGroupDto> findBySpec(String groupName, String description, int page, int size, String sort) {
        Specification<UserGroupPo> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (!ObjectUtils.isEmpty(groupName)) {
                predicates.add(criteriaBuilder.like(root.get("groupName"), "%" + groupName + "%"));
            }
            if (!ObjectUtils.isEmpty(description)) {
                predicates.add(criteriaBuilder.like(root.get("description"), "%" + description + "%"));
            }
            predicates.add(criteriaBuilder.equal(root.get("isDeleted"), false));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        Page<UserGroupPo> userGroupPoPage = userGroupRepository.findAll(spec, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
        return userGroupPoPage.map(UserGroupPoMapper.INSTANCE::toDto);
    }

    @Override
    public UserGroupDto createGroup(UserGroupDto userGroupDto) throws ValidateFailedException {
        validateUserGroup(userGroupDto);
        userGroupDto.setIsDeleted(false);
        userGroupDto.setCreatUser(LoginUtils.getCurrentUser());
        userGroupDto.setCreateDate(LocalDateTime.now(ZoneId.of("Asia/Taipei")));
        UserGroupPo userGroupPo = UserGroupPoMapper.INSTANCE.toPo(userGroupDto);
        return UserGroupPoMapper.INSTANCE.toDto(userGroupRepository.save(userGroupPo));
    }

    private void validateUserGroup(UserGroupDto userGroupDto) throws ValidateFailedException {
        if (userGroupDto == null) {
            throw new ValidateFailedException(ValidateFailedException.DomainErrorStatus.RESOURCE_IS_EMPTY);
        }
        if (ObjectUtils.isEmpty(userGroupDto.getGroupName())) {
            throw new ValidateFailedException(ValidateFailedException.DomainErrorStatus.RESOURCE_IS_EMPTY);
        }
        // 沒有給預設值，預設給予只能覆核查詢
        if(userGroupDto.getReviewLevel() == null){
            userGroupDto.setReviewLevel(ReviewLevel.SEARCH_ONLY);
        }
    }

    @Override
    public UserGroupDto updateGroup(UserGroupDto userGroupDto) throws ValidateFailedException {
        validateUserGroup(userGroupDto);
        userGroupRepository.findByIdAndIsDeletedFalse(userGroupDto.getId()).ifPresent(userGroupPo -> {
            userGroupPo.setGroupName(userGroupDto.getGroupName());
            userGroupPo.setDescription(userGroupDto.getDescription());
            userGroupPo.setReviewLevel(userGroupDto.getReviewLevel());
            userGroupPo.setUpdDate(LocalDateTime.now(ZoneId.of("Asia/Taipei")));
            userGroupPo.setUpdateUser(LoginUtils.getCurrentUser());
            userGroupRepository.save(userGroupPo);
        });
        return UserGroupPoMapper.INSTANCE.toDto(userGroupRepository.save(userGroupRepository.findByIdAndIsDeletedFalse(userGroupDto.getUserId()).get()));
    }

    @Override
    public String deleteGroup(Long userGroupId) throws ValidateFailedException {
        Optional<UserGroupPo> optional = userGroupRepository.findById(userGroupId);
        UserGroupPo userGroupPo = optional.orElseThrow(() -> new ValidateFailedException("群組不存在"));
        // 如果群組底下有任何使用者，則不能刪除
        if(!userGroupPo.getUserPoList().isEmpty()){
            throw new ValidateFailedException(ValidateFailedException.DomainErrorStatus.RESOURCE_CANNOT_DELETE);
        }
        optional.get().setIsDeleted(true);
        userGroupRepository.save(optional.get());
        if(userGroupRepository.findByIdAndIsDeletedFalse(userGroupId).isPresent()){
            return "fail";
        }
        return "success";
    }

}
