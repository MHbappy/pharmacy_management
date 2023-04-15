package com.pharmacy.management.projection;

public interface ProductProjection {
    Long getId();
    String getName();
    String getProductId();
    Integer getReorderLevel();
    Integer getOnStock();
    Double getUnitPrice();
    Integer getLimitCost();
    Integer getLimitUnit();
}
