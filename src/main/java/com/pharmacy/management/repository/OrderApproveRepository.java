package com.pharmacy.management.repository;

import com.pharmacy.management.model.OrderApprove;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderApproveRepository extends JpaRepository<OrderApprove, Long> {
    List<OrderApprove> findAllByOrders_Id(Long orderId);
}
