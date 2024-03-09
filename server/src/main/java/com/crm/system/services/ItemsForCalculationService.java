package com.crm.system.services;

import com.crm.system.exception.MismanagementOfTheClientException;
import com.crm.system.models.order.ItemForAdditionalPurchases;
import com.crm.system.models.order.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ItemsForCalculationService {
    private final OrderService orderService;
    @Value("${app.crm.price.coefficient}")
    private double PRICE_COEFFICIENT;

    public ItemsForCalculationService(OrderService orderService) {
        this.orderService = orderService;
    }

    public void saveItems(Set<ItemForAdditionalPurchases> items, long orderId)  {
        Order order = orderService.getOrder(orderId);
        if (!order.isAgreementSigned()) {
            order.getAdditionalPurchases().clear();

            items = items.stream()
                    .filter(item -> !item.getItemName().isEmpty())
                    .peek(item -> item.setOrder(order))
                    .collect(Collectors.toSet());
            calculateTotalPrice(items);

            double sum = calculateResultSum(items);

            order.getAdditionalPurchases().addAll(items);
            order.setResultPrice(sum);
            order.setDateOfLastChange(LocalDateTime.now());

            order.getClient().setDateOfLastChange(LocalDateTime.now());

            orderService.changeOrder(order);
        } else {
            throw new MismanagementOfTheClientException("You can't changes item's calculation if agreement is signed");
        }
    }

    private double calculateResultSum(Set<ItemForAdditionalPurchases> items) {

        return items.stream()
                .filter(item -> item.getTotalPrice()>0)
                .mapToDouble(ItemForAdditionalPurchases::getTotalPrice)
                .sum();
    }

    private void calculateTotalPrice(Set<ItemForAdditionalPurchases> items) {
        items.stream()
                .filter(item -> item.getQuantity() >= 0 && item.getUnitPrice() >= 0)
                .forEach(item -> item.setTotalPrice(item.getQuantity() * item.getUnitPrice() * PRICE_COEFFICIENT));
    }
}
