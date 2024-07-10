package com.blog.stragety;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLException;


public class UploadImageContext {
    private final ImageUploadStrategy imageUploadStrategy;
    public UploadImageContext(ImageUploadStrategy imageUploadStrategy) {
        this.imageUploadStrategy = imageUploadStrategy;
    }
    public String uploadImage(MultipartFile file, String type) throws Exception {
        return imageUploadStrategy.uploadImage(file,type).get();
    }
}
