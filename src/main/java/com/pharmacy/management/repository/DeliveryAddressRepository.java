package com.pharmacy.management.repository;

import com.pharmacy.management.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data SQL repository for the DeliveryAddress entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddress, Long> {
    List<DeliveryAddress> findAllByIsActive(Boolean isActive);
    List<DeliveryAddress> findAllByIsActiveAndUsers_Id(Boolean isActive, Long userId);
    DeliveryAddress findByIdAndUsersAndIsActive(Long id, Users users, Boolean isActive);
    @Query(nativeQuery = true, value = "select da.* from delivery_address da inner join orders o on da.users_id = o.users_id AND da.id = o.delivery_address_id where o.id = ?1")
    Optional<DeliveryAddress> getDeliveryAddressByOrderId(Long orderId);
}
