package com.example.marketplace_backend.Repositories;

import com.example.marketplace_backend.Model.ProductParameters;
import com.example.marketplace_backend.Model.ProductSubParameters;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductSubParametersRepository extends JpaRepository<ProductSubParameters, UUID> {

    List<ProductSubParameters> findByProductParameterId(UUID productParameterId);

    Optional<ProductSubParameters> findByNameAndProductParameterId(String name, UUID productParameterId);

    @Modifying
    @Transactional
    void deleteByProductParameterId(UUID productParameterId);
}
