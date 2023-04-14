package com.pharmacy.management.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Table(name = "stripe_payment")
@Data
@NoArgsConstructor
public class StripePaymentHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;
    private String accountId;
    private String email;
    private String currency;
    private String source;
    private Boolean isPaid;
    private Double payAmount;
    private String status;
    private Long orderId;
    private LocalDateTime addedDateTime;
}
