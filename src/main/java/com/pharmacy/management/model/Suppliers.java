package com.pharmacy.management.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;

/**
 * A Suppliers.
 */
@Entity
@Table(name = "suppliers")
@Data
public class Suppliers implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "contact_title")
    private String contactTitle;

    @Column(name = "address")
    private String address;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "phone")
    private String phone;

    @Column(name = "fax")
    private String fax;

    @Column(name = "is_active")
    private Boolean isActive;

    @ManyToOne
    @JsonIgnoreProperties(value = { "suppliers", "systemUsers", "deliveryAddresses" }, allowSetters = true)
    private Country country;

    @ManyToOne
    @JsonIgnoreProperties(value = { "suppliers", "systemUsers", "deliveryAddresses" }, allowSetters = true)
    private City city;

    @ManyToOne
    @JsonIgnoreProperties(value = { "suppliers", "systemUsers", "deliveryAddresses" }, allowSetters = true)
    private Region region;
}
