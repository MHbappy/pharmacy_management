package com.pharmacy.management.dto.request;

import com.pharmacy.management.model.enumeration.OrderType;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

@Data
@ToString
public class ProductRequestDTO {
    private Long id;
    @NotNull
    private String name;
    @NotNull
    private String code;
    @NotNull
    private String productId;
    private String decription;
    private String strength;
    private String stsMedicine;
    private Long categoryId;
    private Long supplierId;
    @Enumerated(EnumType.STRING)
    private OrderType orderType;

//    private Integer unitsOnOrder;
//    private Integer reorderLevel;
//    @Column(name = "order_type")
//    @Enumerated(EnumType.STRING)
//    private OrderType orderType;
}
