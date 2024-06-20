package com.crm.system.playload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateNewOrderDTO {
    @NotEmpty
    @NotNull
    private long clientId;
    @NotBlank
    private String realNeed;
    private double estimateBudget;
}
