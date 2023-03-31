package com.pharmacy.management.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pharmacy.management.model.enumeration.InOutStatus;
import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

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
    @NotNull
    private Integer quantity;

    @Column(name = "unit_price")
    @NotNull
    private Integer unitPrice;

    @Column(name = "total_price")
    @NotNull
    private Double totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "in_out_status")
    private InOutStatus inOutStatus;
    private LocalDateTime addedDateTime;
    @ManyToOne
    @JsonIgnoreProperties(value = { "category", "suppliers", "pharmacy", "stocks", "orders" }, allowSetters = true)
    private Product product;

    @JsonIgnore
    Boolean isActive;
}
