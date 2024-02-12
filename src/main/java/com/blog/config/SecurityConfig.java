package com.blog.config;

import com.blog.filter.JwtAuthenticatePreFilter;
import com.blog.handler.JwtlogoutSuccessHandler;
import com.blog.handler.LogoutHandler;
import com.blog.jwt.JwtAuthAccessDeniedHandler;
import com.blog.jwt.JwtAuthenticationEntryPoint;
import jakarta.annotation.Resource;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.config.BeanIds;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

import java.util.Collections;


@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class SecurityConfig {
    @Resource
    private JwtAuthAccessDeniedHandler accessDeniedHandler;
    @Resource
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    @Resource
    private JwtlogoutSuccessHandler jwtLogoutSuccessHandler;
    @Resource
    private JwtAuthenticatePreFilter jwtAuthenticatePreFilter;
    @Resource
    private LogoutHandler logoutHandler;

    @Bean
    @ConditionalOnMissingBean(PasswordEncoder.class)
    public static PasswordEncoder getInstance() {
        return new BCryptPasswordEncoder();
    }

    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.addAllowedMethod(CorsConfiguration.ALL); // 允許所有方法
            config.addAllowedHeader(CorsConfiguration.ALL); // 允許所有請求
            config.setAllowCredentials(true); // 預檢請求
            config.setMaxAge(3600L);
            config.setAllowedOriginPatterns(Collections.singletonList("*"));
            return config;
        };
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(httpSecurityCorsConfigurer ->
                        httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource()))
                .httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests.requestMatchers(
                                        "/swagger-ui/**",
                                        "/v3/api-docs/**",
                                        "/api/v1/auth/login",
                                        "/api/v1/auth/logout",
                                        "/api/v1/auth/register",
                                        "/api/v1/auth/captchaCode",
                                        "/websocket/**",
                                        "/api/v1/auth/forget",
                                        "/api/v1/auth/reset",
                                        "/api/v1/mailNotification/**",
                                        "/static/**").permitAll()
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                .requestMatchers("/actuator/**").permitAll()
                                .anyRequest().authenticated()
                )
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling
                                .accessDeniedHandler(accessDeniedHandler)
                                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )
                .formLogin(formLogin ->{
                    formLogin
                            .loginPage("http://localhost:3000/login")
                            .loginProcessingUrl("http://localhost:9090/api/v1/auth/login")
                            .defaultSuccessUrl("http://localhost:3000/home")
                            .permitAll();
                })
                .logout(logout ->
                        logout
                                .logoutUrl("/api/v1/auth/logout")
                                .logoutSuccessUrl("http://localhost:3000/login")
                                .clearAuthentication(true)
                                .addLogoutHandler(logoutHandler)
                                .logoutSuccessHandler(jwtLogoutSuccessHandler)
                                .invalidateHttpSession(true)
                                .deleteCookies("JSESSIONID")
                                .permitAll()
                )
                .addFilterBefore(jwtAuthenticatePreFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(sessionManagement ->
                        sessionManagement
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );
        return http.build();
    }



}
