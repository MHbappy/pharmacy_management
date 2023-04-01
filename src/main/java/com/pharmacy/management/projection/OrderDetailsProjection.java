package com.pharmacy.management.projection;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface OrderDetailsProjection {
    Long getOrderId();
    LocalDateTime getOrderDate();
    LocalDate getShippedDate();
    String getOrderNo();
    LocalDate getRequiredDate();
    Double getTotalPrice();
    String getDeliveryStatus();

    //UserInfo
    String getUserEmail();
    String getUserFirstName();
    String getUserLastName();

    //CompanyInfo
    String getCompanyName();
    String getCompanyTaxId();
    String getCompanyEmail();
}
