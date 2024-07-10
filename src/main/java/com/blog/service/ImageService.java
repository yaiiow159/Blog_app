package com.blog.service;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    String upload(MultipartFile file, String type) throws FileUploadException;
}
