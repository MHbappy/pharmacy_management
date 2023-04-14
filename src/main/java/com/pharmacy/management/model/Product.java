package com.pharmacy.management.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pharmacy.management.model.enumeration.OrderType;
import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
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

    @Column(name = "code")
    private String code;

    @Column(name = "product_id")
    private String productId;

    @Column(name = "description")
    private String description;

    @Column(name = "strength")
    private String strength;

    @Column(name = "sts_medicine")
    private String stsMedicine;

    @Column(name = "units_on_order")
    private Integer unitsOnOrder  = 0;

//    @Column(name = "order_type")
//    @Enumerated(EnumType.STRING)
//    private OrderType orderType;

    @Column(name = "reorder_level")
    private Integer reorderLevel  = 0;

    @Column(name = "onStock")
    private Integer onStock = 0;

    @Column(name = "unit_price")
    private Double unitPrice = 0d;

    @Column(name = "limit_cost")
    private Integer limitCost  = 0;

    @Column(name = "limit_unit")
    private Integer limitUnit  = 0;

    @Column(name = "file")
    @Lob
    private byte[] file;

    @Column(name = "is_active")
    @JsonIgnore
    private Boolean isActive;

    @ManyToOne
    @JsonIgnoreProperties(value = { "isActive" }, allowSetters = true)
    private Category category;

    @ManyToOne
    @JsonIgnoreProperties(value = { "country", "city", "region", "products" }, allowSetters = true)
    private Suppliers suppliers;

//    @ManyToOne
//    @JsonIgnoreProperties(value = { "country", "city", "region"}, allowSetters = true)
//    private Company company;

//    @ManyToOne
//    @JsonIgnoreProperties(value = { "systemUsers", "products", "stocks", "ordersItems" }, allowSetters = true)
//    private Pharmacy pharmacy;
}
