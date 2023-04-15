package com.pharmacy.management.repository;

import com.pharmacy.management.model.*;
import com.pharmacy.management.model.enumeration.OrderStatus;
import com.pharmacy.management.projection.DashboardInfoProjection;
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
    Page<Orders> findAllByUsersAndOrderNo(Users users, String orderNo, Pageable pageable);
    Page<Orders> findAll(Pageable pageable);
    Page<Orders> findAllByOrderNo(String orderNo, Pageable pageable);
    @Query(nativeQuery = true, value = "select o.id              as orderId,\n" +
            "       o.order_date      as orderDate,\n" +
            "       o.shipped_date    as shippedDate,\n" +
            "       o.order_no        as orderNo,\n" +
            "       o.required_date   as requiredDate,\n" +
            "       o.total_price     as totalPrice,\n" +
            "       u.email as userEmail,\n" +
            "       u.first_name as userFirstName,\n" +
            "       u.last_name as userLastName, \n" +
            "       o.order_status as deliveryStatus,\n" +
            "       c.name            as companyName,\n" +
            "       c.tax_id          as companyTaxId,\n" +
            "       c.email           as companyEmail\n" +
            "from orders o\n" +
            "         inner join users u on o.users_id = u.id\n" +
            "         inner join company c on c.id = u.company_id\n" +
            "where o.id = :orderId")
    Optional<OrderDetailsProjection> getOrderDetailsByOrderId(@Param("orderId") Long orderId);

    @Query(value = "WITH userCount AS (select count(id) as userCount from users)\n" +
            ", totalOrder AS (select count(id) as totalOrder from orders)\n" +
            ", totalProduct AS (select count(id) as totalProduct from product where is_active = true)\n" +
            ", totalCompany AS (select count(id) as totalCompany from company where is_active = true)\n" +
            ", approvedOrder AS (select count(id) as approvedOrder from orders where order_status = 'APPROVED')\n" +
            ", orderCancel AS (select count(id) as orderCancel from orders where order_status = 'CANCELLED')\n" +
            ", orderDeliverd AS (select count(id) as orderDeliverd from orders where order_status = 'DELIVERED')\n" +
            ", orderPending AS (select count(id) as orderPending from orders where order_status = 'PENDING')\n" +
            "select *\n" +
            "from userCount, totalOrder, totalProduct, totalCompany, approvedOrder, orderCancel, orderDeliverd, orderPending", nativeQuery = true)
    DashboardInfoProjection getDashBoardInfoProjection();
    Page<Orders> findByOrderStatusIn(List<OrderStatus> orderStatuses, Pageable pageable);

    @Query(nativeQuery = true, value = "select o.*\n" +
            "from orders o\n" +
            "         inner join users u on o.users_id = u.id\n" +
            "where u.company_id = :companyId")
    Page<Orders> findAllOrdersByCompanyId(@Param("companyId") Long companyId, Pageable pageable);

    @Query(nativeQuery = true, value = "select o.*\n" +
            "from orders o\n" +
            "         inner join users u on o.users_id = u.id\n" +
            "where u.company_id = :companyId\n" +
            "  AND o.order_status = :orderStatus")
    Page<Orders> findAllCompanyIdAndStatus(@Param("companyId") Long companyId, @Param("orderStatus") String orderStatus, Pageable pageable);

    @Query(nativeQuery = true, value = "select o.*\n" +
            "from orders o\n" +
            "         inner join users u on o.users_id = u.id\n" +
            "where u.company_id = :companyId\n" +
            "  AND o.order_status = :orderStatus AND order_date between :startDate AND :endDate")
    Page<Orders> findAllCompanyIdAndStatusAndDate(@Param("companyId") Long companyId, @Param("orderStatus") String orderStatus, @Param("startDate") String startDate, @Param("startDate") String endDate,  Pageable pageable);

}
