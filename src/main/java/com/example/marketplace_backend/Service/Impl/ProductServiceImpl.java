package com.example.marketplace_backend.Service.Impl;

import com.example.marketplace_backend.Model.Category;
import com.example.marketplace_backend.Model.FileEntity;
import com.example.marketplace_backend.Model.Product;
import com.example.marketplace_backend.Repositories.CategoryRepository;
import com.example.marketplace_backend.Repositories.ProductRepository;
import com.example.marketplace_backend.Repositories.SubcategoryRepository;
import com.example.marketplace_backend.controller.Requests.models.ProductRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl extends BaseServiceImpl<Product, Long> {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SubcategoryRepository subcategoryRepository;
    private final FileUploadService fileUploadService;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository, SubcategoryRepository subcategoryRepository, FileUploadService fileUploadService) {
        super(productRepository);
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.subcategoryRepository = subcategoryRepository;
        this.fileUploadService = fileUploadService;
    }


    public Product findByName(String name) {
        return productRepository.findByName(name);
    }

    public List<Product> findByNameContaining(String name) {
        return productRepository.findByNameContaining(name);
    }
    public List<Product> findByCategory(Category category){
        return productRepository.findByCategory(category);
    };
    public void deActiveProductByCategory(Category category){
        List<Product> products = productRepository.findByCategory(category);
        for(Product product : products){
            product.setDeleted(true);
            productRepository.save(product);
        }
    }
    public void activeProductByCategory(Category category){
        List<Product> products = productRepository.findByCategory(category);
        for(Product product : products){
            product.setDeleted(false);
            productRepository.save(product);
        }
    }
    public List<Product> findAllDeActive() {
        return productRepository.findAllDeActive();
    }
    public List<Product> findAllActive() {
        return productRepository.findAllActive();
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product createProduct(ProductRequest dto) throws Exception {
        Product product = new Product();
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setDescription(dto.getDescription());

        product.setCategory(categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found")));
        product.setSubcategory(subcategoryRepository.findById(dto.getSubcategoryId())
                .orElseThrow(() -> new RuntimeException("Subcategory not found")));

        FileEntity image = fileUploadService.saveImage(dto.getImage());
        product.setImage(image);

        product.setDeleted(false);

        return productRepository.save(product);
    }
}
