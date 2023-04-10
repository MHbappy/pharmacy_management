package com.pharmacy.management.dto.request;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ProductRequestExcelDTO {
        private Long id;
        private String name;
        private String code;
        private String description;
        private String strength;
        private String stsMedicine;
        private String categoryName;
        private String supplierName;
        private Double unitPrice = 0d;
        private Integer limitCost = 0;
        private Integer limitUnit = 0;
        private byte[] file;
}
