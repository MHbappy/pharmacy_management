package com.pharmacy.management.repository;

import com.pharmacy.management.model.StripePaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StripePaymentHistoryRepository extends JpaRepository<StripePaymentHistory, Long> {
}
