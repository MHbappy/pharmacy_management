package com.pharmacy.management.repository;

import com.pharmacy.management.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the OrdersItem entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrdersItemRepository extends JpaRepository<OrdersItem, Long> {}
