package com.blog.config;

import com.blog.interceptor.UserLockedInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class HandlerConfig implements WebMvcConfigurer {

    private final AutowireCapableBeanFactory beanFactory;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UserLockedInterceptor(beanFactory)).addPathPatterns("/api/v1/**");
    }
}
