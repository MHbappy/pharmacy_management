package com.pharmacy.management.controller;

import com.pharmacy.management.dto.request.ProductRequestDTO;
import com.pharmacy.management.dto.request.ProductRequestExcelDTO;
import com.pharmacy.management.dto.request.ResponseMessage;
import com.pharmacy.management.model.Product;
import com.pharmacy.management.projection.ProductProjection;
import com.pharmacy.management.repository.ProductRepository;
import com.pharmacy.management.service.ProductService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class ProductResource {

    private final Logger log = LoggerFactory.getLogger(ProductResource.class);
    private final ProductService productService;
    private final ProductRepository productRepository;

    @PostMapping("/products")
    public ResponseEntity<Product> createProduct(@RequestBody ProductRequestDTO productRequestDTO) throws URISyntaxException {

        Optional<Product> productOptional = productRepository.findByCodeAndIsActive(productRequestDTO.getCode(), true);
        if (productOptional.isPresent()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product code should be unique");
        }
        Product result = productService.save(productRequestDTO);

        return ResponseEntity
            .created(new URI("/api/products/" + result.getId()))
            .body(result);
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable(value = "id", required = false) final Long id, @RequestBody ProductRequestDTO productRequestDTO) {

        log.debug("REST request to update Product : {}, {}", id, productRequestDTO);
        if (productRequestDTO.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A new product cannot already have an ID");
        }
        if (!Objects.equals(id, productRequestDTO.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Id");
        }
        if (!productRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Entity not found");
        }

        Product result = productService.save(productRequestDTO);
        return ResponseEntity
            .ok()
            .body(result);
    }

    @GetMapping("/products")
    public Page<Product> getAllProducts(@RequestParam(name = "name", defaultValue = "") String name, Pageable pageable) {
        log.debug("REST request to get all Products");
        return productService.findAllByName(name, pageable);
    }

//    @GetMapping("/on-stock-products")
//    public List<Product> getAllOnStockProducts(@RequestParam(name = "name", defaultValue = "") String name) {
//        log.debug("REST request to get all Products");
//        return productService.getAllOnStockProduct(name);
//    }

    @GetMapping("/all-products")
    public List<Product> getAllProducts(@RequestParam(name = "name", defaultValue = "") String name) {
        log.debug("REST request to get all Products");
        return productService.findAllByName(name);
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        log.debug("REST request to get Product : {}", id);
        Optional<Product> product = productService.findOne(id);
        return ResponseEntity.ok(product.get());
    }

    @GetMapping("/on-stock-product-name-productId-search")
    public List<ProductProjection> searchProductNameAndProductId(@RequestParam(value = "categoryId") Long categoryId,
                                                                 @RequestParam(value = "productNameOrProductId", required = false, defaultValue = "") String productNameOrProductId){
        List<ProductProjection> product = productService.searchProductNameAndProductId(productNameOrProductId, categoryId);
        if (product == null){
            return new ArrayList<>();
        }
        return product;
    }

    @PostMapping("/products/upload-product-by-exel")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        return productService.saveProductWithExcel(productService.getProductFromFile(file));
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
