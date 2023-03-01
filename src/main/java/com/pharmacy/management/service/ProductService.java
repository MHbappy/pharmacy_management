package com.pharmacy.management.service;

import com.pharmacy.management.dto.request.ProductRequestDTO;
import com.pharmacy.management.model.Category;
import com.pharmacy.management.model.Product;
import com.pharmacy.management.model.Suppliers;
import com.pharmacy.management.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;


@Service
@Transactional
@AllArgsConstructor
public class ProductService {
    private final Logger log = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    public Product save(ProductRequestDTO productRequestDTO) {
        log.debug("Request to save Product : {}", productRequestDTO);
        Product product = modelMapper.map(productRequestDTO, Product.class);
        product.setIsActive(true);

        if (productRequestDTO.getCategoryId() != null){
            product.setCategory(new Category(productRequestDTO.getCategoryId()));
        }
        if (productRequestDTO.getSupplierId() != null){
            product.setSuppliers(new Suppliers(productRequestDTO.getSupplierId()));
        }
        //when new product create only
        if (productRequestDTO.getId() == null || productRequestDTO.getId().equals(0)){
            product.setReorderLevel(0);
            product.setUnitsOnOrder(0);
        }
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
    public Page<Product> findAllByName(@RequestParam(name = "name", defaultValue = "") String name, Pageable pageable) {
        log.debug("Request to get all Products");
        return productRepository.findAllByIsActiveAndNameContaining(true, name, pageable);
    }



    @Transactional(readOnly = true)
    public List<Product> findAllByName(@RequestParam(name = "name", defaultValue = "") String name) {
        log.debug("Request to get all Products");
        return productRepository.findAllByIsActiveAndNameContaining(true, name);
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
        });
    }
}
