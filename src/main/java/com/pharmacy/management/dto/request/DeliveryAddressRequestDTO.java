package com.pharmacy.management.dto.request;

import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class DeliveryAddressRequestDTO {
    private Long id;
    @Column(name = "ship_name")
    @Size(min = 4, message = "This field minimum length: 4 characters")
    private String shipName;
    @Column(name = "ship_address")
    @Size(min = 4, message = "This field minimum length: 4 characters")
    private String shipAddress;
    @Column(name = "ship_city")
    private String shipCity;
    @Column(name = "postal_code")
    private String postalCode;
    @NotNull
    private Long countryId;
    @NotNull
    private Long cityId;
    @NotNull
    private Long regionId;
    private Double locationLat;
    private Double locationLon;
}
