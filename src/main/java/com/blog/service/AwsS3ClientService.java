package com.blog.service;

import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.blog.exception.ResourceNotFoundException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface AwsS3ClientService {

    CompletableFuture<String> uploadFileToS3Bucket(String fileName, File file);
    InputStream downloadFileFromS3Bucket(String fileName) throws ResourceNotFoundException;

    CompletableFuture<String> deleteFileFromS3Bucket(String fileName);
}
