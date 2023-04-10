package com.pharmacy.management.repository;

import com.pharmacy.management.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Page<Category> findAllByIsActive(Boolean isActive, Pageable pageable);
    List<Category> findAllByIsActive(Boolean isActive);
    Optional<Category> findByNameAndIsActive(String name, Boolean isActive);
    Boolean existsByNameAndIsActive(String name, Boolean isActive);
}
