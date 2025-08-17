package com.example.marketplace_backend.Repositories;

import com.example.marketplace_backend.Model.Phone.PhoneConnectionAndProductColor;
import com.example.marketplace_backend.Model.ProductColor;
import com.example.marketplace_backend.Model.Phone.PhoneConnection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PhoneConnectionAndProductColorRepository extends JpaRepository<PhoneConnectionAndProductColor, UUID> {
    @Query("SELECT pcpc FROM PhoneConnectionAndProductColor pcpc WHERE pcpc.productColor.id = :colorId")
    List<PhoneConnectionAndProductColor> findByProductColorId(@Param("colorId") UUID colorId);
}
