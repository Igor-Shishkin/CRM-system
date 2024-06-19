package com.crm.system.playload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditClientDataDTO {
    @NotEmpty
    private Long clientId;
    @NotBlank
    private String fullName;
    @Email
    @NotBlank
    private String email;
    @Max(300)
    private String address;
    @Max(50)
    private String phoneNumber;
}
