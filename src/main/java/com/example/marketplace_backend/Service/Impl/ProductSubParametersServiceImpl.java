package com.example.marketplace_backend.Service.Impl;

import com.example.marketplace_backend.Model.ProductSubParameters;
import com.example.marketplace_backend.Model.ProductParameters;
import com.example.marketplace_backend.Repositories.ProductParametersRepository;
import com.example.marketplace_backend.Repositories.ProductSubParametersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional

public class ProductSubParametersServiceImpl extends BaseServiceImpl<ProductSubParameters, UUID> {
    private final ProductSubParametersRepository productSubParametersRepository;
    private final ProductParametersRepository productParametersRepository;

    public ProductSubParametersServiceImpl(ProductSubParametersRepository productSubParametersRepository,
                                           ProductParametersRepository productParametersRepository) {
        super(productSubParametersRepository);
        this.productSubParametersRepository = productSubParametersRepository;
        this.productParametersRepository = productParametersRepository;
    }

    public ProductSubParameters create(ProductSubParameters productSubParameters) {
        if (productSubParameters.getProductParameter() == null ||
                productSubParameters.getProductParameter().getId() == null) {
            throw new IllegalArgumentException("Параметр продукта должен быть указан");
        }

        ProductParameters productParameter = productParametersRepository
                .findById(productSubParameters.getProductParameter().getId())
                .orElseThrow(() -> new RuntimeException("Параметр продукта не найден с ID: " +
                        productSubParameters.getProductParameter().getId()));

        productSubParameters.setProductParameter(productParameter);

        Optional<ProductSubParameters> existing = productSubParametersRepository
                .findByNameAndProductParameterId(productSubParameters.getName(),
                        productParameter.getId());
        if (existing.isPresent()) {
            throw new RuntimeException("Подпараметр с именем '" + productSubParameters.getName() +
                    "' уже существует для данного параметра");
        }

        return productSubParametersRepository.save(productSubParameters);
    }

    public ProductSubParameters update(UUID id, ProductSubParameters updatedSubParameters) {
        ProductSubParameters existing = getById(id);

        // Проверяем уникальность имени, если оно изменилось
        if (!existing.getName().equals(updatedSubParameters.getName())) {
            Optional<ProductSubParameters> duplicate = productSubParametersRepository
                    .findByNameAndProductParameterId(updatedSubParameters.getName(),
                            existing.getProductParameter().getId());
            if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
                throw new RuntimeException("Подпараметр с именем '" + updatedSubParameters.getName() +
                        "' уже существует для данного параметра");
            }
        }

        existing.setName(updatedSubParameters.getName());
        existing.setValue(updatedSubParameters.getValue());

        return productSubParametersRepository.save(existing);
    }

    public ProductSubParameters getById(UUID id) {
        return productSubParametersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Подпараметр продукта не найден с ID: " + id));
    }

    public List<ProductSubParameters> findAll() {
        return productSubParametersRepository.findAll();
    }

    public List<ProductSubParameters> findByProductParameterId(UUID productParameterId) {
        return productSubParametersRepository.findByProductParameterId(productParameterId);
    }

    public Optional<ProductSubParameters> findByNameAndProductParameterId(String name, UUID productParameterId) {
        return productSubParametersRepository.findByNameAndProductParameterId(name, productParameterId);
    }

    public void deleteById(UUID id) {
        if (!productSubParametersRepository.existsById(id)) {
            throw new RuntimeException("Подпараметр продукта не найден с ID: " + id);
        }
        productSubParametersRepository.deleteById(id);
    }

    public void delete(ProductSubParameters productSubParameters) {
        productSubParametersRepository.delete(productSubParameters);
    }

    public void deleteByProductParameterId(UUID productParameterId) {
        productSubParametersRepository.deleteByProductParameterId(productParameterId);
    }

    public boolean existsById(UUID id) {
        return productSubParametersRepository.existsById(id);
    }

    public boolean productParameterExists(UUID productParameterId) {
        return productParametersRepository.existsById(productParameterId);
    }

    public List<ProductSubParameters> createBatch(List<ProductSubParameters> subParameters) {
        for (ProductSubParameters subParam : subParameters) {
            if (subParam.getProductParameter() == null ||
                    subParam.getProductParameter().getId() == null) {
                throw new IllegalArgumentException("Параметр продукта должен быть указан для всех подпараметров");
            }

            if (!productParametersRepository.existsById(subParam.getProductParameter().getId())) {
                throw new RuntimeException("Параметр продукта не найден с ID: " +
                        subParam.getProductParameter().getId());
            }
        }

        return productSubParametersRepository.saveAll(subParameters);
    }

    public ProductSubParameters updateValue(UUID id, String newValue) {
        ProductSubParameters existing = getById(id);
        existing.setValue(newValue);
        return productSubParametersRepository.save(existing);
    }

    public Optional<ProductSubParameters> findById(UUID id) {
        return productSubParametersRepository.findById(id);
    }
}