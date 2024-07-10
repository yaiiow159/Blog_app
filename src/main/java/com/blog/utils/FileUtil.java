package com.blog.utils;

import org.apache.commons.codec.digest.DigestUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileUtil {
    // 生成文件名
    public static String getFileName(String originalFilename) {
        String extension = "";
        int i = originalFilename.lastIndexOf('.');
        if (i > 0) {
            extension = originalFilename.substring(i + 1);
        }
        String uniqueFileName = UUIDUtil.getUUID32();
        if (!extension.isEmpty()) {
            uniqueFileName += "." + extension;
        }
        return uniqueFileName;
    }

    // 生成 publicId (檔案名稱hash(sha-256) + 當前時間戳)
    public static String generatePublicId(String originalFilename) {
        return DigestUtils.sha256Hex(originalFilename) + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy:MM:dd:HH:mm:ss"));
    }
}
