package com.blog.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public interface GoogleStorageService {

    CompletableFuture<String> uploadFile(MultipartFile filePath, String fileName) throws IOException;

    void deleteFile(String fileName) throws IOException;

    byte[] downloadFile(String fileName) throws IOException;


}
