package ru.mortihead.service.impl;

import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.mortihead.service.MinioService;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinioServiceImpl implements MinioService {

    @Value("${minio.url}")
    private String url;
    @Value("${minio.accesskey}")
    private String accessKey;
    @Value("${minio.secretkey}")
    private String secretKey;
    @Value("${minio.bucketname}")
    private String bucketName;

    @Override
    @SneakyThrows
    public String upload(MultipartFile file) {

        // Create a minioClient with the MinIO server playground, its access key and secret key.
        MinioClient minioClient =
                MinioClient.builder()
                        .endpoint(url)
                        .credentials(accessKey, secretKey)
                        .build();

        // Make  bucket if not exist.
        boolean found =
                minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!found) {
            // Make a new bucket
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        } else {
            log.warn("Bucket '{}' already exists.", bucketName);
        }

        String fileName = file.getOriginalFilename();

        ObjectWriteResponse response = minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .object(fileName)
                .stream(file.getInputStream(), file.getSize(), -1)
                .build());

        log.info("'{}' is successfully uploaded as "
                + "object '{}' to bucket '{}'.", fileName, fileName, bucketName);

        return response.object();
    }


    @Override
    @SneakyThrows
    public byte[] download(String objectName) {
        MinioClient minioClient =
                MinioClient.builder()
                        .endpoint(url)
                        .credentials(accessKey, secretKey)
                        .build();

        try (InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build())) {

            return IOUtils.toByteArray(stream);
        }
    }


}
