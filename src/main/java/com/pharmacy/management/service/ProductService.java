package com.pharmacy.management.service;

import com.pharmacy.management.model.Product;
import com.pharmacy.management.model.Suppliers;
import com.pharmacy.management.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class ProductService {
    private final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product save(Product product) {
        log.debug("Request to save Product : {}", product);
        return productRepository.save(product);
    }

    public Optional<Product> partialUpdate(Product product) {
        log.debug("Request to partially update Product : {}", product);

        return productRepository
            .findById(product.getId())
            .map(
                existingProduct -> {
                    if (product.getName() != null) {
                        existingProduct.setName(product.getName());
                    }
                    if (product.getDecription() != null) {
                        existingProduct.setDecription(product.getDecription());
                    }
                    if (product.getUnitsOnOrder() != null) {
                        existingProduct.setUnitsOnOrder(product.getUnitsOnOrder());
                    }
                    if (product.getReorderLevel() != null) {
                        existingProduct.setReorderLevel(product.getReorderLevel());
                    }
                    if (product.getIsActive() != null) {
                        existingProduct.setIsActive(product.getIsActive());
                    }

                    return existingProduct;
                }
            )
            .map(productRepository::save);
    }

    @Transactional(readOnly = true)
    public List<Product> findAll() {
        log.debug("Request to get all Products");
        return productRepository.findAllByIsActive(true);
    }

    @Transactional(readOnly = true)
    public Optional<Product> findOne(Long id) {
        log.debug("Request to get Product : {}", id);
        return productRepository.findById(id);
    }

    public void delete(Long id) {
        log.debug("Request to delete Product : {}", id);
        Optional<Product> suppliersOptional = productRepository.findById(id);
        suppliersOptional.ifPresentOrElse(category -> {
            category.setIsActive(false);
            productRepository.save(category);
        }, () -> {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "There is no Suppliers!");
        } );
    }
}
