package com.pharmacy.management.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.Size;
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
    @Size(min = 2, message = "Minimum name length: 2 characters")
    private String name;

    @Column(name = "is_active")
    @JsonIgnore
    private Boolean isActive;

    @ManyToOne
    private City city;
}
