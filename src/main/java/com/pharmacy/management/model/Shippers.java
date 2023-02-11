package com.pharmacy.management.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;

/**
 * A Shippers.
 */
@Entity
@Table(name = "shippers")
@Data
public class Shippers implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "phone")
    private String phone;

    @ManyToOne
    @JsonIgnoreProperties(value = { "product", "systemUsers", "ordersItems", "shippers" }, allowSetters = true)
    private Orders orders;
}
