package com.pharmacy.management.repository;

import com.pharmacy.management.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data SQL repository for the Orders entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long> {
    List<Orders> findAllByIsActive(Boolean isActive);
    List<Orders> findAllByUsers(Users users);

    Page<Orders> findAllByUsers(Users users, Pageable pageable);
}
