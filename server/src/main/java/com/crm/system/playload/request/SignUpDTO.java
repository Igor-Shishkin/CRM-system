package com.crm.system.playload.request;

import java.util.Set;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpDTO {
    @NotEmpty(message = "Username is mandatory")
    @Size(min = 3, max = 30)
    private String username;

    @NotEmpty(message = "Email is mandatory")
    @Size(max = 100)
    @Email
    private String email;

    private Set<String> role;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 6, max = 40, message = "The password must contain from 6 to 40 characters")
    private String password;
}
