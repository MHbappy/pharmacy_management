package com.pharmacy.management.dto.response;
import java.util.List;

import com.pharmacy.management.model.Users;
import lombok.Data;

@Data
public class UserResponseDTO {
  private Integer id;
  private String username;
  private String email;
  List<Users> appUserRoles;
}
