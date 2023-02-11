package com.pharmacy.management.model;

import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;

/**
 * A Region.
 */
@Entity
@Table(name = "region")
@Data
public class Region implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "is_active")
    private Boolean isActive;
}
