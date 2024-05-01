package com.blog.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.blog.service.AwsS3ClientService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.*;

@Service
public class AmazonS3ClientService implements AwsS3ClientService {
    @Resource(name = "defaultThreadPoolExecutor")
    private ThreadPoolExecutor executor;
    @Resource
    private AmazonS3 amazonS3Client;
    private final String folderName = "userImage";
    private final String bucketName = "useravater";
    public CompletableFuture<String> uploadFileToS3Bucket(String fileName, File file) {
        String key = folderName + "/" + fileName;
        return CompletableFuture.runAsync(() -> {
            amazonS3Client.putObject(new PutObjectRequest(bucketName, key, file));
        }, executor).thenApply(result -> "文件上傳成功");
    }

    public CompletableFuture<String> deleteFileFromS3Bucket(String fileName) {
        String key = folderName + "/" + fileName;
        if (!amazonS3Client.doesObjectExist(bucketName, key)) {
            return CompletableFuture.completedFuture("文件不存在");
        }
        return CompletableFuture.runAsync(() -> {
            amazonS3Client.deleteObject(bucketName, key);
        }, executor).thenApply(result -> "文件删除成功");
    }

    public byte[] downloadFileFromS3Bucket(String fileName) throws IOException {
        String key = folderName + "/" + fileName;
        // 驗證是否有這個 key 的檔案存在
        boolean exist = amazonS3Client.doesObjectExist(bucketName, key);
        if (!exist) {
            return null;
        }
        return amazonS3Client.getObject(new GetObjectRequest(bucketName, key)).getObjectContent().readAllBytes();
    }
}
