package com.blog.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.util.Set;
import java.util.stream.Collectors;

public class SpringSecurityUtil {

    private final static Logger logger = LoggerFactory.getLogger(SpringSecurityUtil.class);
    private static final String DEFAULT_USER = "admin";

    /**
     * 獲取當前使用者名稱
     *
     * @return 當前使用者名稱
     */
    public static String getCurrentUser() {
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (user != null) {
                return user.getUsername();
            }
            return DEFAULT_USER;
        } catch (Exception e) {
            logger.warn("找不到該用戶的資訊");
        }
        return null;
    }

    /**
     * 獲取當前使用者資訊
     *
     * @see User
     * @return User 當前使用者
     */
    public static User getCurrentUserDetails() {
        try {
            return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e) {
            logger.warn("找不到該 用戶的資訊");
        }
        return null;
    }

    /**
     * 獲取當前使用者角色
     *
     * @see GrantedAuthority
     * @return 角色集合
     */
    public static Set<String> getCurrentUserAuthorities() {
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        } catch (Exception e) {
            logger.warn("找不到該 用戶的權限");
        }
        return null;
    }
}
