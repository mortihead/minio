package org.examlpe.controller;

import io.minio.*;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
@RequestMapping(value = "api/v1/minio/", produces = "application/json")
public class MinioController {

    private static String URL = "http://localhost:9000";
    private static String ACCESSKEY = "admin";
    private static String SECRETKEY = "password";
    private static String BUCKETNAME = "test-bucket-1";

    /**
     * @param file
     * @return
     */
    @PostMapping("upload")
    public String upload(@RequestParam("file") MultipartFile file) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        // Create a minioClient with the MinIO server playground, its access key and secret key.
        MinioClient minioClient =
                MinioClient.builder()
                        .endpoint(URL)
                        .credentials(ACCESSKEY, SECRETKEY)
                        .build();

        // Make  bucket if not exist.
        boolean found =
                minioClient.bucketExists(BucketExistsArgs.builder().bucket(BUCKETNAME).build());
        if (!found) {
            // Make a new bucket called 'asiatrip'.
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(BUCKETNAME).build());
        } else {
            log.warn("Bucket '{}' already exists.", BUCKETNAME);
        }

        String filename = file.getOriginalFilename();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String objectName = sdf.format(new Date()) + "/" + filename;

        minioClient.putObject(PutObjectArgs.builder()
                .bucket(BUCKETNAME)
                .object(objectName)
                .stream(file.getInputStream(), file.getSize(), -1)
                .build());

        log.info("'{}' is successfully uploaded as "
                        + "object '{}}' to bucket '{}'.", filename, objectName, BUCKETNAME);
        return "OK";
    }

}
