package com.pharmacy.management.controller;

import com.pharmacy.management.model.Stock;
import com.pharmacy.management.repository.StockRepository;
import com.pharmacy.management.service.StockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
public class StockResource {

    private final Logger log = LoggerFactory.getLogger(StockResource.class);

    private final StockService stockService;

    private final StockRepository stockRepository;

    public StockResource(StockService stockService, StockRepository stockRepository) {
        this.stockService = stockService;
        this.stockRepository = stockRepository;
    }

    @PostMapping("/stocks")
    public ResponseEntity<Stock> createStock(@RequestBody Stock stock) throws URISyntaxException {
        log.debug("REST request to save Stock : {}", stock);
        if (stock.getId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A new stock cannot already have an ID");
        }
        Stock result = stockService.save(stock);
        return ResponseEntity
            .created(new URI("/api/stocks/" + result.getId()))
            .body(result);
    }

    @PutMapping("/stocks/{id}")
    public ResponseEntity<Stock> updateStock(@PathVariable(value = "id", required = false) final Long id, @RequestBody Stock stock)
         {
        log.debug("REST request to update Stock : {}, {}", id, stock);
        if (stock.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid id");
        }
        if (!Objects.equals(id, stock.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid id");
        }
        if (!stockRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Entity not found");
        }

        Stock result = stockService.save(stock);
        return ResponseEntity
            .ok()
            .body(result);
    }

    @GetMapping("/stocks")
    public Page<Stock> getAllStocks(Pageable pageable) {
        log.debug("REST request to get all Stocks");
        return stockService.findAllWithPagination(pageable);
    }

    @GetMapping("/stocks-by-product-id")
    public Page<Stock> getAllStocks(@RequestParam Long productId, Pageable pageable) {
        log.debug("REST request to get all Stocks");
        return stockService.findAllWithPaginationAndStock(productId, pageable);
    }

    @GetMapping("/stocks/{id}")
    public ResponseEntity<Stock> getStock(@PathVariable Long id) {
        log.debug("REST request to get Stock : {}", id);
        Optional<Stock> stock = stockService.findOne(id);
        return ResponseEntity.ok(stock.get());
    }


    @DeleteMapping("/stocks/{id}")
    public ResponseEntity<Void> deleteStock(@PathVariable Long id) {
        log.debug("REST request to delete Stock : {}", id);
        stockService.delete(id);
        return ResponseEntity
            .noContent()
            .build();
    }
}
