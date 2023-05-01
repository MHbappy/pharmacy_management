package com.pharmacy.management.controller;


import com.pharmacy.management.dto.DashOrderBoardChat;
import com.pharmacy.management.dto.response.DashBoardInfo;
import com.pharmacy.management.dto.response.dashboardLineChart.Series;
import com.pharmacy.management.dto.response.dashboardLineChart.SeriesInfo;
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
    private SeriesInfo monthlyOrderChat(){
        SeriesInfo seriesInfo = new SeriesInfo();
        ArrayList<Series> series = new ArrayList<>();

        Series seriesPending = new Series("PENDING", new int[]{83, 113, 85, 116, 61, 76, 118, 51, 65, 20, 200, 45});
        Series seriesApproved = new Series("APPROVED", new int[]{50, 100, 80, 110, 50, 60, 110, 50, 60, 150, 190, 40});
        Series seriesDelivered = new Series("DELIVERED", new int[]{45, 90, 70, 110, 50, 60, 110, 50, 60, 140, 150, 30});
        Series seriesDenied = new Series("DENIED", new int[]{45, 90, 70, 110, 50, 80, 110, 500, 60, 700, 150, 30});
        Series seriesCancelled = new Series("CANCELLED", new int[]{45, 90, 300, 110, 50, 90, 110, 50, 60, 140, 150, 30});

        series.add(seriesPending);
        series.add(seriesApproved);
        series.add(seriesDelivered);
        series.add(seriesDenied);
        series.add(seriesCancelled);
        seriesInfo.setSeries(series);




//        DashOrderBoardChat dashOrderBoardChat1 = new DashOrderBoardChat();
//        dashOrderBoardChat1.setLocalDate(LocalDate.now());
//        dashOrderBoardChat1.setApproved(30);
//        dashOrderBoardChat1.setCancelled(10);
//
//        DashOrderBoardChat dashOrderBoardChat2 = new DashOrderBoardChat();
//        dashOrderBoardChat2.setLocalDate(LocalDate.now().minusDays(2));
//        dashOrderBoardChat2.setApproved(35);
//        dashOrderBoardChat2.setCancelled(11);
//
//        DashOrderBoardChat dashOrderBoardChat3 = new DashOrderBoardChat();
//        dashOrderBoardChat3.setLocalDate(LocalDate.now().minusDays(5));
//        dashOrderBoardChat3.setApproved(25);
//        dashOrderBoardChat3.setCancelled(10);
//
//        ArrayList<DashOrderBoardChat> dashOrderBoardChats = new ArrayList<>();
//        dashOrderBoardChats.add(dashOrderBoardChat1);
//        dashOrderBoardChats.add(dashOrderBoardChat2);
//        dashOrderBoardChats.add(dashOrderBoardChat3);

        return seriesInfo;

    }

}
