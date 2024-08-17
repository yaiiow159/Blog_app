package com.blog.service.impl;


import com.blog.dao.UserGroupPoRepository;
import com.blog.dto.UserGroupDto;
import com.blog.exception.ValidateFailedException;
import com.blog.mapper.UserGroupPoMapper;
import com.blog.po.UserGroupPo;
import com.blog.service.UserGroupService;


import jakarta.mail.MethodNotSupportedException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserGroupServiceImpl implements UserGroupService {

    private final UserGroupPoRepository userGroupRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserGroupServiceImpl.class);

    private void validateUserGroup(UserGroupDto userGroupDto) throws ValidateFailedException {
        if (userGroupDto == null) {
            throw new ValidateFailedException(ValidateFailedException.DomainErrorStatus.RESOURCE_IS_EMPTY);
        }
        if (ObjectUtils.isEmpty(userGroupDto.getGroupName())) {
            throw new ValidateFailedException(ValidateFailedException.DomainErrorStatus.RESOURCE_IS_EMPTY);
        }
    }

    /**
     * 新增群組
     *
     * @param userGroupDto 群組資訊
     * @throws Exception 遭遇異常時拋出
     */
    @Override
    @Transactional
    public void save(UserGroupDto userGroupDto) throws Exception {
        if(userGroupDto == null){
            throw new IllegalArgumentException("使用者群組資訊不能為空");
        }
        UserGroupPo userGroupPo = UserGroupPoMapper.INSTANCE.toPo(userGroupDto);
        logger.debug("使用者群組資訊: {}", userGroupPo);
        userGroupRepository.saveAndFlush(userGroupPo);
        logger.debug("新增後使用者群組資訊: {}", userGroupPo);
    }

    /**
     * 更新群組
     *
     * @param userGroupDto 群組資訊
     * @throws Exception 遭遇異常時拋出
     */
    @Override
    @Transactional
    public void update(UserGroupDto userGroupDto) throws Exception {
        if(userGroupDto == null){
            throw new IllegalArgumentException("使用者群組資訊不能為空");
        }
        UserGroupPo userGroupPo = userGroupRepository.findById(userGroupDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("使用者群組不存在"));
        UserGroupPoMapper.INSTANCE.partialUpdate(userGroupDto, userGroupPo);
        logger.debug("更新後使用者群組資訊: {}", userGroupPo);
        userGroupRepository.saveAndFlush(userGroupPo);
        logger.debug("更新後使用者群組資訊: {}", userGroupPo);
    }

    /**
     * 刪除群組
     *
     * @param userGroupDto 群組資訊
     * @throws Exception 遭遇異常時拋出
     */
    @Override
    public void delete(UserGroupDto userGroupDto) throws Exception {
        throw new MethodNotSupportedException("該刪除方式目前不支援");
    }

    /**
     * 刪除群組
     *
     * @param id 群組序號
     * @throws Exception 遭遇異常時拋出
     */
    @Override
    @Transactional
    public void delete(Long id) throws Exception {
        if(id == null){
            throw new IllegalArgumentException("使用者群組id不能為空");
        }
        if(!userGroupRepository.existsById(id)){
            throw new EntityNotFoundException("使用者群組不存在");
        }
        if(userGroupRepository.countByUserGroupId(id) > 0){
            throw new ValidateFailedException("該使用者群組有使用者");
        }
        userGroupRepository.deleteById(id);
    }

    /**
     * 搜尋群組
     *
     * @param userGroupId 群組序號
     * @return UserGroupDto 群組資訊
     * @throws Exception 遭遇異常時拋出
     */
    @Override
    public UserGroupDto findById(Long userGroupId) throws EntityNotFoundException {
        if(userGroupId == null){
            throw new IllegalArgumentException("使用者群組id不能為空");
        }
        Optional<UserGroupPo> userGroupPo = userGroupRepository.findById(userGroupId);
        return userGroupPo.map(UserGroupPoMapper.INSTANCE::toDto).orElseThrow(() -> new EntityNotFoundException("使用者群組不存在"));
    }

    /**
     * 搜尋全部群組集合
     *
     * @return List<UserGroupDto> 所有群組資訊
     * @throws Exception 遭遇異常時拋出
     */
    @Override
    public List<UserGroupDto> findAll() throws Exception {
        return userGroupRepository.findAll().stream().map(UserGroupPoMapper.INSTANCE::toDto).toList();
    }

    /**
     * 搜尋 條件分頁群組
     *
     * @param page 當前頁數
     * @param pageSize 每頁筆數
     * @return Page<UserGroupDto> 條件分頁群組
     * @throws Exception 遭遇異常時拋出
     */
    @Override
    public Page<UserGroupDto> findAll(Integer page, Integer pageSize) throws Exception {
       Pageable pageable = PageRequest.of(page - 1, pageSize);
       return userGroupRepository.findAll(pageable).map(UserGroupPoMapper.INSTANCE::toDto);
    }

    /**
     * 搜尋 條件分頁群組
     *
     * @param page 當前頁數
     * @param pageSize 每頁筆數
     * @param groupName 群組名稱
     * @param reviewLevel 審核等級
     * @return Page<UserGroupDto> 條件分頁群組
     */
    @Override
    public Page<UserGroupDto> findAll(int page, int pageSize, String groupName, String reviewLevel) throws Exception {
        Specification<UserGroupPo> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(groupName)) {
                predicates.add(criteriaBuilder.like(root.get("groupName"), "%" + groupName + "%"));
            }
            if (StringUtils.hasText(reviewLevel)) {
                predicates.add(criteriaBuilder.equal(root.get("reviewLevel"), reviewLevel));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        return userGroupRepository.findAll(specification, pageable).map(UserGroupPoMapper.INSTANCE::toDto);
    }
}
