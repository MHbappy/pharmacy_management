package com.pharmacy.management.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pharmacy.management.model.enumeration.InOutStatus;
import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;

/**
 * A Stock.
 */
@Entity
@Table(name = "stock")
@Data
public class Stock implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "unit")
    private Integer unit;

    @Column(name = "price")
    private Integer price;

    @Column(name = "total_price")
    private Double totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "in_out_status")
    private InOutStatus inOutStatus;

    @ManyToOne
    @JsonIgnoreProperties(value = { "category", "suppliers", "pharmacy", "stocks", "orders" }, allowSetters = true)
    private Product product;

    @ManyToOne
    @JsonIgnoreProperties(value = { "systemUsers", "products", "stocks", "ordersItems" }, allowSetters = true)
    private Pharmacy pharmacy;

    Boolean isActive;
}
