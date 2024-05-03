package com.blog.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.blog.service.AwsS3ClientService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.*;

@Service
public class AmazonS3ClientService implements AwsS3ClientService {
    @Resource(name = "defaultThreadPoolExecutor")
    private ThreadPoolExecutor executor;
    @Resource
    private AmazonS3 amazonS3Client;
    private final String folderName = "userImage";
    private final String bucketName = "useravater";
    public CompletableFuture<String> uploadFile(String base64Image, String fileName) {
        byte[] imageBytes = Base64.getDecoder().decode(base64Image.split(",")[1]);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(imageBytes.length);
        metadata.setContentType("image/png");
        amazonS3Client.putObject(bucketName, fileName, inputStream, metadata);
        return CompletableFuture.completedFuture("文件上傳成功");
    }


    public CompletableFuture<String> deleteFile(String fileName) {
        String key = folderName + "/" + fileName;
        if (!amazonS3Client.doesObjectExist(bucketName, key)) {
            return CompletableFuture.completedFuture("文件不存在");
        }
        return CompletableFuture.runAsync(() -> {
            amazonS3Client.deleteObject(bucketName, key);
        }, executor).thenApply(result -> "文件删除成功");
    }

    public byte[] downloadFile(String fileName) throws IOException {
        String key = folderName + "/" + fileName;
        // 驗證是否有這個 key 的檔案存在
        boolean exist = amazonS3Client.doesObjectExist(bucketName, key);
        if (!exist) {
            return null;
        }
        S3Object s3object = amazonS3Client.getObject(bucketName, fileName);
        return IOUtils.toByteArray(s3object.getObjectContent());
    }
}
