package com.pharmacy.management.controller;

import com.pharmacy.management.model.Product;
import com.pharmacy.management.repository.ProductRepository;
import com.pharmacy.management.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@RestController
@RequestMapping("/api")
public class ProductResource {

    private final Logger log = LoggerFactory.getLogger(ProductResource.class);

    private final ProductService productService;

    private final ProductRepository productRepository;

    public ProductResource(ProductService productService, ProductRepository productRepository) {
        this.productService = productService;
        this.productRepository = productRepository;
    }


    @PostMapping("/products")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) throws URISyntaxException {
        log.debug("REST request to save Product : {}", product);
        if (product.getId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A new product cannot already have an ID");
        }
        Product result = productService.save(product);
        return ResponseEntity
            .created(new URI("/api/products/" + result.getId()))
            .body(result);
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable(value = "id", required = false) final Long id, @RequestBody Product product) {
        log.debug("REST request to update Product : {}, {}", id, product);
        if (product.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A new product cannot already have an ID");
        }

        if (!Objects.equals(id, product.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Id");
        }

        if (!productRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Entity not found");
        }

        Product result = productService.save(product);
        return ResponseEntity
            .ok()
            .body(result);
    }

    @GetMapping("/products")
    public List<Product> getAllProducts() {
        log.debug("REST request to get all Products");
        return productService.findAll();
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        log.debug("REST request to get Product : {}", id);
        Optional<Product> product = productService.findOne(id);
        return ResponseEntity.ok(product.get());
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        log.debug("REST request to delete Product : {}", id);
        productService.delete(id);
        return ResponseEntity
            .noContent()
            .build();
    }
}
