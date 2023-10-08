package com.blog.utils;

import com.blog.jwt.JwtUser;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

@Slf4j
public class LoginUtils {
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
}
