package com.pharmacy.management.service;

import com.pharmacy.management.model.Product;
import com.pharmacy.management.model.Stock;
import com.pharmacy.management.model.enumeration.InOutStatus;
import com.pharmacy.management.repository.ProductRepository;
import com.pharmacy.management.repository.StockRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class StockService {

    private final Logger log = LoggerFactory.getLogger(StockService.class);
    private final StockRepository stockRepository;
    private final ProductRepository productRepository;


    public Stock save(Stock stock) {
        log.debug("Request to save Stock : {}", stock);
        stock.setIsActive(true);
        stock.setAddedDateTime(LocalDateTime.now());

        if (stock.getProduct() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product not found");
        }

        Optional<Product> productOptional = productRepository.findById(stock.getProduct().getId());
        if (!productOptional.isPresent()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product not found");
        }
        Product product = productOptional.get();

        if (stock.getId() == null){
            if (stock.getInOutStatus().equals(InOutStatus.IN)){
                Integer currentProductStock = (product.getOnStock() == null ? 0 : product.getOnStock()) + stock.getQuantity();
                product.setOnStock(currentProductStock);
                productRepository.save(product);
            }

            if (stock.getInOutStatus().equals(InOutStatus.OUT)){
                Integer currentProductStock = (product.getOnStock() == null ? 0 : product.getOnStock()) - stock.getQuantity();
                if (currentProductStock < 0){
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Full stock can not be negative");
                }
                product.setOnStock(currentProductStock);
                productRepository.save(product);
            }
        }

        return stockRepository.save(stock);
    }

    
    public Optional<Stock> partialUpdate(Stock stock) {
        log.debug("Request to partially update Stock : {}", stock);

        return stockRepository
            .findById(stock.getId())
            .map(
                existingStock -> {
                    if (stock.getQuantity() != null) {
                        existingStock.setQuantity(stock.getQuantity());
                    }
                    if (stock.getTotalPrice() != null) {
                        existingStock.setTotalPrice(stock.getTotalPrice());
                    }
                    if (stock.getInOutStatus() != null) {
                        existingStock.setInOutStatus(stock.getInOutStatus());
                    }

                    return existingStock;
                }
            )
            .map(stockRepository::save);
    }

    
    @Transactional(readOnly = true)
    public List<Stock> findAll() {
        log.debug("Request to get all Stocks");
        return stockRepository.findAll();
    }


    public Page<Stock> findAllWithPagination(Pageable pageable) {
        log.debug("Request to get all Stocks");
        return stockRepository.findAllByIsActive(true, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Stock> findAllWithPaginationAndStock(String productId, Pageable pageable) {
        log.debug("Request to get all Stocks");
        return stockRepository.findAllByIsActiveAndProduct_ProductId(true, productId, pageable);
    }



    @Transactional(readOnly = true)
    public Optional<Stock> findOne(Long id) {
        log.debug("Request to get Stock : {}", id);
        return stockRepository.findById(id);
    }

    
    public void delete(Long id) {
        log.debug("Request to delete Stock : {}", id);

        Optional<Stock> stockOptional = stockRepository.findById(id);
        stockOptional.ifPresentOrElse(stock -> {
            stock.setIsActive(false);
            stockRepository.save(stock);
        }, () -> {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "There is no region!");
        } );
    }
}
