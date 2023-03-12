package com.pharmacy.management.repository;

import com.pharmacy.management.model.*;
import com.pharmacy.management.projection.OrderItemsProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data SQL repository for the OrdersItem entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrdersItemRepository extends JpaRepository<OrdersItem, Long> {
    List<OrdersItem> findAllByOrders_Id(Long id);


    @Query(nativeQuery = true, value = "select id, price, unit from orders_item where orders_id = ?1")
    List<OrderItemsProjection> getAllOrderItemByOrderId(Long productId);
}
