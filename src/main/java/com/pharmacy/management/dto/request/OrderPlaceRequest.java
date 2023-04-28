package com.pharmacy.management.dto.request;
import lombok.Data;

import java.util.List;

@Data
public class OrderPlaceRequest {
    String paymentToken;
    List<OrderPlaceProductDto> productAndQuantityList;
    Long categoryId;
    Long providerId;
    Long diagnosis;
    Long deliveryAddressId;
}
