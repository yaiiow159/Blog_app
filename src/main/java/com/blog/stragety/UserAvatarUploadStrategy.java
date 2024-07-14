package com.blog.stragety;

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
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class UserAvatarUploadStrategy implements ImageUploadStrategy {
    private final Cloudinary cloudinary;
    private final UserPoRepository userPoRepository;

    private final ThreadPoolExecutor threadPoolExecutor;
    private static final Logger logger = LoggerFactory.getLogger(UserAvatarUploadStrategy.class);

    @Autowired
    public UserAvatarUploadStrategy(Cloudinary cloudinary,
                                    UserPoRepository userPoRepository,
                                    @Qualifier("defaultThreadPoolExecutor") ThreadPoolExecutor threadPoolExecutor) {
        this.cloudinary = cloudinary;
        this.userPoRepository = userPoRepository;
        this.threadPoolExecutor = threadPoolExecutor;
    }

    /**
     * 上傳檔案至服務器
     *
     * @param file 上傳檔案
     * @param type 處理類型 (使用者頭像)
     * @return url 服務器存儲網址
     */
    @Override
    public CompletableFuture<String> uploadImage(MultipartFile file,String type){
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 設置自動壓縮圖片
                return cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("folder", type,
                        "public_id", FileUtil.generatePublicId(file.getOriginalFilename()), "overwrite", true,
                        "quality", "auto",
                        "fetch_format", "png")).get("url").toString();
            } catch (IOException e) {
                throw new ValidateFailedException("上傳失敗 原因: " + e.getMessage());
            }
        },threadPoolExecutor).completeOnTimeout(null, 5, TimeUnit.SECONDS).handle((imageUrl, throwable) -> {
            if (throwable != null || imageUrl == null) {
                logger.error("上傳失敗 原因: {}", throwable != null ? throwable.getMessage() : "Timeout");
                throw new ValidateFailedException("上傳失敗 原因: " + (throwable != null ? throwable.getMessage() : "上傳圖片超時"));
            }

            final String imageName = FileUtil.getFileName(Objects.requireNonNull(file.getOriginalFilename()));
            userPoRepository.updateUserAvatar(imageUrl, imageName, SpringSecurityUtil.getCurrentUser());

            return imageUrl;
        });
    }
}
