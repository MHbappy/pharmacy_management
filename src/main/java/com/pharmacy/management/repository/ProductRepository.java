package com.pharmacy.management.repository;

import com.pharmacy.management.model.*;
import com.pharmacy.management.projection.ProductProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data SQL repository for the Product entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findAllByIsActiveAndNameContaining(Boolean isActive, String name, Pageable pageable);
    List<Product> findAllByIsActiveAndNameContaining(Boolean isActive, String name);
    List<Product> findByIdInAndIsActive(List<Long> ids, Boolean isActive);
    @Query(nativeQuery = true, value = "select id, name, product_id as productId from product where concat(lower(name), lower(product_id)) like lower(?1) AND is_active = true  limit 10")
    List<ProductProjection> searchProductNameAndProductId(String productNameOrProductId);
    @Query(nativeQuery = true, value = "select sum(total_price) from orders where (delivery_status = 'APPROVED' OR delivery_status = 'PENDING') AND to_char(order_date, 'YYYY-MM-DD') like :yearMonth AND users_id = :userId")
    Double currentMonthSalesSum(@Param("yearMonth") String yearMonth, @Param("userId") Long userId);
}
