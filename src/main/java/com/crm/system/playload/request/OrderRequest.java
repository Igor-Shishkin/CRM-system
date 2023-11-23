package com.crm.system.playload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderRequest {
    @NotBlank
    @Size(max = 100)
    private String thing;
    private double price;
}
