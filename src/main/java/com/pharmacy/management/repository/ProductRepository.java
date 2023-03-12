package com.pharmacy.management.repository;

import com.pharmacy.management.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data SQL repository for the Product entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findAllByIsActiveAndNameContaining(Boolean isActive, String name, Pageable pageable);
    List<Product> findAllByIsActiveAndNameContaining(Boolean isActive, String name);
    List<Product> findByIdInAndIsActive(List<Long> ids, Boolean isActive);
}
