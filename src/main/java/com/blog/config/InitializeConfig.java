package com.blog.config;

import com.blog.dao.RolePoRepository;
import com.blog.dao.UserGroupPoRepository;
import com.blog.dao.UserPoRepository;
import com.blog.enumClass.GroupAuthEnum;
import com.blog.enumClass.UserRoleEnum;
import com.blog.po.RolePo;
import com.blog.po.UserGroupPo;
import com.blog.po.UserPo;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Configuration
@RequiredArgsConstructor
public class InitializeConfig {

    private final Logger logger = LoggerFactory.getLogger(InitializeConfig.class);

    private final UserPoRepository userJpaRepository;
    private final UserGroupPoRepository userGroupPoRepository;
    private final RolePoRepository rolePoRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    @Transactional
    public void initAdministrator(){
        if(userJpaRepository.findByUserName("admin").isEmpty()) {
            logger.info("初始化管理員....");
            List<RolePo> rolePoList = createRoles();
            UserGroupPo groupPo = createUserGroup();
            createUser(rolePoList, groupPo);
            logger.info("初始化管理員完成");
        }
    }

    private List<RolePo> createRoles() {
        RolePo roleAdmin = createRole(UserRoleEnum.ROLE_ADMIN.getRoleName());
        RolePo roleUser = createRole(UserRoleEnum.ROLE_USER.getRoleName());
        RolePo roleSearch = createRole(UserRoleEnum.SEARCH.getRoleName());
        return rolePoRepository.saveAllAndFlush(Arrays.asList(roleAdmin, roleUser, roleSearch));
    }

    private RolePo createRole(String roleName) {
        RolePo role = new RolePo();
        role.setRoleName(roleName);
        role.setCreatUser("admin");
        role.setUpdateUser("admin");
        return role;
    }

    private UserGroupPo createUserGroup() {
        UserGroupPo userGroupPo = new UserGroupPo();
        userGroupPo.setGroupName("admin");
        userGroupPo.setDescription("管理員群組");
        userGroupPo.setCreatUser("admin");
        userGroupPo.setReviewLevel(GroupAuthEnum.ADMIN.getStatus());
        return userGroupPoRepository.saveAndFlush(userGroupPo);
    }

    private void createUser(List<RolePo> rolePoList, UserGroupPo groupPo) {
        UserPo userPo = new UserPo();
        userPo.setUserName("admin");
        userPo.setEmail("admin123@gmail.com");
        userPo.setPassword(passwordEncoder.encode("admin123"));
        userPo.setNickName("admin");
        userPo.setRoles(new HashSet<>(rolePoList));
        userPo.setUserGroupPo(groupPo);
        userJpaRepository.saveAndFlush(userPo);
    }

}
