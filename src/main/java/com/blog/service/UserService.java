package com.blog.service;

import com.blog.dto.UserProfileDto;
import com.blog.dto.UserProfileRequestBody;
import com.blog.exception.ResourceNotFoundException;
import com.blog.exception.ValidateFailedException;
import com.blog.dto.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;

import javax.naming.AuthenticationNotSupportedException;
import java.util.List;

public interface UserService {
    UserDto findByUserId(Long userId);

    UserDto createUser(UserDto userDto) throws AuthenticationNotSupportedException, ValidateFailedException;

    UserDto updateUser(UserDto userDto) throws AuthenticationNotSupportedException;

    String deleteUser(Long userId);

    UserDto findByUserName(String userName);

    Page<UserDto> findBySpec(Long userId, String userName, String userEmail, int page, int size, String sort, String direction);

    List<UserDto> findUsersByRoleId(Long id);

    UserDto register(UserDto body);

    UserProfileDto findUserProfileByUserNameOrEmail(UserProfileRequestBody userProfileRequestBody) throws ResourceNotFoundException;

    String logout(String token) throws AuthenticationNotSupportedException;
}
