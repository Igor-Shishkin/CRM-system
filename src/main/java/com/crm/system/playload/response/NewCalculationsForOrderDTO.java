package com.crm.system.playload.response;

import com.crm.system.models.order.ItemForCalcualtion;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class NewCalculationsForOrderDTO {
    private Set<ItemForCalcualtion> items;
    private double resultPrice;
}
