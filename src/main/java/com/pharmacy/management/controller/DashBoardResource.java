package com.pharmacy.management.controller;


import com.pharmacy.management.dto.response.DashBoardInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DashBoardResource {

    @GetMapping("/dashboard-info")
    public DashBoardInfo showDashBoardInfo(){
        DashBoardInfo dashBoardInfo = new DashBoardInfo();
        dashBoardInfo.setTotalUsers(2);
        dashBoardInfo.setTotalOrder(8);
        dashBoardInfo.setTotalCompany(2);
        dashBoardInfo.setTotalProducts(5);

        dashBoardInfo.setTotalPendingOrder(5);
        dashBoardInfo.setTotalApprovedOrder(5);
        dashBoardInfo.setTotalCancelOrder(4);
        dashBoardInfo.setTotalDeliveredOrder(2);
        return dashBoardInfo;
    }

}
