package com.pharmacy.management.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;

/**
 * A Product.
 */
@Entity
@Table(name = "product")
@Data
public class Product implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "decription")
    private String decription;

    @Column(name = "units_on_order")
    private Integer unitsOnOrder;

    @Column(name = "reorder_level")
    private Integer reorderLevel;

    @Column(name = "is_active")
    private Boolean isActive;

    @ManyToOne
    @JsonIgnoreProperties(value = { "products" }, allowSetters = true)
    private Category category;

    @ManyToOne
    @JsonIgnoreProperties(value = { "country", "city", "region", "products" }, allowSetters = true)
    private Suppliers suppliers;

    @ManyToOne
    @JsonIgnoreProperties(value = { "systemUsers", "products", "stocks", "ordersItems" }, allowSetters = true)
    private Pharmacy pharmacy;
}
