package com.example.marketplace_backend.Repositories.TableRepository;

import com.example.marketplace_backend.Model.ProductSpec.TableSpec.TableMemory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TableMemoryRepository extends JpaRepository<TableMemory, UUID> {
}
