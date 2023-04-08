package com.pharmacy.management.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pharmacy.management.model.enumeration.OrderStatus;
import lombok.Data;
import javax.persistence.*;

@Entity
@Table(name = "order_approve")
@Data
public class OrderApprove {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "delivery_status")
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Column(name = "comments")
    String comments;

    @ManyToOne
    private Users approvedBy;

    @JsonIgnore
    @ManyToOne
    private Orders orders;

    private Boolean isActive;
}
