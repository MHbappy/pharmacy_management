package com.pharmacy.management.repository;

import com.pharmacy.management.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Stock entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    Page<Stock> findAllByIsActive(Boolean isActive, Pageable pageable);
    Page<Stock> findAllByIsActiveAndProduct_ProductId(Boolean isActive, String productId, Pageable pageable);
}
