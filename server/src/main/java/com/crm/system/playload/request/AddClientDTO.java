package com.crm.system.playload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddClientDTO {
    @NotEmpty
    @Size(max = 50)
    private String fullName;
    @Email
    @NotBlank
    @Size(max = 100)
    private String email;
    @Size(max = 50)
    private String phoneNumber;
    @Size(max = 300)
    private String address;
}
