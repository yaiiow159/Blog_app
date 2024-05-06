package com.blog.service.impl;

import com.blog.service.GoogleStorageService;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class GoogleStorageServiceImpl implements GoogleStorageService {

    private static final String BUCKET_NAME = "blog-pic-bucket";
    private static final String KEY_FOR_ACCESS = "src/main/resources/json/key_for_access.json";
    private static final String GCP_URL = "https://www.googleapis.com/auth/cloud-platform";


    private static GoogleCredentials getGoogleAccess() throws IOException {
        return GoogleCredentials
                .fromStream(new FileInputStream(KEY_FOR_ACCESS))
                .createScoped(Lists.newArrayList((GCP_URL)));
    }

    // get the storage
    private static Storage getStorage(GoogleCredentials googleCredentials) {
        return StorageOptions.newBuilder().setCredentials(googleCredentials).build().getService();
    }

    @Async(value = "defaultThreadPoolExecutor")
    public CompletableFuture<String> uploadFile(String filePath, String fileName) throws IOException {
        GoogleCredentials credentials = getGoogleAccess();
        Storage storage = getStorage(credentials);
        byte[] bytes = Files.readAllBytes(Paths.get(filePath));

        BlobId blobId = BlobId.of(BUCKET_NAME, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        Blob blob = storage.create(blobInfo, bytes);

        blob.toBuilder().setContentType("image/png").build().update();
        return CompletableFuture.completedFuture("上傳文件成功");
    }

    @Async(value = "defaultThreadPoolExecutor")
    public CompletableFuture<String> deleteFile(String fileName) throws IOException {
        GoogleCredentials credentials = getGoogleAccess();
        Storage storage = getStorage(credentials);

        BlobId blobId = BlobId.of(BUCKET_NAME, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        Blob blob = storage.create(blobInfo);
        Blob.BlobSourceOption option = Blob.BlobSourceOption.decryptionKey(fileName);
        blob.delete(option);

        return CompletableFuture.completedFuture("刪除文件成功");
    }

    public byte[] downloadFile(String fileName) throws IOException {
        GoogleCredentials credentials = getGoogleAccess();
        Storage storage = getStorage(credentials);
        BlobId blobId = BlobId.of(BUCKET_NAME, fileName);
        Blob blob = storage.get(blobId);
        return blob.getContent();
    }

}
