package com.pharmacy.management.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;

/**
 * A Category.
 */
@Entity
@Table(name = "category")
@Data
public class Category implements Serializable {
    //medical labs

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "photo")
    private String photo;

    @Column(name = "is_active")
    @JsonIgnore
    private Boolean isActive;
}


//3 medicine
//Fill up the form---
//Category telemedicine/paharmacy/ambulance



//ambulance
//order form

//Paracetalmol-----medical diagnostic
//Rabiprazole------Gastrology

//select 3 medicine with search (Paracele, etc), medical diagnostic
//he have to write some info give some information for getting medicine, why he need the medicine(Field)
//Place the order

//Suppose 40 employee
//pharmacy category -> medicine inventory -> select medicine -> generate
// telemedicine ->
//*** one category one order ---order number like pha1234 or amb3456
//*** create a rules for every employee like one month he can get only 4/5 service
//***
//A employee log in to system, he chose a category medicine or ambule...
// then he have chosse his all medicne, he can order it and select his address which is home,
//

//Medicine, Ambulance, ETC