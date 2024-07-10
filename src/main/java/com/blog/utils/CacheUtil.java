package com.blog.utils;

import com.blog.dto.UserDto;
import com.blog.listener.RemovalCustomerListener;
import com.blog.service.UserService;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 利用guava cache實現本地緩存 工具類
 */

@Component
public class CacheUtil {
    private static UserService userService;

    private final static Logger logger = LoggerFactory.getLogger(CacheUtil.class);

    @Autowired
    public void setUserService(UserService userService) {
        CacheUtil.userService = userService;
    }
    private static final LoadingCache<String, String> quavaCache = CacheBuilder.newBuilder()
            .initialCapacity(10)
            .maximumSize(1000)
            .expireAfterAccess(15, TimeUnit.MINUTES)
            .recordStats()
            .refreshAfterWrite(3, TimeUnit.MINUTES)
            .concurrencyLevel(1)
            .softValues()
            .removalListener(new RemovalCustomerListener())
            .build(new CacheLoader<>() {
                @Override
                public String load(String key) {
                    return getUserInfo(key);
                }
            });

    private static String getUserInfo(final String key) {
        UserDto userDto;
        try {
            userDto = userService.findByName(key);
            if(null == userDto) {
                return null;
            }
            return JsonUtil.toJsonString(userDto);
        } catch (Exception e) {
            logger.error("Json 序列化 錯誤 原因為: {}", e.getMessage());
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
        logger.info("清除緩存");
    }

}
