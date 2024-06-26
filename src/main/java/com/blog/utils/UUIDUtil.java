package com.blog.utils;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UUIDUtil {
    public static String getUUID32(){
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }
}
