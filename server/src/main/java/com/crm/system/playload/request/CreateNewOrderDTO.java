package com.crm.system.playload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateNewOrderDTO {
    private long clientId;
    private String realNeed;
    private double estimateBudget;
}
