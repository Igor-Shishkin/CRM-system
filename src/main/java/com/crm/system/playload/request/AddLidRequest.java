package com.crm.system.playload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddLidRequest {
    @NotBlank
    @Size(max = 50)
    private String fullName;
    @Email
    @NotBlank
    @Size(max = 80)
    private String email;
    @Size(max = 50)
    private String phoneNumber;
    private String addres;
}
