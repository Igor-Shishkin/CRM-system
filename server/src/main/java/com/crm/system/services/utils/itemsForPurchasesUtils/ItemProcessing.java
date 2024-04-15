package com.crm.system.services.utils.itemsForPurchasesUtils;

import com.crm.system.models.order.ItemForAdditionalPurchases;
import com.crm.system.models.order.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ItemProcessing {

    @Value("${app.crm.price.coefficient}")
    private double PRICE_COEFFICIENT;

    public Set<ItemForAdditionalPurchases> processReceivedItemsForSaving(Set<ItemForAdditionalPurchases> items,
                                                                         Order order) {
        return items.stream()
                .filter(item -> !item.getItemName().isEmpty())
                .filter(item -> item.getQuantity() >= 0 && item.getUnitPrice() >= 0)
                .peek(item -> item.setOrder(order))
                .peek(item -> item.setTotalPrice(item.getQuantity() * item.getUnitPrice() * PRICE_COEFFICIENT))
                .collect(Collectors.toSet());
    }

    public void changeSetOfItemsInOrder(Order order, Set<ItemForAdditionalPurchases> items) {
        order.getAdditionalPurchases().clear();

        order.getAdditionalPurchases().addAll(items);
        order.setResultPrice(calculateResultSum(items));
        order.setDateOfLastChange(LocalDateTime.now());

        order.getClient().setDateOfLastChange(LocalDateTime.now());
    }
    private double calculateResultSum(Set<ItemForAdditionalPurchases> items) {
        return items.stream()
                .filter(item -> item.getTotalPrice() > 0)
                .mapToDouble(ItemForAdditionalPurchases::getTotalPrice)
                .sum();
    }
}
