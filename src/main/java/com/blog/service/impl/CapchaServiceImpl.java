package com.blog.service.impl;

import com.blog.service.CapchaService;
import com.blog.utils.Base64Utils;
import com.blog.utils.UUIDUtils;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import io.netty.handler.codec.base64.Base64Encoder;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class CapchaServiceImpl implements CapchaService {

    @Value("${kapcha.token-expiration-time}")
    private Integer expireTime;

    @Resource
    private UUIDUtils uuidUtils;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private DefaultKaptcha producer;

    @Override
    public String createToken(String captchaCode) {
        // 生成token
        String token = uuidUtils.getUUID32();
        redisTemplate.opsForValue().set(token, captchaCode);
        redisTemplate.expire(token, expireTime, TimeUnit.MINUTES);
        return token;
    }
    @Override
    public Map<String, Object> capchaCreator() throws IOException {
        return capchaImageCreator();
    }

    @Override
    public Boolean verifyCapchaCode(String token,String inputCode) {
        if(Boolean.TRUE.equals(redisTemplate.hasKey(token))){
            String captchaCode = (String) redisTemplate.opsForValue().get(token);
            if(Objects.equals(captchaCode, inputCode)){
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    public Map<String, Object> capchaImageCreator() throws IOException {
        String text = producer.createText();
        BufferedImage bufferedImage = producer.createImage(text);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", outputStream);
        byte[] bytes = outputStream.toByteArray();
        Map<String, Object> map = new HashMap<>();
        map.put("key", createToken(text));
        map.put("image", Base64.getEncoder().encode(bytes));
        return map;
    }
}
