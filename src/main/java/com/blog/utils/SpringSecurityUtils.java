package com.blog.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class SpringSecurityUtils {
    private static final String DEFAULT_USER = "admin";
    public static String getCurrentUser(){
        try {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(user != null){
            return user.getUsername();
        }
        return DEFAULT_USER;
    } catch (Exception e) {
        log.warn("cannot get the current user ");
    }
        return null;
    }
    public static User getCurrentUserDetails(){
        try {
            return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e) {
            log.warn("cannot get the current user ");
        }
        return null;
    }

    public static Set<String> getCurrentUserAuthorities() {
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        } catch (Exception e) {
            log.warn("cannot get the current user ");
        }
        return null;
    }
}
