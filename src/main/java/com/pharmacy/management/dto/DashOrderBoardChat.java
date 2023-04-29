package com.pharmacy.management.dto;

import lombok.Data;

import java.time.LocalDate;


@Data
public class DashOrderBoardChat {
    LocalDate localDate;
    Integer pending = 45 ;
    Integer approved = 0;
    Integer delivered = 0;
    Integer denied = 10;
    Integer cancelled = 58;

}
