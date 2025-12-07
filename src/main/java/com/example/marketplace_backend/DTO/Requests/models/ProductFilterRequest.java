package com.example.marketplace_backend.DTO.Requests.models;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class ProductFilterRequest {
    private List<UUID> subcategoryIds;
    private List<UUID> brandIds;
    private List<UUID> statusIds;

    @DecimalMin(value = "0.0", message = "Минимальная цена не может быть отрицательной")
    private BigDecimal minPrice;

    @DecimalMin(value = "0.0", message = "Максимальная цена не может быть отрицательной")
    private BigDecimal maxPrice;

    @Pattern(regexp = "^(price|name)?$", message = "Сортировка возможна только по price или name")
    private String sortBy;

    @Pattern(regexp = "^(asc|desc)?$", message = "Направление сортировки может быть только asc или desc")
    private String sortDirection;

    @Min(value = 0, message = "Номер страницы не может быть отрицательным")
    private int page = 0;

    @Min(value = 1, message = "Размер страницы должен быть не менее 1")
    private int size = 10;
}