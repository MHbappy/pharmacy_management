package com.pharmacy.management.projection;

public interface OrderItemsProjection {
    Long getId();
    String getProductName();
    String getProductId();
    Double getPrice();
    Integer getUnit();
}
