package com.blog.config;

import com.blog.dto.RoleDto;
import com.blog.dto.UserDto;
import com.blog.dto.UserGroupDto;
import com.blog.enumClass.ReviewLevel;
import com.blog.enumClass.UserRole;
import com.blog.po.UserGroupPo;
import com.blog.service.RoleService;
import com.blog.service.UserGroupService;
import com.blog.service.UserService;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Configuration
public class InitializeConfig {
    @Resource
    private UserService userService;
    @Resource
    private RoleService roleService;
    @Resource
    private UserGroupService userGroupService;
    @PostConstruct
    public void init() throws Exception {
            UserGroupDto userGroupDto = null;
            userGroupDto = new UserGroupDto();
            // 初始化群組
            if(userGroupService.findByGroupName("group1") == null){
                userGroupDto.setGroupName("group1");
                userGroupDto.setDescription("群組1");
                userGroupDto.setCreatUser("admin");
                userGroupDto.setReviewLevel(ReviewLevel.ADMIN.getRoleName());
            } else {
                userGroupDto = userGroupService.findByGroupName(UserGroupPo.DEFAULT_GROUP_NAME);
            }
            // 初始化使用者與其權限
            if(userService.findByUserName(UserService.DEFAULT_USER_NAME) == null){
            RoleDto roleDto = new RoleDto();
            RoleDto roleDto1 = new RoleDto();
            RoleDto roleDto2 = new RoleDto();

            if(roleService.findByRoleName(UserRole.ROLE_ADMIN.getRoleName()) == null){
                roleDto.setRoleName(UserRole.ROLE_ADMIN.getRoleName());
                roleDto.setCreatUser("admin");
            } else {
                roleDto = roleService.findByRoleName(UserRole.ROLE_ADMIN.getRoleName());
            }

            if(roleService.findByRoleName(UserRole.ROLE_USER.getRoleName()) == null){
                roleDto1.setRoleName(UserRole.ROLE_USER.getRoleName());
                roleDto1.setCreatUser("admin");
            } else {
                roleDto1 = roleService.findByRoleName(UserRole.ROLE_USER.getRoleName());
            }

            if(roleService.findByRoleName(UserRole.ONLY_SEARCH.getRoleName()) == null){
                roleDto2.setRoleName(UserRole.ONLY_SEARCH.getRoleName());
                roleDto2.setCreatUser("admin");
            } else {
                roleDto2 = roleService.findByRoleName(UserRole.ONLY_SEARCH.getRoleName());
            }

            List<RoleDto> roles = Arrays.asList(roleDto, roleDto1, roleDto2);

            UserDto user = new UserDto();
            user.setUserName("admin");
            user.setPassword("admin1234");
            user.setCreatUser("admin");
            user.setEmail("examyou076@gmail.com");
            user.setUserGroupDto(userGroupDto);
            user.setRoles(new HashSet<>(roles));
            userService.register(user);
        }
    }

}
