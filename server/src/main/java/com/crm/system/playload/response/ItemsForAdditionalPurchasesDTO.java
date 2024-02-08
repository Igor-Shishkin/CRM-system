package com.crm.system.playload.response;

import com.crm.system.models.order.ItemForAdditionalPurchases;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode
public class ItemsForAdditionalPurchasesDTO {
    private Set<ItemForAdditionalPurchases> items;
    private double resultPrice;
}
