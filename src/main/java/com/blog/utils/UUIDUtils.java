package com.blog.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UUIDUtils {

    @Bean
    public String getUUID32(){
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }
}
