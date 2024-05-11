package com.blog.service.impl;

import com.blog.service.GoogleStorageService;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class GoogleStorageServiceImpl implements GoogleStorageService {

    private static final String BUCKET_NAME = "blog-pic-bucket";
    private static final String GCP_URL = "https://www.googleapis.com/auth/cloud-platform";


    private static GoogleCredentials getGoogleAccess() throws IOException {
        try (InputStream is = GoogleStorageServiceImpl.class.getClassLoader().getResourceAsStream("json/key_for_access.json")) {
            if (is == null) {
                throw new FileNotFoundException("找不到該文件");
            }
            return GoogleCredentials.fromStream(is).createScoped(Lists.newArrayList((GCP_URL)));
        }
    }

    // get the storage
    private static Storage getStorage(GoogleCredentials googleCredentials) {
        return StorageOptions.newBuilder().setCredentials(googleCredentials).build().getService();
    }

    @Async(value = "defaultThreadPoolExecutor")
    public CompletableFuture<String> uploadFile(MultipartFile file, String fileName) throws IOException {
        GoogleCredentials credentials = getGoogleAccess();
        Storage storage = getStorage(credentials);
        // 將 file 轉成 bytes
        byte[] bytes = file.getBytes();
        // 將 bytes 轉成 blob
        BlobId blobId = BlobId.of(BUCKET_NAME, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        Blob blob = storage.create(blobInfo, bytes);

        blob.toBuilder().setContentType("image/jpg").build().update();
        // 回傳url
        return CompletableFuture.completedFuture(blob.getMediaLink());
    }

    @Async(value = "defaultThreadPoolExecutor")
    public void deleteFile(String fileName) throws IOException {
        GoogleCredentials credentials = getGoogleAccess();
        Storage storage = getStorage(credentials);

        BlobId blobId = BlobId.of(BUCKET_NAME, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        Blob blob = storage.create(blobInfo);
        Blob.BlobSourceOption option = Blob.BlobSourceOption.decryptionKey(fileName);
        blob.delete(option);
        // 返回刪除結果
        CompletableFuture.completedFuture("刪除文件成功");
    }

    public byte[] downloadFile(String fileName) throws IOException {
        // 回傳 圖片的 url地址
        GoogleCredentials credentials = getGoogleAccess();
        Storage storage = getStorage(credentials);
        BlobId blobId = BlobId.of(BUCKET_NAME, fileName);
        Blob blob = storage.get(blobId);
        return blob.getContent();
    }

}
