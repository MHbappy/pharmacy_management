package com.pharmacy.management.model;

import com.pharmacy.management.model.enumeration.OrderStatus;
import com.pharmacy.management.model.enumeration.PaymentStatus;
import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;


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
    private LocalDateTime orderDate;

    @Column(name = "shipped_date")
    private LocalDate shippedDate;

    @Column(name = "order_no")
    private String orderNo;

    @Column(name = "required_date")
    private LocalDate requiredDate;

    @Column(name = "total_price")
    private Double totalPrice;

    @Column(name = "order_status")
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Column(name = "payment_status")
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @ManyToOne
    private DeliveryAddress deliveryAddress;

    @ManyToOne
    private Provider provider;

    private Boolean isSelfPayment;

    @ManyToOne
//    @JsonIgnore
    private Users users;

    Boolean isActive;
}
