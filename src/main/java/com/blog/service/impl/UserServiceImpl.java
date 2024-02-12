package com.blog.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.blog.dao.*;
import com.blog.dto.*;
import com.blog.exception.ResourceNotFoundException;
import com.blog.exception.ValidateFailedException;
import com.blog.enumClass.UserRole;
import com.blog.exception.JwtDomainException;
import com.blog.jwt.JwtBlackListService;
import com.blog.mapper.*;
import com.blog.po.*;
import com.blog.service.UserGroupService;
import com.blog.service.UserService;
import com.blog.utils.FileUtils;
import com.blog.utils.JwtTokenUtil;
import com.blog.utils.SpringSecurityUtils;

import com.blog.utils.UUIDUtils;
import jakarta.annotation.Resource;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ObjectUtils;

import javax.naming.AuthenticationNotSupportedException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    @Resource
    private UserGroupPoRepository userGroupPoRepository;
    @Resource
    private UserJpaRepository userJpaRepository;
    @Resource
    private RolePoRepository rolePoRepository;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private AmazonS3ClientService amazonS3ClientService;

    @Override
    public UserDto findByUserId(Long userId) {
        var userPo = Optional.of(userJpaRepository.findByIdAndIsDeletedIsFalse(userId))
                .orElseThrow(() -> new UsernameNotFoundException("找不到使用者: " + userId));
        return userPo.map(UserPoMapper.INSTANCE::toDto).orElse(null);
    }

    @Override
    public UserDto createUser(UserDto userDto) throws AuthenticationNotSupportedException, ValidateFailedException {
        if(userJpaRepository.findByUserName(userDto.getUserName()).isPresent()){
            throw new ValidateFailedException(ValidateFailedException.DomainErrorStatus.RESOURCE_ALREADY_EXISTS);
        }
        if(userJpaRepository.findByEmail(userDto.getEmail()).isPresent()){
            throw new ValidateFailedException(ValidateFailedException.DomainErrorStatus.RESOURCE_ALREADY_EXISTS);
        }
        validateUser(userDto);
        // 密碼需進行先進行加密處理，Spring security 在驗證階段會使用passwordEncoder比對
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        //增加創建使用者名稱與時間
        userDto.setCreatUser(SpringSecurityUtils.getCurrentUser());
        userDto.setIsDeleted(false);
        UserPo userPo = UserPoMapper.INSTANCE.toPo(userDto);
        // 檢查db是否有此群組
        if(userGroupPoRepository.findByGroupName(userDto.getGroupName()).isPresent()){
            userPo.setUserGroupPo(userGroupPoRepository.findByGroupName(userDto.getGroupName()).get());
        }
        Set<RolePo> rolePoSet = new HashSet<>();
        if(userDto.getRolesName() != null){
            for (String roleName: userDto.getRolesName()) {
                rolePoRepository.findByName(roleName).ifPresent(rolePoSet::add);
            }
        }
        userPo.setRoles(rolePoSet);
        userPo = userJpaRepository.save(userPo);
        return UserPoMapper.INSTANCE.toDto(userPo);
    }

    @Override
    public UserDto updateUser(UserDto userDto) throws AuthenticationNotSupportedException {
        validateUser(userDto);
        userJpaRepository.findById(userDto.getId()).ifPresent(userPo -> {
            // 增加更新使用者與更新時
            userDto.setUpdateUser(SpringSecurityUtils.getCurrentUser());
            // 檢查db是否有此群組
            if(userGroupPoRepository.findByGroupName(userDto.getGroupName()).isPresent()){
                userPo.setUserGroupPo(userGroupPoRepository.findByGroupName(userDto.getGroupName()).get());
            }
            Set<RolePo> rolePoSet = new HashSet<>();
            if(userDto.getRolesName() != null){
                for (String roleName: userDto.getRolesName()) {
                    rolePoRepository.findByName(roleName).ifPresent(rolePoSet::add);
                }
            }
            userPo.setUserName(userDto.getUserName());
            userPo.setEmail(userDto.getEmail());
            userPo.setRoles(rolePoSet);
            userJpaRepository.save(userPo);
        });
        return UserPoMapper.INSTANCE.toDto(userJpaRepository.findById(userDto.getId()).orElse(null));
    }

    @Override
    public String deleteUser(Long id) {
        JSONObject jsonObject = new JSONObject();
        userJpaRepository.findById(id).ifPresent(userPo -> {
            userPo.setIsDeleted(true);
            userJpaRepository.save(userPo);
        });
        if(userJpaRepository.findByIdAndIsDeletedIsFalse(id).isPresent()){
            jsonObject.put("message", "刪除失敗");
            return jsonObject.toJSONString();
        }
        jsonObject.put("message", "刪除成功");
        return jsonObject.toJSONString();
    }

    @Override
    public UserDto findByUserName(String userName) {
        UserPo userPo = userJpaRepository.findByUserName(userName).orElse(null);
        if (userPo == null)
            return null;
        List<RolePo> rolePoList = rolePoRepository.findByUserId(userPo.getId());
        UserDto userDto = UserPoMapper.INSTANCE.toDto(userPo);
        if(rolePoList != null){
            Set<RoleDto> roleDtoSet = rolePoList.stream().map(RolePoMapper.INSTANCE::toDto).collect(Collectors.toSet());
            userDto.setRoles(roleDtoSet);
        }
        return userDto;
    }

    @Override
    public Page<UserDto> findBySpec(String userName, String userEmail, int page, int size, String sort, String direction) {
        Specification<UserPo> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if(!ObjectUtils.isEmpty(userName)){
                predicates.add(criteriaBuilder.equal(root.get("userName"), userName));
            }
            if(!ObjectUtils.isEmpty(userEmail)){
                predicates.add(criteriaBuilder.equal(root.get("email"), userEmail));
            }
            predicates.add(criteriaBuilder.equal(root.get("isDeleted"), false));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        Pageable pageable = PageRequest.of(page - 1, size, Sort.Direction.fromString(direction), sort);
        Page<UserPo> userPos = userJpaRepository.findAll(spec, pageable);
        return userPos.map(UserPoMapper.INSTANCE::toDto);
    }

    @Override
    public List<UserDto> findUsersByRoleId(Long id) {
        Optional<List<UserPo>> optional = userJpaRepository.findUsersByRoleId(id);
        return optional.map(userPos ->
                userPos.stream().map(UserPoMapper.INSTANCE::toDto).toList()).orElse(null);
    }

    @Override
    public String register(UserDto body) {
        JSONObject jsonObject = new JSONObject();
        if(userJpaRepository.findByUserName(body.getUserName()).isPresent()){
            throw new ValidateFailedException(ValidateFailedException.DomainErrorStatus.USER_ALREADY_EXISTS);
        }
        if(userJpaRepository.findByEmail(body.getEmail()).isPresent()){
            throw new ValidateFailedException(ValidateFailedException.DomainErrorStatus.EMAIL_ALREADY_EXISTS);
        }
        UserDto userDto = new UserDto();
        userDto.setUserName(body.getUserName());
        userDto.setCreatUser(SpringSecurityUtils.getCurrentUser());
        userDto.setPassword(passwordEncoder.encode(body.getPassword()));
        userDto.setIsDeleted(false);
        userDto.setEmail(body.getEmail());
        userDto.setCreatUser(userDto.getUserName());

        UserPo userPo = UserPoMapper.INSTANCE.toPo(userDto);
        UserGroupPo userGroupPo = null;
        // 設置使用者群組
        if(body.getUserGroupDto() == null){
            // 給予預設群組
            userGroupPo = userGroupPoRepository.findByGroupName(UserGroupPo.DEFAULT_GROUP_NAME).orElse(null);
            userPo.setUserGroupPo(userGroupPo);
        } else {
            userGroupPo = userGroupPoRepository.findByGroupName(body.getUserGroupDto().getGroupName()).orElse(null);
            if(userGroupPo == null){
                userGroupPo = UserGroupPoMapper.INSTANCE.toPo(body.getUserGroupDto());
                userGroupPo.setIsDeleted(false);
                userGroupPo = userGroupPoRepository.save(userGroupPo);
            }
            userPo.setUserGroupPo(userGroupPo);
        }

        // 設置權縣 如果請求帶過來的role 為null 預設給他user權限
        if(body.getRoles() == null){
            RolePo rolePo = rolePoRepository.findByName(UserRole.ROLE_USER.getRoleName()).orElse(null);
            if(rolePo != null){
                userPo.setRoles(new HashSet<>(Collections.singleton(rolePo)));
            }
        } else {
            List<RolePo> poList = new ArrayList<>();
            body.getRoles().forEach(roleDto -> {
                RolePo rolePo = rolePoRepository.findByName(roleDto.getRoleName()).orElse(null);
                if(rolePo != null){
                    poList.add(rolePo);
                }
            });
            userPo.setRoles(new HashSet<>(poList));
        }
        userPo = userJpaRepository.save(userPo);
        jsonObject.put("userName", userPo.getUserName());
        jsonObject.put("message", "註冊成功");
        return jsonObject.toJSONString();
    }

    @Override
    public String logout(String token) throws ValidateFailedException {
        if(null == token || token.isEmpty()){
            throw new ValidateFailedException(ValidateFailedException.DomainErrorStatus.JWT_AUTHENTICATION_TOKEN_EXPIRED, "令牌為空");
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", "登出成功");
        return jsonObject.toJSONString();
    }

    @Override
    public UserProfileDto updateUserProfile(UserProfileRequestBody userProfileRequestBody) throws IOException, ExecutionException, InterruptedException, TimeoutException {
        UserPo userPo = userJpaRepository.findByUserName(userProfileRequestBody.getName())
                .orElseThrow(() -> new UsernameNotFoundException("找不到使用者 " + userProfileRequestBody.getName()));
        File file = null;
        String fileName;
        if(null != userProfileRequestBody.getAvatar()){
            // 存儲到aws s3當中
            file = FileUtils.convertMultipartFileToFile(userProfileRequestBody.getAvatar());
            fileName = FileUtils.generateFileName(userProfileRequestBody.getAvatar());
            CompletableFuture<String> result = amazonS3ClientService.uploadFileToS3Bucket(fileName, file);
            if(!result.get().equals("文件上傳成功")){
                throw new IOException("文件上傳失敗");
            }
            userPo.setAvatarName(fileName);
        } else if (null != userPo.getAvatarName() && !userPo.getAvatarName().isEmpty()) {
            CompletableFuture<String> result = amazonS3ClientService.deleteFileFromS3Bucket(userPo.getAvatarName());
            if(!result.get().equals("文件删除成功")) {
                throw new IOException("文件删除失敗");
            }
            userPo.setAvatarName(null);
        }

        userPo.setUserName(userProfileRequestBody.getName());
        userPo.setEmail(userProfileRequestBody.getEmail());
        userPo.setNickName(userProfileRequestBody.getNickName());
        userPo.setAddress(userProfileRequestBody.getAddress());
        TemporalAccessor parse = dateTimeFormatter.parse(userProfileRequestBody.getBirthday());
        userPo.setBirthday(LocalDate.from(parse));
        userPo = userJpaRepository.save(userPo);

        UserProfileDto userProfileDto = new UserProfileDto();
        userProfileDto.setEmail(userPo.getEmail());
        userProfileDto.setUsername(userPo.getUserName());
        userProfileDto.setAddress(userPo.getAddress());
        userProfileDto.setNickname(userPo.getNickName());
        userProfileDto.setBirthday(userPo.getBirthday());
        if(null != file){
            userProfileDto.setAvatar(FileCopyUtils.copyToByteArray(file));
        }
        return userProfileDto;
    }

    @Override
    public UserProfileDto getUserProfile(String username) throws Exception {
        UserPo userPo = userJpaRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("找不到使用者 " + username));
        String avatarName = userPo.getAvatarName();
        UserProfileDto userProfileDto = new UserProfileDto();
        if(avatarName != null){
            try {
                InputStream inputStream = amazonS3ClientService.downloadFileFromS3Bucket(avatarName);
                if(null != inputStream) {
                    userProfileDto.setAvatar(FileCopyUtils.copyToByteArray(inputStream));
                }
            } catch (Exception e) {
                 log.error("沒有此檔案", e);
            }
        }
        userProfileDto.setEmail(userPo.getEmail());
        userProfileDto.setUsername(userPo.getUserName());
        userProfileDto.setPassword(userPo.getPassword());
        userProfileDto.setAddress(userPo.getAddress());
        userProfileDto.setNickname(userPo.getNickName());
        userProfileDto.setBirthday(userPo.getBirthday());
        return userProfileDto;
    }

    @Override
    public List<UserDto> findUsersByRoleName(long id) {
        List<UserPo> userPos = userJpaRepository.findUsersByRoleName(id);
        return userPos.stream().map(UserPoMapper.INSTANCE::toDto).toList();
    }

    private List<String> validateUserRole(List<RoleDto> roleSet) {
        var roleList = new ArrayList<String>();
        for (RoleDto roleDto : roleSet) {
            UserRole userRole = UserRole.fromString(roleDto.getRoleName());
            switch (userRole) {
                case ROLE_ADMIN:
                    roleList.add("ROLE_ADMIN");
                    break;
                case ROLE_USER:
                    roleList.add("ROLE_USER");
                    break;
                case ONLY_SEARCH:
                    roleList.add("ONLY_SEARCH");
                    break;
            }
        }
        return roleList;
    }

    private void validateUser(UserDto userDto) throws AuthenticationNotSupportedException{
        List<RoleDto> roleList = userDto.getRoles().stream().toList();
        List<String> userRoleList = validateUserRole(roleList);
        if(CollectionUtils.isEmpty(userRoleList)){
            throw new AuthenticationNotSupportedException("此名使用者沒有設置權限，需設置權限");
        }
        // 判斷是否建立群組
        if(userGroupPoRepository.findByGroupName(userDto.getUserGroupDto().getGroupName()).isEmpty()){
            throw new AuthenticationNotSupportedException("此名使用者沒有關聯的群組，請先建立關聯群組");
        }
    }
}
