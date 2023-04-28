package com.pharmacy.management.repository;

import com.pharmacy.management.model.Provider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data SQL repository for the Provider entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProviderRepository extends JpaRepository<Provider, Long> {
    Page<Provider> findAllByIsActive(Boolean isActive, Pageable pageable);
    List<Provider> findAllByIsActive(Boolean isActive);
    @Query(nativeQuery = true, value = "select distinct p.* from provider p, provider_category pc where category_id = ?1 AND p.is_active = true")
    List<Provider> findAllProviderListByCategoryId(Long categoryId);
}
