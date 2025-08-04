package com.example.marketplace_backend.Service.Impl;

import com.example.marketplace_backend.Model.Intermediate_objects.ProductStatuses;
import com.example.marketplace_backend.Model.Product;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
//Специальный фильтр для продукта
public class ProductSpecification {


    public static Specification<Product> isNotDeleted() {
        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
    }

    public static Specification<Product> hasSubcategoryIds(List<UUID> subcategoryIds) {
        return (root, query, cb) -> subcategoryIds == null || subcategoryIds.isEmpty()
                ? null
                : root.get("subcategory").get("id").in(subcategoryIds);
    }

    public static Specification<Product> hasBrandIds(List<UUID> brandIds) {
        return (root, query, cb) -> brandIds == null || brandIds.isEmpty()
                ? null
                : root.get("brand").get("id").in(brandIds);
    }

    public static Specification<Product> hasPriceBetween(BigDecimal min, BigDecimal max) {
        return (root, query, cb) -> {
            if (min != null && max != null)
                return cb.between(root.get("price"), min, max);
            if (min != null)
                return cb.greaterThanOrEqualTo(root.get("price"), min);
            if (max != null)
                return cb.lessThanOrEqualTo(root.get("price"), max);
            return null;
        };
    }

    public static Specification<Product> hasStatusIds(List<UUID> statusIds) {
        return (root, query, cb) -> {
            if (statusIds == null || statusIds.isEmpty()) {
                return cb.conjunction();
            }

            Join<Object, Object> productStatusesJoin = root.join("productStatuses", JoinType.INNER);
            return productStatusesJoin.get("status").get("id").in(statusIds);
        };
    }



}
