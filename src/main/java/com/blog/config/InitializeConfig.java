package com.blog.config;

import com.blog.dao.RolePoRepository;
import com.blog.dao.UserGroupPoRepository;
import com.blog.dao.UserPoRepository;
import com.blog.enumClass.UserRole;
import com.blog.po.RolePo;
import com.blog.po.UserGroupPo;
import com.blog.po.UserPo;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Configuration
@Slf4j
public class InitializeConfig {

    @Resource
    private UserPoRepository userJpaRepository;

    @Resource
    private UserGroupPoRepository userGroupPoRepository;

    @Resource
    private RolePoRepository rolePoRepository;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private EntityManager entityManager;

    @PostConstruct
    @Transactional(rollbackFor = Exception.class)
    public void initAdministrator() throws Exception {

        if(userJpaRepository.findByUserName("admin").isEmpty()) {

            RolePo roleAdmin = new RolePo();
            roleAdmin.setRoleName(UserRole.ROLE_ADMIN.getRoleName());
            roleAdmin.setCreatUser("admin");
            roleAdmin.setUpdateUser("admin");

            RolePo roleUser = new RolePo();
            roleUser.setRoleName(UserRole.ROLE_USER.getRoleName());
            roleUser.setCreatUser("admin");
            roleUser.setUpdateUser("admin");

            RolePo roleSearch = new RolePo();
            roleSearch.setRoleName(UserRole.ONLY_SEARCH.getRoleName());
            roleSearch.setCreatUser("admin");
            roleSearch.setUpdateUser("admin");

            List<RolePo> rolePoList = rolePoRepository.saveAllAndFlush(Arrays.asList(roleAdmin, roleUser, roleSearch));

            UserGroupPo userGroupPo = new UserGroupPo();
            userGroupPo.setGroupName("admin");
            userGroupPo.setDescription("管理員群組");
            userGroupPo.setCreatUser("admin");
            userGroupPo.setUpdateUser("admin");
            UserGroupPo groupPo = userGroupPoRepository.saveAndFlush(userGroupPo);

            UserPo userPo = new UserPo();
            userPo.setUserName("admin");
            userPo.setEmail("admin123@gmail.com");
            userPo.setPassword(passwordEncoder.encode("admin123"));
            userPo.setNickName("admin");

            userPo.setRoles(new HashSet<>(rolePoList));
            userPo.setUserGroupPo(groupPo);
            userJpaRepository.saveAndFlush(userPo);

            log.info("初始化管理員帳號成功....");

        }
    }

}
