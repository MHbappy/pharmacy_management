package com.pharmacy.management.dto.request;

import lombok.Data;
import lombok.ToString;
import javax.validation.constraints.NotNull;

@Data
@ToString
public class ProductRequestDTO {
    private Long id;
    @NotNull
    private String name;
    @NotNull
    private String code;
//    @NotNull
//    private String productId;
    private String description;
    private String strength;
    private String stsMedicine;
    @NotNull
    private Long categoryId;
    private Long supplierId;
//    @Enumerated(EnumType.STRING)
//    private OrderType orderType;
    @NotNull
    private Integer unitPrice;
    private Integer limitCost;
    private Integer limitUnit;
    private byte[] file;
}
