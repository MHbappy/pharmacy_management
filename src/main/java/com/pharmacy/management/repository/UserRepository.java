package com.pharmacy.management.repository;

import javax.transaction.Transactional;

import com.pharmacy.management.model.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users, Long> {
  boolean existsByEmail(String email);

  Page<Users> findAllByCompany_Id(Long companyId, Pageable pageable);

  Page<Users> findAllByIsActiveAndEmailContainingIgnoreCase(Boolean isActive, String email, Pageable pageable);

  Users findByEmail(String email);

  @Transactional
  void deleteByEmail(String email);

}
