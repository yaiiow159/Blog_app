package com.blog.controller;

import com.blog.response.ResponseBody;
import com.blog.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/upload")
@RequiredArgsConstructor
@Tag(name = "圖片上傳相關功能", description = "圖片上傳相關功能API")
public class UploadController {

    private final ImageService imageService;

    @PostMapping
    @Operation(summary = "上傳圖片", description = "上傳圖片")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseBody<String> upload(@RequestPart("file") MultipartFile file, @RequestPart("type") String type) {
        String url;
        try {
            url = imageService.upload(file, type);
        } catch (Exception e) {
            return new ResponseBody<>(false, "上傳失敗 原因: " + e.getMessage(), null, HttpStatus.NO_CONTENT);
        }
        return new ResponseBody<>(true, "上傳成功",url, HttpStatus.OK);
    }
}
