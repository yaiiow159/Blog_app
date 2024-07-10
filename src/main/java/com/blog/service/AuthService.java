package com.blog.service;

import com.blog.dto.UserDto;
import jakarta.mail.MessagingException;

import java.security.NoSuchAlgorithmException;

public interface AuthService {
    void resetPassword(String token, String newPassword) throws Exception;

    void forgotPassword(String email) throws Exception;

    void register(UserDto userDto) throws Exception;
}
