package com.pharmacy.management.dto.response;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pharmacy.management.model.Company;
import com.pharmacy.management.model.Roles;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.ManyToOne;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class UserDataDTO {

  @Size(min = 4, message = "Minimum User name length: 8 characters")
  private String username;

  @Size(min = 8, message = "Minimum email length: 5 characters")
  @Email
  private String email;

  @Size(min = 4, message = "Minimum Password length: 6 characters")
  private String password;

  @Size(min = 4, message = "Minimum First name length: 2 characters")
  private String firstName;

  private String lastName;

  private String title;

  private String titleOfCourtesy;

  private LocalDate birthDate;

  private LocalDate hireDate;

  private String address;

  private String homePhone;

  private String extention;

  private String photoUrl;

  private String notes;

  private String taxNum;

  private String state;

  private String codeCity;

  private String codeLocation;

  private Company company;

  List<Roles> appUserRoles;

}
