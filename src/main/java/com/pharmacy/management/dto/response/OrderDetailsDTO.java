package com.pharmacy.management.dto.response;

import com.pharmacy.management.model.DeliveryAddress;
import com.pharmacy.management.projection.OrderItemsProjection;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDetailsDTO {

    //Order Details info
    private Long orderId;
    private String orderDate;
    private String shippedDate;
    private String orderNo;
    private String requiredDate;
    private Double totalPrice;
    private String deliveryStatus;

    //UserInfo
    private String userEmail;
    private String userFirstName;
    private String userLastName;

    //CompanyInfo
    private String companyName;
    private String companyTaxId;
    private String companyEmail;

    //Delivery address
    private DeliveryAddress deliveryAddress;

    //orderItems
    private List<OrderItemsProjection> orderItems;
}
