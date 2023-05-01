package com.pharmacy.management.dto.response.dashboardLineChart;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Series {
    private String name;
    private int data[];
}
