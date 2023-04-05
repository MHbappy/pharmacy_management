package com.pharmacy.management.dto.response;
import lombok.Data;


@Data
public class DashBoardInfo {

    //only admin and other system users show
    private Integer totalUsers;
    private Integer totalProducts;
    private Integer totalCompany;


    //All employee show
    private Integer totalPendingOrder;
    private Integer totalApprovedOrder;
    private Integer totalCancelOrder;
    private Integer totalDeliveredOrder;
    private Integer totalOrder;
}
