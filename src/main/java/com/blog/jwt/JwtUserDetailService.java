package com.blog.jwt;


import com.blog.dto.UserDto;
import com.blog.service.UserService;
import com.blog.utils.CacheUtil;
import com.blog.utils.JsonUtil;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtUserDetailService implements UserDetailsService {

    private final UserService userService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("當前加載使用者: {}", username);
        UserDto userDto = null;
        if (null != CacheUtil.get(username)) {
            try {
                String userInfo = CacheUtil.get(username);
                userDto = JsonUtil.parseObject(userInfo, UserDto.class);
            } catch (JsonProcessingException e) {
                log.error("Json格式轉換時時發生錯誤: {}", e.getMessage());
            }
        } else {
            try {
                userDto = userService.findByName(username);
                cacheUserInfo(userDto);
            } catch (UsernameNotFoundException e) {
                log.error("該使用者不存在: {}", e.getMessage());
                throw new UsernameNotFoundException("該使用者不存在" + username);
            } catch (JsonProcessingException e) {
                log.error("Json格式轉換時時發生錯誤: {}", e.getMessage());
            } catch (Exception e) {
                log.error("錯誤異常: {}", e.getMessage());
            }
        }
        assert userDto != null;
        String userName = userDto.getUserName();
        String email = userDto.getPassword();

        Set<GrantedAuthority> authoritySet = new HashSet<>();
        userDto.getRoles().forEach(role -> {
            authoritySet.add((GrantedAuthority) role::getRoleName);
        });
        return new User(userName, email, authoritySet);
    }

    private void cacheUserInfo(UserDto userDto) throws JsonProcessingException {
        String json = JsonUtil.toJsonString(userDto);
        CacheUtil.put(userDto.getUserName(), json);
    }
}
