package com.irfan.productservice.Service;

import com.irfan.productservice.Exception.ProductNotFoundException;
import com.irfan.productservice.dto.ProductRequest;
import com.irfan.productservice.dto.ProductResponse;
import com.irfan.productservice.model.Product;
import com.irfan.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    public void createProduct(ProductRequest productRequest) {
        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .build();

        productRepository.save(product);
        log.info("Product {} is saved", product.getId());
    }

    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();
        // Log the product list
        log.info("Retrieved product list: " + products);
        return products.stream().map(this::mapToProductResponse).toList();
    }

    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();

    }

    public ProductResponse getProductById(String productId) {
        Optional<Product> getProduct = productRepository.findById(productId);

        if (getProduct.isPresent()) {
            Product product = getProduct.get();
            log.info("Retrieved product by ID {}: {}", productId, product);
            return mapToProductResponse(product);
        } else {
            throw new ProductNotFoundException("Product not found with ID: " + productId);
        }
    }

    public ProductResponse updateProductById(String productId, ProductRequest productRequest) {
        Optional<Product> getProduct = productRepository.findById(productId);

        if (getProduct.isPresent()) {
            Product existingProduct = getProduct.get();
            // Update the existing product with new data from the request
            existingProduct.setName(productRequest.getName());
            existingProduct.setPrice(productRequest.getPrice());
            existingProduct.setDescription(productRequest.getDescription());
            // Add more fields to update as needed

            // Save the updated product
            productRepository.save(existingProduct);

            // Log the update
            log.info("Updated product by ID {}: {}", productId, existingProduct);

            // Map and return the updated product
            return mapToProductResponse(existingProduct);
        } else {
            throw new ProductNotFoundException("Product not found with ID: " + productId);
        }
    }

    public void deleteProductById(String productId) {
        Optional<Product> getProduct = productRepository.findById(productId);

        if (getProduct.isPresent()) {
            Product existingProduct = getProduct.get();

            // Delete the product
            productRepository.delete(existingProduct);

            // Log the deletion
            log.info("Deleted product by ID {}: {}", productId, existingProduct);
        } else {
            throw new ProductNotFoundException("Product not found with ID: " + productId);
        }
    }


}
