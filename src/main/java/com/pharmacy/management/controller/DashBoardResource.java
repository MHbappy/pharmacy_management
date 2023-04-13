package com.pharmacy.management.controller;


import com.pharmacy.management.dto.response.DashBoardInfo;
import com.pharmacy.management.projection.DashboardInfoProjection;
import com.pharmacy.management.repository.OrdersRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class DashBoardResource {

    private final OrdersRepository ordersRepository;

    @GetMapping("/dashboard-info")
    public DashBoardInfo showDashBoardInfo(){
        DashboardInfoProjection dashboardInfoProjection = ordersRepository.getDashBoardInfoProjection();
        DashBoardInfo dashBoardInfo = new DashBoardInfo();
        dashBoardInfo.setTotalUsers(dashboardInfoProjection.getUserCount());
        dashBoardInfo.setTotalOrder(dashboardInfoProjection.getTotalOrder());
        dashBoardInfo.setTotalCompany(dashboardInfoProjection.getTotalCompany());
        dashBoardInfo.setTotalProducts(dashboardInfoProjection.getTotalProduct());
        dashBoardInfo.setTotalPendingOrder(dashboardInfoProjection.getOrderPending());
        dashBoardInfo.setTotalApprovedOrder(dashboardInfoProjection.getApprovedOrder());
        dashBoardInfo.setTotalCancelOrder(dashboardInfoProjection.getOrderCancel());
        dashBoardInfo.setTotalDeliveredOrder(dashboardInfoProjection.getOrderDeliverd());
        return dashBoardInfo;
    }

}
