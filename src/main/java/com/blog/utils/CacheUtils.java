package com.blog.utils;

import com.blog.dto.UserDto;
import com.blog.listener.RemovalCustomerListener;
import com.blog.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 利用guava cache實現本地緩存 工具類
 */
@Component
@Slf4j
public class CacheUtils {
    private static UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        CacheUtils.userService = userService;
    }
    private static final LoadingCache<String, String> quavaCache = CacheBuilder.newBuilder()
            .initialCapacity(10)
            .maximumSize(100)
            .expireAfterAccess(15, TimeUnit.MINUTES)
            .recordStats()
            .refreshAfterWrite(3, TimeUnit.MINUTES)
            .concurrencyLevel(4)
            .softValues()
            .removalListener(new RemovalCustomerListener())
            .build(new CacheLoader<>() {
                @Override
                public String load(String key) {
                    return getUserInfo(key);
                }
            });

    private static String getUserInfo(String key) {
        UserDto userDto;
        try {
            userDto = userService.findByUserName(key);
            if(null == userDto) {
                return null;
            }
            return JsonUtil.toJsonString(userDto);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String get(String key) {
        return quavaCache.getIfPresent(key);
    }

        public static void put(String key, String value) {
            quavaCache.put(key, value);
        }

        public static void remove(String key) {
            if(quavaCache.getIfPresent(key) == null) {
                return;
            }
            quavaCache.invalidate(key);
        }

        public static void clear() {
        quavaCache.cleanUp();
        System.out.println("清除緩存...");
    }

}
