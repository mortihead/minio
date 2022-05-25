package ru.mortihead.service;

import org.springframework.web.multipart.MultipartFile;

public interface MinioService {
    String upload(MultipartFile file);
    byte[] download(String objectName);
}
