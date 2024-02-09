package com.crm.system.playload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditClientDataDTO {
    @NotBlank
    private Long clientId;
    @NotBlank
    private String fullName;
    @Email
    private String email;
    @NotBlank
    private String address;
    @NotBlank
    private String phoneNumber;
}
