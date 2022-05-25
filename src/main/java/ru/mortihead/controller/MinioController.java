package ru.mortihead.controller;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.mortihead.service.MinioService;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
@RequestMapping(value = "api/v1/minio/", produces = "application/json")
public class MinioController {

    private final MinioService minioService;

    /**
     * @param file  File to upload
     * @return      object
     */
    @ApiOperation("Загрузка файла")
    @PostMapping("upload")
    public String upload(@RequestParam("file") MultipartFile file) {
        return minioService.upload(file);
    }


    /**
     * Получение файла
     * @param objectName    Object-name
     * @return              File
     */
    @ApiOperation("Получение файла")
    @GetMapping("/download")
    public ResponseEntity<ByteArrayResource>  download(@RequestParam(name = "object-name") String objectName) {
        byte[] data = minioService.download(objectName);
        ByteArrayResource resource = new ByteArrayResource(data);

        return ResponseEntity
                .ok()
                .contentLength(data.length)
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + objectName + "\"")
                .body(resource);
    }

}
