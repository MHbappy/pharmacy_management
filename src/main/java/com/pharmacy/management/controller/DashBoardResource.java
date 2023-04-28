package com.pharmacy.management.controller;


import com.pharmacy.management.dto.DashOrderBoardChat;
import com.pharmacy.management.dto.response.DashBoardInfo;
import com.pharmacy.management.projection.DashboardInfoProjection;
import com.pharmacy.management.repository.OrdersRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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



    @GetMapping("/dashboard-monthly-order")
    private List<DashOrderBoardChat> monthlyOrderChat(){
        DashOrderBoardChat dashOrderBoardChat1 = new DashOrderBoardChat();
        dashOrderBoardChat1.setLocalDate(LocalDate.now());
        dashOrderBoardChat1.setApproved(30);
        dashOrderBoardChat1.setCancelled(10);

        DashOrderBoardChat dashOrderBoardChat2 = new DashOrderBoardChat();
        dashOrderBoardChat2.setLocalDate(LocalDate.now().minusDays(2));
        dashOrderBoardChat2.setApproved(35);
        dashOrderBoardChat2.setCancelled(11);

        DashOrderBoardChat dashOrderBoardChat3 = new DashOrderBoardChat();
        dashOrderBoardChat3.setLocalDate(LocalDate.now().minusDays(5));
        dashOrderBoardChat3.setApproved(25);
        dashOrderBoardChat3.setCancelled(10);

        ArrayList<DashOrderBoardChat> dashOrderBoardChats = new ArrayList<>();
        dashOrderBoardChats.add(dashOrderBoardChat1);
        dashOrderBoardChats.add(dashOrderBoardChat2);
        dashOrderBoardChats.add(dashOrderBoardChat3);

        return dashOrderBoardChats;

    }

}
