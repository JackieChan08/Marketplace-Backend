package com.example.marketplace_backend.Service.Impl;

import com.example.marketplace_backend.Model.ProductParameters;
import com.example.marketplace_backend.Model.Product;
import com.example.marketplace_backend.Repositories.ProductParametersRepository;
import com.example.marketplace_backend.Repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class ProductParametersServiceImpl extends BaseServiceImpl<ProductParameters, UUID>{
    private final ProductParametersRepository productParametersRepository;
    private final ProductRepository productRepository;

    public ProductParametersServiceImpl(ProductParametersRepository productParametersRepository,
                                        ProductRepository productRepository) {
        super(productParametersRepository);
        this.productParametersRepository = productParametersRepository;
        this.productRepository = productRepository;
    }

    public ProductParameters create(ProductParameters productParameters) {
        if (productParameters.getProduct() == null || productParameters.getProduct().getId() == null) {
            throw new IllegalArgumentException("Продукт должен быть указан");
        }

        Product product = productRepository.findById(productParameters.getProduct().getId())
                .orElseThrow(() -> new RuntimeException("Продукт не найден с ID: " + productParameters.getProduct().getId()));

        productParameters.setProduct(product);

        Optional<ProductParameters> existing = productParametersRepository
                .findByNameAndProductId(productParameters.getName(), product.getId());
        if (existing.isPresent()) {
            throw new RuntimeException("Параметр с именем '" + productParameters.getName() +
                    "' уже существует для данного продукта");
        }

        return productParametersRepository.save(productParameters);
    }

    public ProductParameters update(UUID id, ProductParameters updatedParameters) {
        ProductParameters existing = getById(id);

        if (!existing.getName().equals(updatedParameters.getName())) {
            Optional<ProductParameters> duplicate = productParametersRepository
                    .findByNameAndProductId(updatedParameters.getName(), existing.getProduct().getId());
            if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
                throw new RuntimeException("Параметр с именем '" + updatedParameters.getName() +
                        "' уже существует для данного продукта");
            }
        }

        existing.setName(updatedParameters.getName());
        return productParametersRepository.save(existing);
    }

    public ProductParameters getById(UUID id) {
        return productParametersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Параметр продукта не найден с ID: " + id));
    }

    public List<ProductParameters> findAll() {
        return productParametersRepository.findAll();
    }

    public List<ProductParameters> findByProductId(UUID productId) {
        return productParametersRepository.findByProductId(productId);
    }

    public Optional<ProductParameters> findByNameAndProductId(String name, UUID productId) {
        return productParametersRepository.findByNameAndProductId(name, productId);
    }

    public long countByProductId(UUID productId) {
        return productParametersRepository.countByProductId(productId);
    }

    public void deleteById(UUID id) {
        if (!productParametersRepository.existsById(id)) {
            throw new RuntimeException("Параметр продукта не найден с ID: " + id);
        }
        productParametersRepository.deleteById(id);
    }

    public void delete(ProductParameters productParameters) {
        productParametersRepository.delete(productParameters);
    }

    public void deleteByProductId(UUID productId) {
        productParametersRepository.deleteByProductId(productId);
    }

    public boolean existsById(UUID id) {
        return productParametersRepository.existsById(id);
    }

    public boolean productExists(UUID productId) {
        return productRepository.existsById(productId);
    }

    public Optional<ProductParameters> findById(UUID id) {
        return productParametersRepository.findById(id);
    }
}
