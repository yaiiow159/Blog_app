package com.blog.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    private static final String CLOUDNAME = "dl4nfjvva";

    private static final String APIKEY = "122577474975536";

    private static final String APISECRET = "gfGRSb8k1t6cMWBY8X00jO5x8bk";


    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", CLOUDNAME,
                "api_key", APIKEY,
                "api_secret", APISECRET
        ));
    }

}
