package com.blog.interceptor;

import com.blog.dao.UserJpaRepository;
import com.blog.po.UserPo;
import com.blog.utils.SpringSecurityUtils;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;

@Slf4j
@Component
public class UserLockedInterceptor implements HandlerInterceptor {

    private final AutowireCapableBeanFactory beanFactory;
    @Resource
    private UserJpaRepository userJpaRepository;

    public UserLockedInterceptor(AutowireCapableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 檢查使用者是否被鎖
        if(userJpaRepository == null){
            userJpaRepository = beanFactory.getBean(UserJpaRepository.class);
        }
        String currentUserName = SpringSecurityUtils.getCurrentUser();
        Optional<UserPo> user = userJpaRepository.findByUserName(currentUserName);

        if (user.isPresent()) {
            UserPo userPo = user.get();
            if (userPo.isLocked()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "帳號已被鎖戶");
                return false;
            }
        }
        return true;
    }
}
