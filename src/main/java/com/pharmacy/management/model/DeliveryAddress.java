package com.pharmacy.management.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * A DeliveryAddress.
 */
@Entity
@Table(name = "delivery_address")
@Data
public class DeliveryAddress implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "ship_name")
    @Size(min = 4, message = "This field minimum length: 4 characters")
    private String shipName;

    @Column(name = "ship_address")
    @Size(min = 4, message = "This field minimum length: 4 characters")
    private String shipAddress;

    @Column(name = "ship_city")
    private String shipCity;

    private Double locationLat;

    private Double locationLon;

    @Column(name = "postal_code")
    private String postalCode;

    @ManyToOne
//    @JsonIgnoreProperties(value = { "company"}, allowSetters = true)
    @JsonIgnore
    private Users users;

    @ManyToOne
    @JsonIgnoreProperties(value = { "suppliers", "systemUsers", "deliveryAddresses" }, allowSetters = true)
    private  Country country;

    @ManyToOne
    @JsonIgnoreProperties(value = { "suppliers", "systemUsers", "deliveryAddresses" }, allowSetters = true)
    private City city;

    @ManyToOne
    @JsonIgnoreProperties(value = { "suppliers", "systemUsers", "deliveryAddresses" }, allowSetters = true)
    private Region region;

    Boolean isActive;
}
