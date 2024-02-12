package com.blog.service;

import com.blog.dto.UserProfileDto;
import com.blog.dto.UserProfileRequestBody;
import com.blog.exception.ResourceNotFoundException;
import com.blog.exception.ValidateFailedException;
import com.blog.dto.UserDto;
import jakarta.mail.MessagingException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import javax.naming.AuthenticationNotSupportedException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface UserService {
    public static final String DEFAULT_USER_NAME = "admin";
    UserDto findByUserId(Long userId);

    UserDto createUser(UserDto userDto) throws AuthenticationNotSupportedException, ValidateFailedException;

    UserDto updateUser(UserDto userDto) throws AuthenticationNotSupportedException;

    String deleteUser(Long userId);

    UserDto findByUserName(String userName);

    Page<UserDto> findBySpec(String userName, String userEmail, int page, int size, String sort, String direction);

    List<UserDto> findUsersByRoleId(Long id);

    String register(UserDto body);

    String logout(String token) throws AuthenticationNotSupportedException;

    UserProfileDto updateUserProfile(UserProfileRequestBody userProfileRequestBody) throws IOException, ExecutionException, InterruptedException, TimeoutException;

    UserProfileDto getUserProfile(String username) throws ResourceNotFoundException, ExecutionException, InterruptedException, IOException, Exception;

    List<UserDto> findUsersByRoleName(long id);

}
