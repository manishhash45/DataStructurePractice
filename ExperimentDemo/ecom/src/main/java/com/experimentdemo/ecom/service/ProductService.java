package com.experimentdemo.ecom.service;

import com.experimentdemo.ecom.entity.Product;
import com.experimentdemo.ecom.dto.ProductDTO;
import com.experimentdemo.ecom.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public ProductDTO createProduct(Product product) {
        Product savedProduct = productRepository.save(product);
        return convertToDTO(savedProduct);
    }

    public Optional<ProductDTO> getProductById(Long id) {
        return productRepository.findById(id).map(this::convertToDTO);
    }

    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProductDTO> getProductsByCategory(String category) {
        return productRepository.findByCategory(category).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProductDTO> getProductsByBrand(String brand) {
        return productRepository.findByBrand(brand).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProductDTO> searchProducts(String searchTerm) {
        return productRepository.searchProducts(searchTerm).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ProductDTO updateProduct(Long id, Product productDetails) {
        Product product = productRepository.findById(id).orElseThrow(() ->
            new RuntimeException("Product not found"));
        
        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setDiscountPrice(productDetails.getDiscountPrice());
        product.setStock(productDetails.getStock());
        product.setCategory(productDetails.getCategory());
        product.setBrand(productDetails.getBrand());
        product.setImageUrl(productDetails.getImageUrl());
        
        Product updatedProduct = productRepository.save(product);
        return convertToDTO(updatedProduct);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    private ProductDTO convertToDTO(Product product) {
        return new ProductDTO(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getDiscountPrice(),
            product.getStock(),
            product.getCategory(),
            product.getBrand(),
            product.getRating(),
            product.getReviewCount(),
            product.getImageUrl()
        );
    }
}