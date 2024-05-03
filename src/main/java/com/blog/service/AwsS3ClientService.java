package com.blog.service;

import com.blog.exception.ResourceNotFoundException;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public interface AwsS3ClientService {

    CompletableFuture<String> uploadFile(String imageUrl, String imageName);
    byte[] downloadFile(String fileName) throws ResourceNotFoundException, IOException;

    CompletableFuture<String> deleteFile(String fileName);
}
