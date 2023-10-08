package com.blog.jwt;


import com.blog.dto.UserDto;
import com.blog.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class JwtUserDetailService implements UserDetailsService {
    @Resource
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("loadUserByUsername: {}", username);
        UserDto userDto = userService.findByUserName(username);
        if(userDto == null)
            throw new UsernameNotFoundException("User not found");
        String userName = userDto.getUserName();
        String password = userDto.getPassword();
        Set<GrantedAuthority> authoritySet = userDto.getRoles().stream().map(
                role -> new SimpleGrantedAuthority(role.getRoleName())
        ).collect(Collectors.toSet());
        return new User(userName, password, authoritySet);
    }
}
