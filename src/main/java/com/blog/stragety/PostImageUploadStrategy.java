package com.blog.stragety;

import com.blog.dao.PostPoRepository;
import com.blog.dao.UserPoRepository;
import com.blog.exception.ValidateFailedException;
import com.blog.utils.FileUtil;
import com.blog.utils.SpringSecurityUtil;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


@Component
@RequiredArgsConstructor
public class PostImageUploadStrategy implements ImageUploadStrategy {
    private final Cloudinary cloudinary;
    private final PostPoRepository postPoRepository;

    @Autowired
    @Qualifier("defaultThreadPoolExecutor")
    private ThreadPoolExecutor threadPoolExecutor;
    private static final Logger logger = LoggerFactory.getLogger(PostImageUploadStrategy.class);

    /**
     * 上傳檔案至服務器 (異步執行)
     *
     * @param file 上傳檔案
     * @param type 處理類型 (文章圖片)
     * @return url 服務器存儲網址
     */
    @Override
    public CompletableFuture<String> uploadImage(MultipartFile file, String type) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("folder", type,
                        "public_id", FileUtil.generatePublicId(file.getOriginalFilename()), "overwrite", true,
                        "quality", "auto",
                        "fetch_format", "png")).get("url").toString();
            } catch (IOException e) {
                throw new ValidateFailedException("上傳失敗 原因: " + e.getMessage());
            }
        },threadPoolExecutor).completeOnTimeout(null, 5, TimeUnit.SECONDS).handle((imageUrl, throwable) -> {
            if (throwable != null || imageUrl == null) {
                logger.error("上傳失敗 原因: {}", throwable != null ? throwable.getMessage() : "上傳圖片超時");
                throw new ValidateFailedException("上傳失敗 原因: " + (throwable != null ? throwable.getMessage() : "上傳圖片超時"));
            }
            final String imageName = FileUtil.generatePublicId(file.getOriginalFilename());
            postPoRepository.updatePostImage(imageUrl, imageName, SpringSecurityUtil.getCurrentUser());
            return imageUrl;
        });
    }
}
