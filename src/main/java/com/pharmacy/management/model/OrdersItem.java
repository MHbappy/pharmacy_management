package com.pharmacy.management.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;

/**
 * A OrdersItem.
 */
@Entity
@Table(name = "orders_item")
@Data
public class OrdersItem implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "price")
    private Double price;

    @Column(name = "is_active")
    private Boolean isActive;

    @ManyToOne
    @JsonIgnoreProperties(value = { "product", "systemUsers", "ordersItems", "shippers" }, allowSetters = true)
    private Orders orders;

    @ManyToOne
    @JsonIgnoreProperties(value = { "systemUsers", "products", "stocks", "ordersItems" }, allowSetters = true)
    private Pharmacy pharmacy;
}
