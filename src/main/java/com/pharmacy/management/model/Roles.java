package com.pharmacy.management.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "roles")
@Data
public class Roles {
    @NotNull
    @Size(max = 50)
    @Id
    @Column(length = 50)
    private String name;
}
