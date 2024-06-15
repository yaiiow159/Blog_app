package com.blog.service;

import jakarta.mail.MessagingException;

import java.security.NoSuchAlgorithmException;

public interface AuthService {
    void resetPassword(String token, String newPassword);

    void forgotPassword(String email) throws MessagingException, NoSuchAlgorithmException;
}
