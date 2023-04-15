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
    Page<Product> findAllByIsActiveAndNameContainingIgnoreCase(Boolean isActive, String name, Pageable pageable);
    List<Product> findAllByIsActiveAndNameContaining(Boolean isActive, String name);
    List<Product> findByIdInAndIsActive(List<Long> ids, Boolean isActive);
    @Query(nativeQuery = true, value = "select id,\n" +
            "       name,\n" +
            "       product_id    as productId,\n" +
            "       reorder_level as reorderLevel,\n" +
            "       on_stock      as onStock,\n" +
            "       unit_price    as unitPrice,\n" +
            "       limit_cost    as limitCost,\n" +
            "       limit_unit    as limitUnit\n" +
            "from product\n" +
            "where concat(lower(name), lower(product_id)) like lower(:productNameOrProductId)\n" +
            "  AND is_active = true AND on_stock > 0 AND categoryId = :categoryId\n" +
            "limit 50")
    List<ProductProjection> searchProductNameAndProductId(@Param("productNameOrProductId") String productNameOrProductId, @Param("categoryId") Long categoryId);
    @Query(nativeQuery = true, value = "select sum(total_price) from orders where (order_status = 'APPROVED' OR order_status = 'PENDING') AND to_char(order_date, 'YYYY-MM-DD') like :yearMonth AND users_id = :userId")
    Double currentMonthSalesSum(@Param("yearMonth") String yearMonth, @Param("userId") Long userId);
    @Query(value = "select * from product where on_stock > 0 AND is_active = true AND name like :name", nativeQuery = true)
    List<Product> findAllOnStockProductAndNameContain(String name);
    Optional<Product> findByNameAndIsActive(String name, Boolean isActive);
    Optional<Product> findByCodeAndIsActive(String code, Boolean isActive);
    Boolean existsByNameAndIsActive(String name, Boolean isActive);
    Boolean existsByCodeAndIsActive(String code, Boolean isActive);
}
