package com.pharmacy.management.dto.response;

import java.time.LocalDate;
import java.util.List;

import com.pharmacy.management.model.Roles;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

@Data
@NoArgsConstructor
public class UserDataDTO {
  
  private String username;
  private String email;
  private String password;

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

  private String taxId;

  private String state;

  private String codeCity;

  private String codeLocation;

  List<Roles> appUserRoles;

}
