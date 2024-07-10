package com.blog.stragety;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

public interface ImageUploadStrategy {
    CompletableFuture<String> uploadImage(MultipartFile file, String type);

}
