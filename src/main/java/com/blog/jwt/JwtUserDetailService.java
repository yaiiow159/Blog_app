package com.blog.jwt;


import com.blog.dao.RolePoRepository;
import com.blog.dto.UserDto;
import com.blog.service.UserService;
import com.blog.utils.CacheUtils;
import com.blog.utils.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.Resource;
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
public class JwtUserDetailService implements UserDetailsService {
    @Resource
    private UserService userService;
    @Resource
    private RolePoRepository rolePoRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("loadUserByUsername: {}", username);
        UserDto userDto = null;
        if (null != CacheUtils.get(username)) {
            try {
                String userInfo = CacheUtils.get(username);
                userDto = JsonUtil.parseObject(userInfo, UserDto.class);
            } catch (JsonProcessingException e) {
                log.error("JsonProcessingException: {}", e.getMessage());
            }
        } else {
            userDto = userService.findByUserName(username);
            if(null == userDto)
                throw new UsernameNotFoundException("User not found");
            try {
                cacheUserInfo(userDto);
            } catch (JsonProcessingException e) {
                log.error("JsonProcessingException: {}", e.getMessage());
            }
        }
        assert userDto != null;
        String userName = userDto.getUserName();
        String email = userDto.getPassword();

        Set<GrantedAuthority> authoritySet = new HashSet<>();
        userDto.getRoles().forEach(role -> {
            authoritySet.add(new GrantedAuthority() {
                @Override
                public String getAuthority() {
                    return role.getRoleName();
                }
            });
        });
        return new User(userName, email, authoritySet);
    }

    private void cacheUserInfo(UserDto userDto) throws JsonProcessingException {
        String json = JsonUtil.toJsonString(userDto);
        CacheUtils.put(userDto.getUserName(), json);
    }
}
