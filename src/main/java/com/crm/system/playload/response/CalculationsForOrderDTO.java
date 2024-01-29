package com.crm.system.playload.response;

import com.crm.system.models.order.ItemForCalculation;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode
public class CalculationsForOrderDTO {
    private Set<ItemForCalculation> items;
    private double resultPrice;
}
