package com.blog.service;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public interface GoogleStorageService {

    CompletableFuture<String> uploadFile(String filePath, String fileName) throws IOException;

    CompletableFuture<String> deleteFile(String fileName) throws IOException;

    byte[] downloadFile(String fileName) throws IOException;


}
