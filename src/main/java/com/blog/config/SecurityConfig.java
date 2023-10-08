package com.blog.config;

import com.blog.filter.JwtAuthenticatePreFilter;
import com.blog.handler.JwtlogoutSuccessHandler;
import com.blog.jwt.JwtAuthAccessDeniedHandler;
import com.blog.jwt.JwtAuthenticationEntryPoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;


import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.Resource;


@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Resource
    private JwtAuthAccessDeniedHandler accessDeniedHandler;

    @Resource
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Resource
    private JwtlogoutSuccessHandler jwtLogoutSuccessHandler;

    @Resource
    private JwtAuthenticatePreFilter jwtAuthenticatePreFilter;


    @Bean
    @ConditionalOnMissingBean
    public static PasswordEncoder getInstance() {
        return new BCryptPasswordEncoder();
    }


    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/swagger-ui/**", "/v3/api-docs/**","/api/v1/auth/login","/api/v1/auth/register","/webjars/**").permitAll()
                .anyRequest().authenticated().and()
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler)
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .and().logout().logoutSuccessHandler(jwtLogoutSuccessHandler)
                .and()
                .addFilterBefore(jwtAuthenticatePreFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .build();
    }
}
