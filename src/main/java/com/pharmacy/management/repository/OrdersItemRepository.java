package com.pharmacy.management.repository;

import com.pharmacy.management.model.*;
import com.pharmacy.management.projection.OrderDetailsProjection;
import com.pharmacy.management.projection.OrderItemsProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data SQL repository for the OrdersItem entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrdersItemRepository extends JpaRepository<OrdersItem, Long> {
    List<OrdersItem> findAllByOrders_Id(Long id);
    @Query(nativeQuery = true, value = "select ot.id, ot.price, ot.unit, p.name as productName, p.product_id as productId from orders_item ot inner join product p on p.id = ot.product_id where orders_id = ?1")
    List<OrderItemsProjection> getAllOrderItemByOrderId(Long productId);



}
