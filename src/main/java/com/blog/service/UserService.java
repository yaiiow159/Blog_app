package com.blog.service;

import com.blog.dto.UserProfileDto;
import com.blog.dto.UserProfileRequestBody;
import com.blog.exception.ResourceNotFoundException;
import com.blog.exception.ValidateFailedException;
import com.blog.dto.UserDto;
import org.springframework.data.domain.Page;

import javax.naming.AuthenticationNotSupportedException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface UserService {
    UserDto findByUserId(Long userId);

    void add(UserDto userDto) throws AuthenticationNotSupportedException, ValidateFailedException;

    void edit(UserDto userDto) throws AuthenticationNotSupportedException;

    String delete(Long userId);

    UserDto findByUserName(String userName);

    String register(UserDto body) throws AuthenticationNotSupportedException;

    void logout(String token) throws AuthenticationNotSupportedException;

    UserProfileDto updateUserProfile(UserProfileRequestBody userProfileRequestBody) throws IOException, ExecutionException, InterruptedException, TimeoutException;

    UserProfileDto getUserProfile(String username) throws ResourceNotFoundException, ExecutionException, InterruptedException, IOException, Exception;

    List<UserDto> findUsersByRoleName(long id);

    String unlockUser(Long userId);

    String lockUser(Long userId);

    void changePassword(String oldPassword, String newPassword);

    Page<UserDto> findAll(String userName, String userEmail, int page, int pageSize);
}
