package com.pharmacy.management.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * A Orders.
 */
@Entity
@Table(name = "orders")
@Data
public class Orders implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "order_date")
    private LocalDate orderDate;

    @Column(name = "shipped_date")
    private LocalDate shippedDate;

    @Column(name = "required_date")
    private LocalDate requiredDate;

    @Column(name = "total_price")
    private Double totalPrice;

    @ManyToOne
    @JsonIgnoreProperties(value = { "category", "suppliers", "pharmacy", "stocks", "orders" }, allowSetters = true)
    private Product product;

    @ManyToOne
    private Users users;

    Boolean isActive;
}
