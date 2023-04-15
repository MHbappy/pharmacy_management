package com.pharmacy.management.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.Size;

@Table(name = "users")
@Entity
@Data
@NoArgsConstructor
public class Users {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private String email;

  @Size(min = 6, message = "Minimum password length: 6 characters")
  @JsonIgnore
  private String password;

  @Column(name = "first_name")
  private String firstName;

  @Column(name = "last_name")
  private String lastName;

  @Column(name = "numId")
  private String numId;

  @Column(name = "title")
  private String title;

  @Column(name = "state")
  private String state;

  @Column(name = "code_city")
  private String codeCity;

  @Column(name = "code_location")
  private String codeLocation;

  @Column(name = "title_of_courtesy")
  private String titleOfCourtesy;

  @Column(name = "birth_date")
  private LocalDate birthDate;

  @Column(name = "hire_date")
  private LocalDate hireDate;

  @Column(name = "address")
  private String address;

  @Column(name = "zip_code")
  private String zip_code;

  @Column(name = "home_phone")
  private String homePhone;

  @Column(name = "taxNum")
  private String taxNum;

  @Column(name = "extention")
  private String extention;

  @Column(name = "photo_url")
  private String photoUrl;

  @Column(name = "notes")
  private String notes;

  @Column(name = "is_self_payment")
  private Boolean isSelfPayment = false;

  @Column(name = "is_active")
  private Boolean isActive;

  @ManyToOne
  @JsonIgnoreProperties(value = { "country", "city", "region"}, allowSetters = true)
  private Company company;

  @ManyToOne
  private CompanyPolicy companyPolicy;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
          name = "user_role",
          joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
          inverseJoinColumns = {@JoinColumn(name = "role_name", referencedColumnName = "name")})
  private Set<Roles> roles = new HashSet<>();

}
