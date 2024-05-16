package com.blog.service.impl;

import com.blog.dao.*;
import com.blog.dto.*;
import com.blog.exception.ValidateFailedException;
import com.blog.enumClass.UserRole;
import com.blog.mapper.*;
import com.blog.po.*;
import com.blog.service.GoogleStorageService;
import com.blog.service.UserService;
import com.blog.utils.SpringSecurityUtil;

import com.blog.utils.ThreadLocalUtil;
import jakarta.annotation.Resource;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.naming.AuthenticationNotSupportedException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private UserGroupPoRepository userGroupPoRepository;
    @Resource
    private UserPoRepository userJpaRepository;
    @Resource
    private RolePoRepository rolePoRepository;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private GoogleStorageService googleStorageService;

    @Resource
    @Lazy
    private UserDetailsService userDetailsService;

    @Override
    public UserDto findByUserId(Long userId) {
        var optionalUserPo  = Optional.of(userJpaRepository.findById(userId))
                .orElseThrow(() -> new UsernameNotFoundException("找不到使用者: " + userId));
        UserDto userDto = optionalUserPo.map(UserPoMapper.INSTANCE::toDto).orElse(null);
        assert userDto != null;
        userDto.setUserGroupDto(UserGroupPoMapper.INSTANCE.toDto(optionalUserPo.get().getUserGroupPo()));
        return userDto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class,isolation = Isolation.REPEATABLE_READ)
    public String lockUser(Long userId) {
        UserPo userPo = userJpaRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("找不到使用者序號: " + userId));
        userPo.setLocked(true);
        userJpaRepository.saveAndFlush(userPo);
        return "鎖戶成功";
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        String username = SpringSecurityUtil.getCurrentUser();
        userJpaRepository.changePassword(passwordEncoder.encode(newPassword), username);
        // 查詢新使用者密碼
        userJpaRepository.findByUserName(username).ifPresent(userPo -> {
            // 更新spring-security的數據
            var userDetails = userDetailsService.loadUserByUsername(username);
            var usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null,userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        });
    }

    @Override
    public Page<UserDto> findAll(String userName, String userEmail, int page, int pageSize) {
        Specification<UserPo> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> list = new ArrayList<>();
            if (!ObjectUtils.isEmpty(userName)) {
                Predicate predicate = criteriaBuilder.like(root.get("userName"), "%" + userName + "%");
                list.add(predicate);
            }
            if (!ObjectUtils.isEmpty(userEmail)) {
                Predicate predicate = criteriaBuilder.like(root.get("email"), "%" + userEmail + "%");
                list.add(predicate);
            }
            Predicate[] p = new Predicate[list.size()];
            return criteriaBuilder.and(list.toArray(p));
        };
        Pageable pageable = PageRequest.of(page - 1 , pageSize);
        return userJpaRepository.findAll(specification, pageable).map(UserPoMapper.INSTANCE::toDto).map(
                userDto -> {
                    userDto.setRoleNames(userDto.getRoles()
                            .stream()
                            .map(RoleDto::getRoleName).collect(Collectors.toSet()));
                    return userDto;
                }
        ).map(userDto -> {
            userDto.setGroupName(UserGroupPoMapper.INSTANCE.toDto
                    (userGroupPoRepository.findByUserPoListContaining(userDto.getId())).getGroupName());
            return userDto;
        });
    }

    @Override
    public void upload(MultipartFile file) throws IOException, ExecutionException, InterruptedException {
        String imageName = generateImageName(file);
        CompletableFuture<String> result = googleStorageService.uploadFile(file, imageName);
        if(result.get() == null) {
            throw new IOException("上傳圖片失敗");
        }
        userJpaRepository.updateImageName(result.get(), SpringSecurityUtil.getCurrentUser());
    }

    public String unlockUser(Long userId) {
        return userJpaRepository.findById(userId)
                .map(userPo -> {
                    userPo.setLocked(true);
                    userJpaRepository.saveAndFlush(userPo);
                    return "解鎖成功";
                })
                .orElseThrow(() -> new UsernameNotFoundException("找不到使用者: " + userId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.REPEATABLE_READ)
    public void add(UserDto userDto) throws AuthenticationNotSupportedException, ValidateFailedException {
        if (userJpaRepository.findByUserName(userDto.getUserName()).isPresent()) {
            throw new ValidateFailedException(ValidateFailedException.DomainErrorStatus.RESOURCE_ALREADY_EXISTS);
        }
        if (userJpaRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new ValidateFailedException(ValidateFailedException.DomainErrorStatus.RESOURCE_ALREADY_EXISTS);
        }
        validateUser(userDto);
        // 密碼需進行先進行加密處理，Spring security 在驗證階段會使用passwordEncoder比對
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        //增加創建使用者名稱與時間
        UserPo userPo = UserPoMapper.INSTANCE.toPo(userDto);
        userPo.setCreatUser(SpringSecurityUtil.getCurrentUser());
        userPo.setCreateDate(LocalDateTime.now(ZoneId.of("Asia/Taipei")));
        userPo.setUserGroupPo(userGroupPoRepository.findById(userDto.getGroupId()).orElseThrow(() -> new ValidateFailedException("找不到群組")));
        Set<RolePo> rolePoList = new HashSet<>(rolePoRepository.findAllById(userDto.getRoleIds()));
        userPo.setRoles(CollectionUtils.isEmpty(rolePoList) ? new HashSet<>() : rolePoList);
        userJpaRepository.saveAndFlush(userPo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.REPEATABLE_READ)
    public void edit(UserDto userDto) throws AuthenticationNotSupportedException {
        validateUser(userDto);
        userJpaRepository.findById(userDto.getId()).ifPresent(userPo -> {
            userPo = UserPoMapper.INSTANCE.partialUpdate(userDto, userPo);
            userPo.setPassword(passwordEncoder.encode(userDto.getPassword()));
            userPo.setUpdateUser(SpringSecurityUtil.getCurrentUser());
            userPo.setUpdDate(LocalDateTime.now(ZoneId.of("Asia/Taipei")));
            userPo.setUserGroupPo(userGroupPoRepository.findById(userDto.getGroupId()).orElseThrow(() -> new ValidateFailedException("找不到群組")));
            rolePoRepository.findAll().forEach(userPo.getRoles()::remove);
            rolePoRepository.findAllById(userDto.getRoleIds()).forEach(userPo.getRoles()::add);
            userJpaRepository.saveAndFlush(userPo);
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String delete(Long id) {
        userJpaRepository.deleteById(id);
        return "刪除成功";
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.REPEATABLE_READ)
    public UserDto findByUserName(String userName) {
        UserPo userPo = userJpaRepository.findByUserName(userName).orElse(null);
        return UserPoMapper.INSTANCE.toDto(userPo);
    }
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.REPEATABLE_READ)
    public String register(UserDto userDto) {
        if (userJpaRepository.findByUserName(userDto.getUserName()).isPresent()) {
            throw new ValidateFailedException(ValidateFailedException.DomainErrorStatus.USER_ALREADY_EXISTS);
        }
        if (userJpaRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new ValidateFailedException(ValidateFailedException.DomainErrorStatus.EMAIL_ALREADY_EXISTS);
        }
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        userDto.setCreatUser(SpringSecurityUtil.getCurrentUser());
        userDto.setCreateDate(LocalDateTime.now(ZoneId.of("Asia/Taipei")));
        UserPo userPo = UserPoMapper.INSTANCE.toPo(userDto);
        // 設置使用者群組
        if (userGroupPoRepository.findByGroupName(userDto.getGroupName()).isPresent()) {
            userGroupPoRepository.findByGroupName(userDto.getGroupName()).ifPresent(
                    userPo::setUserGroupPo
            );
        } else {
            userGroupPoRepository.findByGroupName(UserGroupPo.DEFAULT_GROUP_NAME).ifPresent(
                    userPo::setUserGroupPo
            );
        }
        Set<RolePo> rolePoSet = new HashSet<>();
        rolePoRepository.findByRoleName(UserRole.ROLE_USER.toString()).ifPresent(rolePoSet::add);
        userPo.setRoles(rolePoSet);
        userJpaRepository.saveAndFlush(userPo);
        return "註冊成功";
    }

    @Override
    public void logout (String token) throws ValidateFailedException {
        if (null == token || token.isEmpty()) {
            throw new ValidateFailedException(
                    ValidateFailedException.DomainErrorStatus.JWT_AUTHENTICATION_TOKEN_EXPIRED, "令牌為空");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.REPEATABLE_READ)
    public UserProfileDto updateUserProfile (UserProfileDto userProfileDto)  {
        UserPo userPo = userJpaRepository.findByUserName(userProfileDto.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("找不到使用者 " + userProfileDto.getUsername()));

        userPo.setUserName(userProfileDto.getUsername());
        userPo.setEmail(userProfileDto.getEmail());
        userPo.setNickName(userProfileDto.getNickname());
        userPo.setAddress(userProfileDto.getAddress());
        userPo = userJpaRepository.saveAndFlush(userPo);
        // 從s3 取回圖片
        return getUserProfile(userPo.getUserName());
    }

    @Override
    public UserProfileDto getUserProfile (String username) {
        UserPo userPo = userJpaRepository.findByUserName(username)
                .orElseThrow(() -> new ValidateFailedException("找不到使用者 " + username));
        UserProfileDto userProfileDto = new UserProfileDto();
        userProfileDto.setEmail(userPo.getEmail());
        userProfileDto.setUsername(userPo.getUserName());
        userProfileDto.setPassword(userPo.getPassword());
        userProfileDto.setAddress(userPo.getAddress());
        userProfileDto.setNickname(userPo.getNickName());
        userProfileDto.setAvatarName(userPo.getAvatarName());
        userProfileDto.setAvatarPath(userPo.getAvatarPath());
        return userProfileDto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.REPEATABLE_READ)
    public List<UserDto> findUsersByRoleName (long id){
        List<UserPo> userPos = userJpaRepository.findUsersByRoleName(id);
        return userPos.stream().map(UserPoMapper.INSTANCE::toDto).toList();
    }

    private void validateUser (UserDto userDto) throws AuthenticationNotSupportedException {
        if (userDto.getGroupId() == 0) {
            throw new AuthenticationNotSupportedException("此名使用者沒有關聯的群組");
        }
        if(userDto.getRoleIds().isEmpty()) {
            throw new AuthenticationNotSupportedException("使用者沒有關聯的角色");
        }
        // 判斷是否建立群組
        if (userGroupPoRepository.findById(userDto.getGroupId()).isEmpty()) {
            throw new AuthenticationNotSupportedException("此名使用者沒有關聯的群組，請先建立關聯群組");
        }
    }

    private String generateImageName(MultipartFile imgFile) {
        String originalFilename = imgFile.getOriginalFilename();
        String extension = "";
        if (Objects.requireNonNull(originalFilename).lastIndexOf(".") > -1) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return UUID.randomUUID() + extension;
    }

}
