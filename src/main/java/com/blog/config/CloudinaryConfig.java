package com.blog.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Value("${cloudinary.cloud_name}")
    private String CLOUDNAME;

    @Value("${cloudinary.api_key}")
    private String APIKEY;

    @Value("${cloudinary.api_secret}")
    private String APISECRET;

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", CLOUDNAME,
                "api_key", APIKEY,
                "api_secret", APISECRET
        ));
    }
}
