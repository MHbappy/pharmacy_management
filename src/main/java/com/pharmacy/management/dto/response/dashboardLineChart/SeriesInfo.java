package com.pharmacy.management.dto.response.dashboardLineChart;
import lombok.Data;
import java.util.List;

@Data
public class SeriesInfo {
    List<Series> series;
    String xaxis[] = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    String yaxis = "Sales";
}
