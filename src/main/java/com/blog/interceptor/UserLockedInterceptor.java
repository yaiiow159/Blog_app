package com.blog.interceptor;

import com.blog.dao.UserPoRepository;
import com.blog.exception.ValidateFailedException;
import com.blog.po.UserPo;
import com.blog.utils.SpringSecurityUtil;

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
    private UserPoRepository userJpaRepository;

    public UserLockedInterceptor(AutowireCapableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 檢查使用者是否被鎖
        if(null == userJpaRepository) {
            userJpaRepository = beanFactory.getBean(UserPoRepository.class);
        }
        String currentUserName = SpringSecurityUtil.getCurrentUser();
        Optional<UserPo> user = userJpaRepository.findByUserName(currentUserName);

        if (user.isPresent()) {
            UserPo userPo = user.get();
            if (userPo.isLocked()) {
                throw new ValidateFailedException(ValidateFailedException.DomainErrorStatus.USER_LOCKED);
            }
        }
        return true;
    }
}
