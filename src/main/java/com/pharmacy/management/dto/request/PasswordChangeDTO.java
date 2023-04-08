package com.pharmacy.management.dto.request;

import lombok.Data;
import javax.validation.constraints.Size;

@Data
public class PasswordChangeDTO {
    private Long userId;
    @Size(min = 6, message = "Minimum First name length: 6 characters")
    private String password;
}
