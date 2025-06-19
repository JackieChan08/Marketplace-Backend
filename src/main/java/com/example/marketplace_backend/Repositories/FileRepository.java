package com.example.marketplace_backend.Repositories;

import com.example.marketplace_backend.Model.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FileRepository extends JpaRepository<FileEntity, UUID> {
    FileEntity findByUniqueName(String uniqueName);
    Optional<FileEntity> findByOriginalName(String originalName);
}
