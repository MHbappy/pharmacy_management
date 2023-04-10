package com.pharmacy.management.dto.request;

import com.pharmacy.management.model.Company;
import com.pharmacy.management.model.CompanyPolicy;
import com.pharmacy.management.model.Roles;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;


@Data
@NoArgsConstructor
public class UserDataExcelDTO {
    private String email;
    private String password;
    private String firstName;
    private String lastName;

    private String numId;

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

    private String company;

    private String companyPolicy;
}
