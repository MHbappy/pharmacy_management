package com.pharmacy.management.service;

import com.pharmacy.management.config.ExcelHelper;
import com.pharmacy.management.dto.request.ProductRequestDTO;
import com.pharmacy.management.dto.request.ProductRequestExcelDTO;
import com.pharmacy.management.model.Category;
import com.pharmacy.management.model.Product;
import com.pharmacy.management.model.Suppliers;
import com.pharmacy.management.projection.ProductProjection;
import com.pharmacy.management.repository.CategoryRepository;
import com.pharmacy.management.repository.ProductRepository;
import com.pharmacy.management.repository.SuppliersRepository;
import lombok.AllArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@Transactional
@AllArgsConstructor
public class ProductService {
    private final Logger log = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SuppliersRepository suppliersRepository;
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
        Product product1 = productRepository.save(product);
        product1.setProductId("PROD-23-" + product1.getId());
        return productRepository.save(product1);
    }

    public ResponseEntity<?> saveProductWithExcel(List<ProductRequestExcelDTO> productRequestDTO) {
        log.debug("Request to save Product : {}", productRequestDTO);

        List<Product> productList = new ArrayList<>();
        for (ProductRequestExcelDTO prodDto : productRequestDTO){
            Product product = modelMapper.map(prodDto, Product.class);
            product.setIsActive(true);

            if (prodDto.getCategoryName() == null || prodDto.getCategoryName().isEmpty()){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category name can not be empty");
            }
            if (prodDto.getSupplierName() == null || prodDto.getSupplierName().isEmpty()){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Supplier name can not be empty");
            }

            Boolean isExistProduct = productRepository.existsByCodeAndIsActive(prodDto.getCode(), true);
            if (isExistProduct){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The name of product should be unique. Check with" + prodDto.getName());
            }

            Optional<Category> categoryOptional = categoryRepository.findByNameAndIsActive(prodDto.getCategoryName(), true);
            if (!categoryOptional.isPresent()){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category is not found. Check with " + prodDto.getCategoryName());
            }

            Optional<Suppliers> suppliersOptional = suppliersRepository.findByCompanyNameAndIsActive(prodDto.getSupplierName(), true);
            if (!suppliersOptional.isPresent()){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Supplier is not found. Check with " + prodDto.getSupplierName());
            }

            product.setUnitsOnOrder(0);
            product.setOnStock(0);
            product.setReorderLevel(0);
            product.setUnitsOnOrder(0);
            product.setCategory(categoryOptional.get());
            product.setSuppliers(suppliersOptional.get());
            productList.add(product);
        }

        List<Product> savedProductList = productRepository.saveAll(productList);
        List<Product> afterGeneratedProductId = new ArrayList<>();

        for(Product prd : savedProductList){
            prd.setProductId("PROD-23-" + prd.getId());
            afterGeneratedProductId.add(prd);
        }
        productRepository.saveAll(afterGeneratedProductId);
        return ResponseEntity.ok(true);
    }

    public List<ProductRequestExcelDTO> getProductFromFile(MultipartFile file) {
        try {
            String fileName = file.getOriginalFilename();
            String ext = FilenameUtils.getExtension(fileName);
            List<ProductRequestExcelDTO> userRoll = ExcelHelper.excelToProduct(file.getInputStream(), ext);
            return userRoll;
        } catch (IOException e) {
            throw new RuntimeException("fail to store excel data: " + e.getMessage());
        }
    }

//    public Optional<Product> partialUpdate(Product product) {
//        log.debug("Request to partially update Product : {}", product);
//
//        return productRepository
//            .findById(product.getId())
//            .map(
//                existingProduct -> {
//                    if (product.getName() != null) {
//                        existingProduct.setName(product.getName());
//                    }
//                    if (product.getDecription() != null) {
//                        existingProduct.setDecription(product.getDecription());
//                    }
//                    if (product.getUnitsOnOrder() != null) {
//                        existingProduct.setUnitsOnOrder(product.getUnitsOnOrder());
//                    }
//                    if (product.getReorderLevel() != null) {
//                        existingProduct.setReorderLevel(product.getReorderLevel());
//                    }
//                    if (product.getIsActive() != null) {
//                        existingProduct.setIsActive(product.getIsActive());
//                    }
//
//                    return existingProduct;
//                }
//            )
//            .map(productRepository::save);
//    }


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

    public List<ProductProjection> searchProductNameAndProductId(String productNameOrProductId){
        return productRepository.searchProductNameAndProductId("%" + productNameOrProductId + "%");
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
