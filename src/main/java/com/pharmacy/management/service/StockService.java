package com.pharmacy.management.service;


import com.pharmacy.management.model.Stock;
import com.pharmacy.management.repository.StockRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StockService {

    private final Logger log = LoggerFactory.getLogger(StockService.class);

    private final StockRepository stockRepository;

    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    
    public Stock save(Stock stock) {
        log.debug("Request to save Stock : {}", stock);
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
                    if (stock.getUnit() != null) {
                        existingStock.setUnit(stock.getUnit());
                    }
                    if (stock.getPrice() != null) {
                        existingStock.setPrice(stock.getPrice());
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

    
    @Transactional(readOnly = true)
    public Optional<Stock> findOne(Long id) {
        log.debug("Request to get Stock : {}", id);
        return stockRepository.findById(id);
    }

    
    public void delete(Long id) {
        log.debug("Request to delete Stock : {}", id);
        stockRepository.deleteById(id);
    }
}