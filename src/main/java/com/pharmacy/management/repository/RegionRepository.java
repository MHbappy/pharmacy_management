package com.pharmacy.management.repository;

import com.pharmacy.management.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data SQL repository for the Region entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {
    List<Region> findAllByCity_IdAndCity_IsActiveAndIsActive(Long cityId, Boolean cityIsActive, Boolean regionIsActive);
}
