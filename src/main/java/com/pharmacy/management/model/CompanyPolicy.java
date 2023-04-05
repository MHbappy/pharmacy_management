package com.pharmacy.management.model;

import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * A CompanyPolicy.
 */
@Entity
@Table(name = "company_policy")
@Data
public class CompanyPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "designation")
    @NotNull
    private String designation;

    @Column(name = "limit_cost")
    @NotNull
    private Double limitCost;

    @Column(name = "policy_start_from")
    @NotNull
    private LocalDate policyStartFrom;

    @ManyToOne
    @NotNull
    Company company;

    @Column(name = "is_active")
    private Boolean isActive;
}
