package com.pharmacy.management.repository;

import com.pharmacy.management.model.*;
import com.pharmacy.management.model.enumeration.OrderStatus;
import com.pharmacy.management.projection.OrderDetailsProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data SQL repository for the Orders entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long> {
    List<Orders> findAllByIsActive(Boolean isActive);
    List<Orders> findAllByUsers(Users users);

    Page<Orders> findAllByUsers(Users users, Pageable pageable);

    @Query(nativeQuery = true, value = "select o.id              as orderId,\n" +
            "       o.order_date      as orderDate,\n" +
            "       o.shipped_date    as shippedDate,\n" +
            "       o.order_no        as orderNo,\n" +
            "       o.required_date   as requiredDate,\n" +
            "       o.total_price     as totalPrice,\n" +
            "       u.email as userEmail,\n" +
            "       u.first_name as userFirstName,\n" +
            "       u.last_name as userLastName, \n" +
            "       o.delivery_status as deliveryStatus,\n" +
            "       c.name            as companyName,\n" +
            "       c.tax_id          as companyTaxId,\n" +
            "       c.email           as companyEmail\n" +
            "from orders o\n" +
            "         inner join users u on o.users_id = u.id\n" +
            "         inner join company c on c.id = u.company_id\n" +
            "where o.id = :orderId")
    Optional<OrderDetailsProjection> getOrderDetailsByOrderId(@Param("orderId") Long orderId);

    Page<Orders> findByOrderStatusIn(List<OrderStatus> orderStatuses, Pageable pageable);


}
