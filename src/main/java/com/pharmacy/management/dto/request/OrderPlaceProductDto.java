package com.pharmacy.management.dto.request;


import lombok.Data;

@Data
public class OrderPlaceProductDto {
    Long productId;
    Integer quantity = 0;
}
