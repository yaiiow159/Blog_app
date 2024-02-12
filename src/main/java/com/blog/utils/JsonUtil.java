package com.blog.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
public class JsonUtil {

    public static String toJsonString(Object obj) throws JsonProcessingException {
         return JSON.toJSONString(obj);
    }

    public static <T> T parseObject(String json, Class<T> clazz) throws JsonProcessingException {
        return JSON.parseObject(json, clazz);
    }

    public static <T> T parseObject(String json, TypeReference<T> typeReference) throws JsonProcessingException {
        return JSON.parseObject(json, typeReference);
    }

    public static <T> T parseObject(String json, Class<T> clazz, ObjectMapper objectMapper) throws JsonProcessingException {
        return objectMapper.readValue(json, clazz);
    }
}
