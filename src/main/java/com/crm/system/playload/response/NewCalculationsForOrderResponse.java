package com.crm.system.playload.response;

import com.crm.system.models.order.ItemForCalcualtion;
import com.crm.system.services.ItemsForCalculationService;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class NewCalculationsForOrderResponse {
    private Set<ItemForCalcualtion> items;
    private double resultPrice;
}
