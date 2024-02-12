package com.blog.utils;

import java.util.Base64;

public class Base64Utils {
    public static String encodeString(String password) {
        return Base64.getEncoder().encodeToString(password.getBytes());
    }
    public static String decodeString(String password) {
        return new String(Base64.getDecoder().decode(password.getBytes()));
    }

}
