package com.blog.config;

import com.blog.dao.RolePoRepository;
import com.blog.dao.UserGroupPoRepository;
import com.blog.dao.UserPoRepository;
import com.blog.enumClass.GroupAuthEnum;
import com.blog.enumClass.UserRoleEnum;
import com.blog.exception.ValidateFailedException;
import com.blog.po.RolePo;
import com.blog.po.UserGroupPo;
import com.blog.po.UserPo;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class InitializeConfig {

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${admin.email}")
    private String adminEmail;

    private final Logger logger = LoggerFactory.getLogger(InitializeConfig.class);

    private final UserPoRepository userJpaRepository;
    private final UserGroupPoRepository userGroupPoRepository;
    private final RolePoRepository rolePoRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    @Transactional
    public void initAdministrator(){
        if(userJpaRepository.findByUserName("admin").isEmpty()) {
            List<RolePo> rolePoList = createRoles();
            UserGroupPo groupPo = createUserGroup();
            createUser(rolePoList, groupPo);
            logger.info("初始化管理員完成");
        }
    }

    private List<RolePo> createRoles() {
        logger.info("初始化角色中....");
        try {
            RolePo roleAdmin = createRole(UserRoleEnum.ROLE_ADMIN.getRoleName());
            RolePo roleUser = createRole(UserRoleEnum.ROLE_USER.getRoleName());
            RolePo roleSearch = createRole(UserRoleEnum.SEARCH.getRoleName());
            ArrayList<RolePo> rolePoList = new ArrayList<>();
            if(roleAdmin != null) {
                rolePoList.add(roleAdmin);
            }
            if(roleUser != null) {
                rolePoList.add(roleUser);
            }
            if(roleSearch != null) {
                rolePoList.add(roleSearch);
            }
            return rolePoRepository.saveAllAndFlush(rolePoList);
        } catch (Exception e) {
            logger.error("初始化角色失敗 錯誤原因:" + e.getMessage());
            throw new ValidateFailedException("初始化角色失敗 錯誤原因:" + e.getMessage());
        }
    }

    private RolePo createRole(String roleName) {
        if (rolePoRepository.findByRoleName(roleName).isPresent()) {
            return null;
        }
        RolePo role = new RolePo();
        role.setRoleName(roleName);
        role.setCreatUser("admin");
        role.setUpdateUser("admin");
        return role;
    }

    private UserGroupPo createUserGroup() {
        if(userGroupPoRepository.findByGroupName("admin-group").isPresent()) {
            return userGroupPoRepository.findByGroupName("admin-group").get();
        }
        try {
            UserGroupPo userGroupPo = new UserGroupPo();
            userGroupPo.setGroupName("admin-group");
            userGroupPo.setDescription("管理員群組");
            userGroupPo.setCreatUser("admin");
            userGroupPo.setReviewLevel(GroupAuthEnum.ADMIN.getStatus());
            return userGroupPoRepository.saveAndFlush(userGroupPo);
        } catch (Exception e) {
            logger.error("初始化群組失敗 錯誤原因:" + e.getMessage());
            throw new ValidateFailedException("初始化群組失敗 錯誤原因:" + e.getMessage());
        }
    }

    private void createUser(List<RolePo> rolePoList, UserGroupPo groupPo) {
        logger.info("初始化管理員中....");
        UserPo userPo;
        if(userJpaRepository.findByUserName("admin").isPresent()) {
            return;
        } else {
            userPo = new UserPo();
        }
        try {
            userPo.setUserName("admin");
            userPo.setEmail(adminEmail);
            userPo.setPassword(passwordEncoder.encode(adminPassword));
            userPo.setNickName("admin");
            userPo.setRoles(new HashSet<>(rolePoList));
            userPo.setUserGroupPo(groupPo);
            userPo.setCreatUser("system");
            userJpaRepository.saveAndFlush(userPo);
        } catch (Exception e) {
            logger.error("初始化管理員失敗 錯誤原因:" + e.getMessage());
            throw new ValidateFailedException("初始化管理員失敗 錯誤原因:" + e.getMessage());
        }
    }

}
