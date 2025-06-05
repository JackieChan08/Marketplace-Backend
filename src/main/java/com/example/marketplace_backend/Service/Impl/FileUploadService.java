package com.example.marketplace_backend.Service.Impl;

import com.example.marketplace_backend.Model.FileEntity;
import com.example.marketplace_backend.Repositories.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final FileRepository fileRepository;
    @Value("${file.upload.dir}")
    private String uploadsDir;

    public FileEntity saveImage(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(uploadsDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalName = file.getOriginalFilename();
        String uniqueName = UUID.randomUUID() + "_" + originalName;

        // Проверка: не загружать повторно одинаковые фото
        Optional<FileEntity> existing = fileRepository.findByOriginalName(originalName);
        if (existing.isPresent()) {
            return existing.get();
        }

        Path filePath = uploadPath.resolve(uniqueName);
        Files.copy(file.getInputStream(), filePath);

        FileEntity fileEntity = new FileEntity();
        fileEntity.setOriginalName(originalName);
        fileEntity.setUniqueName(uniqueName);
        fileEntity.setFileType(file.getContentType());
        fileEntity.setFilePath(filePath.toString());
        fileEntity.setUploadTime(LocalDateTime.now());
        System.out.println("FILE SAVED TO: " + filePath);

        return fileRepository.save(fileEntity);
    }

}
