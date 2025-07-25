package com.example.marketplace_backend.Service.Impl;

import com.example.marketplace_backend.Model.FileEntity;
import com.example.marketplace_backend.Repositories.FileRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final FileRepository fileRepository;

    @Value("${file.upload.dir}")
    private String uploadsDir;

    @Transactional
    public FileEntity saveImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Файл не может быть пустым");
        }

        String originalName = file.getOriginalFilename();
        if (originalName == null || originalName.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя файла не может быть пустым");
        }

        String contentType = file.getContentType();
        if (contentType == null ||
                (!contentType.startsWith("image/") && !contentType.equalsIgnoreCase("image/svg+xml"))) {
            throw new IllegalArgumentException("Поддерживаются только изображения");
        }

        // Проверка размера файла
        long maxFileSize = 10 * 1024 * 1024; // 10MB
        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException("Размер файла не должен превышать 10MB");
        }

        Path uploadPath = Paths.get(uploadsDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileExtension = getFileExtension(originalName);
        String uniqueName = generateUniqueFileName(originalName, fileExtension);

        try {
            Path filePath = uploadPath.resolve(uniqueName).normalize();

            if (!filePath.startsWith(uploadPath)) {
                throw new SecurityException("Недопустимый путь к файлу");
            }

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Создание записи в БД
            FileEntity fileEntity = new FileEntity();
            fileEntity.setOriginalName(originalName);
            fileEntity.setUniqueName(uniqueName);
            fileEntity.setFileType(contentType);
            fileEntity.setFilePath(filePath.toString());

            FileEntity savedEntity = fileRepository.save(fileEntity);

            log.info("Файл успешно сохранен: {}", uniqueName);

            return savedEntity;
        } catch (IOException e) {
            log.error("Ошибка при сохранении файла: {}", originalName, e);
            throw new IOException("Не удалось сохранить файл: " + originalName, e);
        }
    }

    private String generateUniqueFileName(String originalName, String fileExtension) {
        String uniqueName;
        int attempts = 0;
        int maxAttempts = 5;

        do {
            uniqueName = UUID.randomUUID() + "_" + System.currentTimeMillis() + fileExtension;
            attempts++;

            if (attempts > maxAttempts) {
                throw new RuntimeException("Не удалось создать уникальное имя файла после " + maxAttempts + " попыток");
            }
        } while (fileRepository.findByUniqueName(uniqueName).isPresent());

        return uniqueName;
    }

    private boolean isImageFile(String contentType) {
        return contentType.startsWith("image/") &&
                (contentType.equals("image/jpeg") ||
                        contentType.equals("image/png") ||
                        contentType.equals("image/gif") ||
                        contentType.equals("image/webp") ||
                        contentType.equals("image/svg") ||
                        contentType.equals("image/xml") ||
                        contentType.equals("image/bmp"));
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }

    @Transactional
    public boolean deleteImage(String uniqueName) {
        if (uniqueName == null || uniqueName.trim().isEmpty()) {
            log.warn("Попытка удалить файл с пустым uniqueName");
            return false;
        }

        boolean dbDeleted = false;
        boolean fileDeleted = false;

        try {
            // Сначала удаляем запись из БД
            Optional<FileEntity> fileEntityOpt = fileRepository.findByUniqueName(uniqueName);

            if (fileEntityOpt.isPresent()) {
                FileEntity fileEntity = fileEntityOpt.get();
                fileRepository.delete(fileEntity);
                dbDeleted = true;
                log.info("Запись файла удалена из БД: {}", uniqueName);
            } else {
                log.warn("Файл не найден в базе данных: {}", uniqueName);
            }

            // Затем удаляем файл с диска
            Path filePath = Paths.get(uploadsDir).resolve(uniqueName).normalize();

            // Проверка на path traversal атаки
            Path uploadPath = Paths.get(uploadsDir);
            if (!filePath.startsWith(uploadPath)) {
                log.error("Недопустимый путь к файлу при удалении: {}", filePath);
                return dbDeleted; // Возвращаем результат удаления из БД
            }

            if (Files.exists(filePath)) {
                fileDeleted = Files.deleteIfExists(filePath);
                if (fileDeleted) {
                    log.info("Файл успешно удален с диска: {}", uniqueName);
                }
            } else {
                log.warn("Файл не найден на диске: {}", uniqueName);
                fileDeleted = true; // Считаем успешным, если файла и не было
            }

            // Успешным считается удаление, если либо из БД удалили, либо файл удалили
            boolean success = dbDeleted || fileDeleted;

            if (success) {
                log.info("Файл успешно обработан для удаления: {}", uniqueName);
            }

            return success;

        } catch (IOException e) {
            log.error("Ошибка при удалении файла: {}", uniqueName, e);
            return dbDeleted; // Возвращаем хотя бы результат удаления из БД
        } catch (Exception e) {
            log.error("Неожиданная ошибка при удалении файла: {}", uniqueName, e);
            return dbDeleted;
        }
    }

    public Optional<FileEntity> getFileInfo(String uniqueName) {
        if (uniqueName == null || uniqueName.trim().isEmpty()) {
            return Optional.empty();
        }
        return fileRepository.findByUniqueName(uniqueName);
    }

    public boolean fileExists(String uniqueName) {
        if (uniqueName == null || uniqueName.trim().isEmpty()) {
            return false;
        }

        // Проверяем в БД
        Optional<FileEntity> dbEntity = fileRepository.findByUniqueName(uniqueName);
        if (dbEntity.isEmpty()) {
            return false;
        }

        // Проверяем на диске
        Path filePath = Paths.get(uploadsDir).resolve(uniqueName).normalize();

        // Проверка на path traversal
        Path uploadPath = Paths.get(uploadsDir);
        if (!filePath.startsWith(uploadPath)) {
            return false;
        }

        return Files.exists(filePath);
    }

    @Transactional
    public void cleanupOrphanedFiles() {
        try {
            Path uploadPath = Paths.get(uploadsDir);
            if (!Files.exists(uploadPath)) {
                return;
            }

            Files.walk(uploadPath)
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        String filename = file.getFileName().toString();
                        if (fileRepository.findByUniqueName(filename).isEmpty()) {
                            try {
                                Files.deleteIfExists(file);
                                log.info("Удален неиспользуемый файл: {}", filename);
                            } catch (IOException e) {
                                log.error("Не удалось удалить неиспользуемый файл: {}", filename, e);
                            }
                        }
                    });
        } catch (IOException e) {
            log.error("Ошибка при очистке неиспользуемых файлов", e);
        }
    }
}