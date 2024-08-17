package com.blog.service.impl;

import com.blog.dao.*;

import com.blog.dto.UserDto;
import com.blog.dto.UserProfileDto;
import com.blog.exception.ValidateFailedException;
import com.blog.mapper.UserGroupPoMapper;
import com.blog.mapper.UserPoMapper;
import com.blog.po.RolePo;
import com.blog.po.UserGroupPo;
import com.blog.po.UserPo;
import com.blog.service.UserService;

import com.blog.utils.SpringSecurityUtil;
import jakarta.mail.MethodNotSupportedException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import org.springframework.data.domain.Page;


import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserGroupPoRepository userGroupPoRepository;
    private final UserPoRepository userJpaRepository;
    private final RolePoRepository rolePoRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);


    /**
     * 新增使用者
     *
     * @param userDto 使用者資訊
     * @throws Exception 遇到異常時拋出
     */
    @Override
    @Transactional
    public void save(UserDto userDto) throws Exception {
        logger.debug("新增使用者資訊: {}", userDto);
        UserPo userPo = UserPoMapper.INSTANCE.toPo(userDto);
        // 建立群組關聯
        UserGroupPo userGroupPo = userGroupPoRepository.findById(userDto.getGroupId()).orElseThrow(() -> new EntityNotFoundException("找不到群組資訊"));
        userPo.setUserGroupPo(userGroupPo);
        //建立角色關聯
        Set<RolePo> rolePos = new HashSet<>();
        userDto.getRoleIds().forEach(roleId -> {
            RolePo rolePo = rolePoRepository.findById(roleId).orElseThrow(() -> new EntityNotFoundException("找不到角色資訊"));
            rolePos.add(rolePo);
        });
        userPo.setRoles(rolePos);
        //設定密碼
        userPo.setPassword(passwordEncoder.encode(userDto.getPassword()));
        userJpaRepository.saveAndFlush(userPo);
        logger.debug("新增使用者 " + userPo.getUserName() + " 成功");
    }

    /**
     * 更新使用者
     *
     * @param userDto 使用者資訊
     * @throws Exception 遇到異常時拋出
     */
    @Override
    @Transactional
    public void update(UserDto userDto) throws Exception {
        logger.debug("更新使用者資訊: {}", userDto);
        UserPo userPo = userJpaRepository.findById(userDto.getId()).orElseThrow(() -> new EntityNotFoundException("找不到使用者 " + userDto.getId()));
        // 更新使用者資訊
        userPo = UserPoMapper.INSTANCE.partialUpdate(userDto, userPo);
        // 建立群組關聯
        UserGroupPo userGroupPo = userGroupPoRepository.findById(userDto.getGroupId()).orElseThrow(() -> new EntityNotFoundException("找不到群組資訊"));
        userPo.setUserGroupPo(userGroupPo);
        //建立角色關聯
        Set<RolePo> rolePos = new HashSet<>();
        userDto.getRoleIds().forEach(roleId -> {
            RolePo rolePo = rolePoRepository.findById(roleId).orElseThrow(() -> new EntityNotFoundException("找不到角色資訊"));
            rolePos.add(rolePo);
        });
        userPo.setRoles(rolePos);
        //設定密碼
        userPo.setPassword(passwordEncoder.encode(userDto.getPassword()));
        userJpaRepository.saveAndFlush(userPo);
        logger.debug("更新使用者 " + userPo.getUserName() + " 成功");
    }

    /**
     * 刪除使用者
     *
     * @param userDto 使用者資訊
     * @throws Exception 遇到異常時拋出
     */
    @Override
    public void delete(UserDto userDto) throws Exception {
        throw new MethodNotSupportedException("該刪除使用者方法不支援");
    }

    /**
     * 刪除使用者
     *
     * @param id  使用者序號
     * @throws Exception 遇到異常時拋出
     */
    @Override
    @Transactional
    public void delete(Long id) throws Exception {
        logger.debug("刪除使用者序號: {}", id);
        if(id == null) {
            throw new IllegalArgumentException("刪除使用者資訊不得為空");
        }
        if(!userJpaRepository.existsById(id)) {
            throw new EntityNotFoundException("找不到使用者id " + id + "的資訊");
        }
        userJpaRepository.deleteById(id);
    }

    /**
     * 搜尋指定id的使用者資訊
     *
     * @param id 使用者序號
     * @return UserDto 使用者資訊
     * @throws EntityNotFoundException 異常拋出
     */
    @Override
    public UserDto findById(Long id) throws EntityNotFoundException {
        logger.debug("查詢使用者序號: {}", id);
        if(id == null) {
            throw new IllegalArgumentException("查詢使用者資訊不得為空");
        }
        UserPo userPo = userJpaRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("找不到使用者 " + id));
        return UserPoMapper.INSTANCE.toDto(userPo);
    }

    /**
     * 搜尋 所有使用者
     *
     * @return List<UserDto> 使用者資訊
     * @throws Exception 異常拋出
     */
    @Override
    public List<UserDto> findAll() throws Exception {
        logger.debug("查詢所有使用者");
        List<UserPo> userPos = userJpaRepository.findAll();
        return UserPoMapper.INSTANCE.toDtoList(userPos);
    }

    /**
     * 搜尋所有的分頁集合
     *
     * @param page  當前頁數
     * @param pageSize 每頁筆數
     * @return Page<UserDto> 條件分頁使用者集合
     * @throws Exception 遭遇異常時拋出
     */
    @Override
    public Page<UserDto> findAll(Integer page, Integer pageSize) throws Exception {
        logger.debug("查詢所有使用者");
        Page<UserPo> userPos = userJpaRepository.findAll(PageRequest.of(page, pageSize));
        return userPos.map(UserPoMapper.INSTANCE::toDto);
    }


    /**
     * 鎖定帳戶
     *
     * @param id 使用者序號
     */
    @Override
    @Transactional
    public void lock(long id){
        UserPo userPo = userJpaRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("查無此使用者"));
        userPo.setLocked(true);
        userJpaRepository.saveAndFlush(userPo);
    }

    /**
     * 解鎖使用者
     *
     * @param id 使用者序號
     */
    @Override
    @Transactional
    public void unlock(long id){
        UserPo userPo = userJpaRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("查無此使用者"));
        userPo.setLocked(false);
        userJpaRepository.saveAndFlush(userPo);
    }

    /**
     * 更新個人資訊
     *
     * @param userProfileDto 個人資訊
     */
    @Override
    @Transactional
    public void updateProfile(UserProfileDto userProfileDto){
        UserPo userPo = userJpaRepository.findById(userProfileDto.getId()).orElseThrow(() -> new EntityNotFoundException("查無此使用者"));
        BeanUtils.copyProperties(userProfileDto, userPo, "id", "createUser", "createDate");
        userJpaRepository.saveAndFlush(userPo);
    }

    /**
     * 搜尋個人資訊
     *
     * @param username 使用者名稱
     * @return UserProfileDto 使用者資訊
     */
    @Override
    public UserProfileDto queryProfile(String username) {
        UserPo userPo = userJpaRepository.findByUserName(username).orElseThrow(() -> new EntityNotFoundException("查無此使用者"));
        UserProfileDto userProfileDto = new UserProfileDto();
        BeanUtils.copyProperties(userPo, userProfileDto, "id", "createUser", "createDate");
        return userProfileDto;
    }

    /**
     * 更新使用者密碼
     *
     * @param oldPassword 舊密碼
     * @param newPassword 新密碼
     */
    @Override
    public void changePassword(String oldPassword, String newPassword) {
        UserPo userPo = userJpaRepository.findByUserName(SpringSecurityUtil.getCurrentUser()).orElseThrow(() -> new EntityNotFoundException("查無此使用者"));
        if(!userPo.getPassword().equals(passwordEncoder.encode(oldPassword))) {
            throw new ValidateFailedException("舊密碼錯誤");
        }
        userPo.setPassword(passwordEncoder.encode(newPassword));
        userJpaRepository.saveAndFlush(userPo);
    }

    /**
     * 搜尋指定名稱使用者
     *
     * @param key 使用者名稱
     * @return UserDto 使用者資訊
     */
    @Override
    public UserDto findByName(String key) {
        UserPo userPo = userJpaRepository.findByUserName(key).orElseThrow(() -> new EntityNotFoundException("查無此使用者"));
        return UserPoMapper.INSTANCE.toDto(userPo);
    }

    /**
     * 搜尋符合條件的分頁集合
     *
     * @param page 當前頁數
     * @param pageSize 每頁筆數
     * @param userName 使用者名稱
     * @param userEmail 使用者信箱
     * @return Page<UserDto> 條件分頁使用者集合
     */
    @Override
    public Page<UserDto> findAll(Integer page, Integer pageSize, String userName, String userEmail) {
        // 實現動態查詢
        Specification<UserPo> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if(StringUtils.hasText(userName)) {
                predicates.add(criteriaBuilder.like(root.get("userName"), "%" + userName + "%"));
            }
            if(StringUtils.hasText(userEmail)) {
                predicates.add(criteriaBuilder.like(root.get("email"), "%" + userEmail + "%"));
            }
            return query.where(predicates.toArray(new Predicate[0])).getRestriction();
        };
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        return userJpaRepository.findAll(specification, pageable).map(UserPoMapper.INSTANCE::toDto);
    }
}
