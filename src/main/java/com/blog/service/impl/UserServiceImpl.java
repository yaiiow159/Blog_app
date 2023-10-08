package com.blog.service.impl;

import com.blog.dao.*;
import com.blog.dto.*;
import com.blog.exception.ResourceNotFoundException;
import com.blog.exception.ValidateFailedException;
import com.blog.enumClass.UserRole;
import com.blog.exception.JwtDomainException;
import com.blog.jwt.JwtBlackListService;
import com.blog.mapper.*;
import com.blog.po.CommentPo;
import com.blog.po.PostPo;
import com.blog.po.RolePo;
import com.blog.po.UserPo;
import com.blog.service.UserService;
import com.blog.utils.JwtTokenUtil;
import com.blog.utils.LoginUtils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import javax.naming.AuthenticationNotSupportedException;
import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class,isolation = org.springframework.transaction.annotation.Isolation.REPEATABLE_READ)
public class UserServiceImpl implements UserService {
    @Resource
    private UserGroupPoRepository userGroupPoRepository;
    @Resource
    private UserJpaRepository userJpaRepository;
    @Resource
    private RolePoRepository rolePoRepository;

    @Resource
    private CommentPoRepository commentPoRepository;

    @Resource
    private JwtBlackListService jwtBlackListService;

    @Resource
    private JwtTokenUtil jwtTokenUtil;

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final PostPoRepository postPoRepository;

    public UserServiceImpl(PostPoRepository postPoRepository) {
        this.postPoRepository = postPoRepository;
    }

    @Override
    public UserDto findByUserId(Long userId) {
        var userPo = Optional.of(userJpaRepository.findByIdAndIsDeletedIsFalse(userId))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with userId: " + userId));
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
        userDto.setCreatUser(LoginUtils.getCurrentUser());
        userDto.setCreateDate(LocalDateTime.now(ZoneId.of("Asia/Taipei")));
        userDto.setIsDeleted(false);
        UserPo userPo = UserPoMapper.INSTANCE.toPo(userDto);
        // 檢查db是否有此群組
        userGroupPoRepository.findByGroupName(userDto.getUserGroupDto().getGroupName()).ifPresent(
                userPo::setUserGroupPo);
        Set<RolePo> rolePoSet = new HashSet<>();
        // 先從db撈取role的數據
        for (RoleDto roleDto: userDto.getRoles()) {
            rolePoRepository.findByName(roleDto.getRoleName()).ifPresent(rolePoSet::add);
        }
        userPo.setRoles(rolePoSet);
        userPo = userJpaRepository.save(userPo);
        return UserPoMapper.INSTANCE.toDto(userPo);
    }

    @Override
    public UserDto updateUser(UserDto userDto) throws AuthenticationNotSupportedException {
        // 判斷密碼是否有改動過
        if(userDto.getPassword() != null)
            userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        validateUser(userDto);
        userJpaRepository.findById(userDto.getId()).ifPresent(userPo -> {
            // 增加更新使用者與更新時間
            Set<RolePo> rolePoSet = new HashSet<>();
            userDto.setUpdateUser(LoginUtils.getCurrentUser());
            userDto.setUpdDate(LocalDateTime.now(ZoneId.of("Asia/Taipei")));
            if(userGroupPoRepository.findByGroupName(userDto.getUserGroupDto().getGroupName()).isPresent()){
                userPo.setUserGroupPo(userGroupPoRepository.findByGroupName(userDto.getUserGroupDto().getGroupName()).get());
            }
            for (RoleDto roleDto: userDto.getRoles()) {
                rolePoRepository.findByName(roleDto.getRoleName()).ifPresent(rolePoSet::add);
            }
            userPo.setUserName(userDto.getUserName());
            userPo.setPassword(userDto.getPassword());
            userPo.setEmail(userDto.getEmail());
            userPo.setRoles(rolePoSet);
            userJpaRepository.save(userPo);
        });
        return UserPoMapper.INSTANCE.toDto(userJpaRepository.findById(userDto.getId()).orElse(null));
    }

    @Override
    public String deleteUser(Long id) {
        userJpaRepository.findById(id).ifPresent(userPo -> {
            userPo.setIsDeleted(true);
            userJpaRepository.save(userPo);
        });
        if(userJpaRepository.findByIdAndIsDeletedIsFalse(id).isPresent()){
            return "fail";
        }
        return "success";
    }

    public UserDto findByUserName(String userName) {
        UserPo userPo = userJpaRepository.findByUserName(userName).orElse(null);
        if (userPo == null)
            return null;
        return UserPoMapper.INSTANCE.toDto(userPo);
    }

    @Override
    public Page<UserDto> findBySpec(Long id, String userName, String userEmail, int page, int size, String sort, String direction) {
        Specification<UserPo> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if(!ObjectUtils.isEmpty(id)){
                predicates.add(criteriaBuilder.equal(root.get("id"), id));
            }
            if(!ObjectUtils.isEmpty(userName)){
                predicates.add(criteriaBuilder.equal(root.get("userName"), userName));
            }
            if(!ObjectUtils.isEmpty(userEmail)){
                predicates.add(criteriaBuilder.equal(root.get("email"), userEmail));
            }
            predicates.add(criteriaBuilder.equal(root.get("isDeleted"), false));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        return userJpaRepository.findAll(spec, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction.toLowerCase()), sort)))
                .map(UserPoMapper.INSTANCE::toDto);
    }

    @Override
    public List<UserDto> findUsersByRoleId(Long id) {
        Optional<List<UserPo>> optional = userJpaRepository.findUsersByRoleId(id);
        return optional.map(userPos -> userPos.stream().map(UserPoMapper.INSTANCE::toDto).toList()).orElse(null);
    }

    @Override
    public UserDto register(UserDto body) {
        if(userJpaRepository.findByUserName(body.getUserName()).isPresent()){
            throw new JwtDomainException(HttpStatus.BAD_REQUEST, "此名使用者已存在");
        }
        if(userJpaRepository.findByEmail(body.getEmail()).isPresent()){
            throw new JwtDomainException(HttpStatus.BAD_REQUEST, "此電子郵件已存在");
        }
        UserDto userDto = new UserDto();
        userDto.setUserName(body.getUserName());
        userDto.setCreatUser(LoginUtils.getCurrentUser());
        userDto.setCreateDate(LocalDateTime.now());
        userDto.setPassword(passwordEncoder.encode(body.getPassword()));
        userDto.setIsDeleted(false);
        userDto.setEmail(body.getEmail());
        RolePo rolePo = rolePoRepository.findByName(UserRole.ROLE_USER.getRoleName()).orElse(null);

        UserPo userPo = UserPoMapper.INSTANCE.toPo(userDto);
        userPo.setUserGroupPo(UserGroupPoMapper.INSTANCE.toPo(body.getUserGroupDto()));
        if(rolePo != null){
            userPo.setRoles(new HashSet<>(Collections.singleton(rolePo)));
        } else {
            List<RolePo> poList = RolePoMapper.INSTANCE.toPoList(body.getRoles().stream().toList());
            userPo.setRoles(new HashSet<>(poList));
        }
        userPo = userJpaRepository.save(userPo);
        // 然後保存UserPo
        return UserPoMapper.INSTANCE.toDto(userPo);
    }

    @Override
    public UserProfileDto findUserProfileByUserNameOrEmail(UserProfileRequestBody userProfileRequestBody) throws ResourceNotFoundException {
        UserProfileDto userProfileDto = new UserProfileDto();
        UserPo userPo = userJpaRepository.findByUserName(userProfileRequestBody.getName()).orElseThrow(() -> new UsernameNotFoundException("User not found with name: " + userProfileRequestBody.getName()));
        List<PostPo> list = postPoRepository.findByAuthorNameOrAuthorEmail(userProfileRequestBody.getName(), userProfileRequestBody.getEmail());
        if(CollectionUtils.isEmpty(list)){
            throw new ResourceNotFoundException();
        }
        List<CommentPo> commentPoList = commentPoRepository.findByNameOrEmail(userProfileRequestBody.getName(), userProfileRequestBody.getEmail());
        if(CollectionUtils.isEmpty(commentPoList)){
            throw new ResourceNotFoundException();
        }
        userProfileDto.setPostDtoList(PostPoMapper.INSTANCE.toDtoList(list));
        userProfileDto.setUserDto(UserPoMapper.INSTANCE.toDto(userPo));
        userProfileDto.setCommentDtoList(CommentPoMapper.INSTANCE.toDtoList(commentPoList));

        return userProfileDto;
    }

    @Override
    public String logout(String token) throws AuthenticationNotSupportedException {
        jwtBlackListService.addJwtToBlackList(token);
        //SecurityContextHolder.clearContext();
        return "logout successful";
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
