package com.pharmacy.management.dto.response;

import lombok.Data;

@Data
public class OrderDetailsItemsInfo {
    Long id;
    String productName;
    String productId;
    Double price;
    Integer unit;
    String userName;
    private String mobilePhone;
    private String email;
}
