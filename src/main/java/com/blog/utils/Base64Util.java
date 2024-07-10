package com.blog.utils;

import java.util.Base64;

public class Base64Util {
    public static String encodeString(String str) {
        return Base64.getEncoder().encodeToString(str.getBytes());
    }
    public static String decodeString(String decodeStr) {
        return new String(Base64.getDecoder().decode(decodeStr.getBytes()));
    }

}
