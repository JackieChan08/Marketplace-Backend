package com.example.marketplace_backend.controller;

import com.example.marketplace_backend.Service.Impl.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequiredArgsConstructor
public class FileController {

    private final FileUploadService fileUploadService;

    @Value("${file.upload.dir}")
    private String uploadDir;

    @GetMapping("/uploads/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        try {
            // Проверка входных данных
            if (filename == null || filename.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            Path filePath = Paths.get(uploadDir).resolve(filename).normalize();

            // Проверка на path traversal атаки
            Path uploadPath = Paths.get(uploadDir);
            if (!filePath.startsWith(uploadPath)) {
                return ResponseEntity.badRequest().build();
            }

            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }

            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            // Получение оригинального имени файла из базы данных
            String originalFilename = fileUploadService.getFileInfo(filename)
                    .map(fileEntity -> fileEntity.getOriginalName())
                    .orElse(filename.contains("_") ? filename.substring(filename.indexOf('_') + 1) : filename);

            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + originalFilename + "\"")
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);

        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/uploads/{filename:.+}")
    public ResponseEntity<?> deleteFile(@PathVariable String filename) {
        try {
            // Проверка входных данных
            if (filename == null || filename.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Filename cannot be empty");
            }

            boolean deleted = fileUploadService.deleteImage(filename);
            if (deleted) {
                return ResponseEntity.ok().body("File deleted successfully");
            } else {
                return ResponseEntity.badRequest().body("File not found or could not be deleted");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error occurred while deleting file");
        }
    }
}