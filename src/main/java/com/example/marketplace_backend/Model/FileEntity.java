package com.example.marketplace_backend.Model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "files")
public class FileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String originalName;
    private String uniqueName;
    private String fileType;
    private String filePath;
    private LocalDateTime uploadTime;
}
