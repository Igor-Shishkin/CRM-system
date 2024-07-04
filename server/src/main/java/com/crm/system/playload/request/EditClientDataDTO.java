package com.crm.system.playload.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditClientDataDTO {

    @Positive(message = "Client ID must be positive")
    private Long clientId;

    @NotBlank
    private String fullName;

    @Email
    @NotBlank
    private String email;

    @Size(max = 300)
    private String address;

    @Size(max = 50)
    private String phoneNumber;
}
