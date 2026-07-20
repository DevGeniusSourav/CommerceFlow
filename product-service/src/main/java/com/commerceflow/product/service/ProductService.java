package com.commerceflow.product.service;

import com.commerceflow.product.dto.request.CreateProductRequest;
import com.commerceflow.product.dto.response.ProductResponse;
import com.commerceflow.product.entity.Product;
import com.commerceflow.product.exception.ProductNotFoundException;
import com.commerceflow.product.mapper.ProductMapper;
import com.commerceflow.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    public ProductResponse createProduct(CreateProductRequest request) {
        Product product = productMapper.toEntity(request);
        Product savedProduct = productRepository.save(product);
        return productMapper.toResponse(savedProduct);
    }

    public ProductResponse getProductById(Long id) {
        return productRepository.findById(id).map(productMapper::toResponse).orElseThrow(() -> new ProductNotFoundException(id));
    }

    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable).map(productMapper::toResponse);
    }
}
