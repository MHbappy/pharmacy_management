package com.pharmacy.management.dto.request;
import lombok.Data;

import java.util.List;

@Data
public class OrderPlaceRequest {
    List<OrderPlaceProductDto> productAndQuantityList;
    Long categoryId;
    Long diagnosis;
    Long deliveryAddressId;
}
