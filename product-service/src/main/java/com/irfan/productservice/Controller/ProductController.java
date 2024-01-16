package com.irfan.productservice.Controller;

import com.irfan.productservice.Service.ProductService;
import com.irfan.productservice.dto.ProductRequest;
import com.irfan.productservice.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createProduct(@RequestBody ProductRequest productRequest) {
            productService.createProduct(productRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> getAllProducts() {
       return productService.getAllProducts();
    }

    @GetMapping("{productId}")
    @ResponseStatus(HttpStatus.OK)
    public ProductResponse getProductById(@PathVariable String productId) {
        return productService.getProductById(productId);
    }

    @PutMapping("{productId}")
    @ResponseStatus(HttpStatus.OK)
    public ProductResponse updateProductById(@PathVariable String productId, @RequestBody ProductRequest productRequest) {
        return productService.updateProductById(productId, productRequest);
    }

    @DeleteMapping("{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProductById(@PathVariable String productId) {
        productService.deleteProductById(productId);
    }

}
