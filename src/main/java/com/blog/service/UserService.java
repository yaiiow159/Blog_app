package com.blog.service;

import com.blog.dto.UserDto;
import com.blog.dto.UserProfileDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService extends BaseService<UserDto> {
    void lock(long id) throws Exception;
    void unlock(long id) throws Exception;
    void updateProfile(UserProfileDto userProfileDto) throws Exception;
    UserProfileDto queryProfile(String username) throws Exception;
    void changePassword(String oldPassword, String newPassword);

    UserDto findByName(String key) throws Exception;
    Page<UserDto> findAll(Integer page, Integer pageSize, String userName, String userEmail);
}
