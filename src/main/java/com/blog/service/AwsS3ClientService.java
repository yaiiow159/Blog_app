package com.blog.service;

import com.blog.exception.ResourceNotFoundException;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public interface AwsS3ClientService {

    CompletableFuture<String> uploadFileToS3Bucket(String fileName, File file);
    byte[] downloadFileFromS3Bucket(String fileName) throws ResourceNotFoundException, IOException;

    CompletableFuture<String> deleteFileFromS3Bucket(String fileName);
}
