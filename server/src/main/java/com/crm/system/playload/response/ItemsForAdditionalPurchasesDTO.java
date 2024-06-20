package com.crm.system.playload.response;

import com.crm.system.models.order.ItemForAdditionalPurchases;
import com.crm.system.models.order.Order;
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

    public ItemsForAdditionalPurchasesDTO(Order order) {
        this.items = order.getAdditionalPurchases();
        this.resultPrice = order.getResultPrice();
    }
}
