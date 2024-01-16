package com.irfan.productservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.irfan.productservice.dto.ProductRequest;
import com.irfan.productservice.model.Product;
import com.irfan.productservice.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProductServiceApplicationTests {

	@Container
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private ProductRepository productRepository;

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry dymDynamicProperRegistry){
		dymDynamicProperRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
	}

	@Test
	void shouldCreateProduct() throws Exception {
		ProductRequest productRequest = getProductRequest();
		String productRequestString = objectMapper.writeValueAsString(productRequest);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
				.contentType(MediaType.APPLICATION_JSON)
				.content(productRequestString))
				.andExpect(status().isCreated());
		Assertions.assertEquals(1, productRepository.findAll().size());
	}

	@Test
	void shouldGetAllProducts() throws Exception {
		// Perform the GET request
		mockMvc.perform(MockMvcRequestBuilders.get("/api/product")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.jsonPath("$").isArray());

		// Add more assertions based on the expected response
		// For example, you can check specific elements in the response content
	}

	@Test
	void shouldUpdateProduct() throws Exception {
		// Create a product and save it to the repository for updating
		ProductRequest initialProductRequest = getProductRequest();
		Product initialProduct = productRepository.save(mapProductRequestToProduct(initialProductRequest));

		// Update the product request with new values
		ProductRequest updatedProductRequest = getUpdateProductRequest("Updated Product", "Updated Description", BigDecimal.valueOf(1500));

		// Perform the update request
		mockMvc.perform(MockMvcRequestBuilders.put("/api/product/{id}", initialProduct.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(updatedProductRequest)))
				.andExpect(status().isOk());

		// Verify that the product has been updated in the repository
		Product updatedProduct = productRepository.findById(initialProduct.getId())
				.orElseThrow(() -> new RuntimeException("Product not found"));

		// Assertions for the updated product using ObjectMapper
		Assertions.assertEquals(updatedProductRequest.getName(), updatedProduct.getName());
		Assertions.assertEquals(updatedProductRequest.getDescription(), updatedProduct.getDescription());
		Assertions.assertEquals(updatedProductRequest.getPrice(), updatedProduct.getPrice());
	}


//	@Test
//	void shouldDeleteProduct() throws Exception {
//		// Create a product and save it to the repository for deleting
//		ProductRequest productRequest = getProductRequest();
//		Product product = mapProductRequestToProduct(productRequest);
//
//		when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
//
//		// Perform the delete request
//		mockMvc.perform(MockMvcRequestBuilders.delete("/api/product/{id}", product.getId())
//						.contentType(MediaType.APPLICATION_JSON))
//				.andExpect(MockMvcResultMatchers.status().isOk());
//
//		// Verify that the product has been deleted from the repository
//		verify(productRepository, times(1)).delete(product);
//
//		// Additional verification: Attempt to find the product by ID, it should not exist
//		Optional<Product> deletedProduct = productRepository.findById(product.getId());
//		Assertions.assertTrue(deletedProduct.isEmpty(), "Product should be deleted");
//	}

	private Product mapProductRequestToProduct(ProductRequest productRequest) {
		return objectMapper.convertValue(productRequest, Product.class);
	}

	private ProductRequest getUpdateProductRequest(String name, String description, BigDecimal price) {
		return ProductRequest.builder()
				.name("Iphone 15")
				.description("Apple Iphone Updated")
				.price(BigDecimal.valueOf(5000))
				.build();
	}

	private ProductRequest getProductRequest() {
		return ProductRequest.builder()
				.name("Iphone 13")
				.description("Apple Iphone")
				.price(BigDecimal.valueOf(1200))
				.build();
	}


}
