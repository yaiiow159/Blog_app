package com.blog.service.impl;

import com.blog.exception.ValidateFailedException;
import com.blog.service.ImageService;
import com.blog.stragety.PostImageUploadStrategy;
import com.blog.stragety.UploadImageContext;
import com.blog.stragety.UserAvatarUploadStrategy;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.UploadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    private final UserAvatarUploadStrategy userAvatarUploadStrategy;
    private final PostImageUploadStrategy postImageUploadStrategy;
    private static final Logger logger = LoggerFactory.getLogger(ImageServiceImpl.class);

    /**
     * 上傳檔案至服務器 (異步執行)
     *
     * @param file 上傳檔案
     * @param type 處理類型 (使用者頭像/文章圖片)
     * @return url 服務器存儲網址
     */
    @Override
    public String upload(MultipartFile file, String type) throws FileUploadException {
        // 檢查 檔案類型 (png 才可以)
        if (!Objects.equals(file.getContentType(), "image/png")) {
            throw new ValidateFailedException("檔案類型不符, 限定 png");
        }
        // 最大不超過 5MB
        if (file.getSize() > 1024 * 1024 * 5) {
            throw new ValidateFailedException("檔案大小超過限制, 限定 1MB");
        }
        String url;
        UploadImageContext uploadImageContext;
        try {
            // 上傳 按照不同 類型處理
            url = switch (type) {
                case "user_avatars" -> {
                    uploadImageContext = new UploadImageContext(userAvatarUploadStrategy);
                    yield uploadImageContext.uploadImage(file, type);
                }
                case "article_images" -> {
                    uploadImageContext = new UploadImageContext(postImageUploadStrategy);
                    yield uploadImageContext.uploadImage(file, type);
                }
                default -> null;
            };
        } catch (Exception e) {
            logger.error("上傳失敗 原因: {}", e.getMessage());
            throw new FileUploadException("上傳失敗 原因: " + e.getMessage());
        }
        return url;
    }
}
