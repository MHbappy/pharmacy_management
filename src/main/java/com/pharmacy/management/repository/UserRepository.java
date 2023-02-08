package com.pharmacy.management.repository;

import javax.transaction.Transactional;

import com.pharmacy.management.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users, Integer> {

  boolean existsByEmail(String email);

  Users findByEmail(String email);

  @Transactional
  void deleteByEmail(String email);

}
