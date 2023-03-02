package com.pharmacy.management.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * A Company.
 */
@Entity
@Table(name = "company")
@Data
public class Company implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "name")
    @Size(min = 2, message = "Minimum name length: 2 characters")
    private String name;

    private String taxId;

    @Column(name = "address_1")
    private String address1;

    @Column(name = "address")
    private String address;

    @Column(name = "phone")
    private String phone;

    @Column(name = "mobile")
    private String mobile;

    @Column(name = "mobile_phone")
    private String mobilePhone;

    @Column(name = "email")
    private String email;

    @Column(name = "is_active")
    @JsonIgnore
    private Boolean isActive;

    @ManyToOne
    @JsonIgnoreProperties(value = { "suppliers", "systemUsers", "deliveryAddresses", "companies" }, allowSetters = true)
    private Country country;

    @ManyToOne
    @JsonIgnoreProperties(value = { "suppliers", "systemUsers", "deliveryAddresses", "companies" }, allowSetters = true)
    private City city;

    @ManyToOne
    @JsonIgnoreProperties(value = { "suppliers", "systemUsers", "deliveryAddresses", "companies" }, allowSetters = true)
    private Region region;
}
