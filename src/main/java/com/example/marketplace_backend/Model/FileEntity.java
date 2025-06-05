package com.example.marketplace_backend.Model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "files")
public class FileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originalName;
    private String uniqueName;
    private String fileType;
    private String filePath;
    private LocalDateTime uploadTime;
}
