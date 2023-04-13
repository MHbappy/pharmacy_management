package com.pharmacy.management.projection;

public interface DashboardInfoProjection {
    int getUserCount();
    int getTotalOrder();
    int getTotalProduct();
    int getTotalCompany();
    int getApprovedOrder();
    int getOrderCancel();
    int getOrderDeliverd();
    int getOrderPending();
}
