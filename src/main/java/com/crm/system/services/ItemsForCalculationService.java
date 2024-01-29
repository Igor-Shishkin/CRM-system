package com.crm.system.services;

import com.crm.system.exception.MismanagementOfTheClientException;
import com.crm.system.models.order.ItemForCalculation;
import com.crm.system.models.order.Order;
import org.springframework.stereotype.Service;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ItemsForCalculationService {
    private final OrderService orderService;

    public ItemsForCalculationService(UserService userService, OrderService orderService) {
        this.orderService = orderService;
    }

    public void saveItems(Set<ItemForCalculation> items, long orderId) throws UserPrincipalNotFoundException {
        Order order = orderService.getOrder(orderId);
        if (!order.isAgreementSigned()) {
            order.getCalculations().clear();

            items = items.stream()
                    .filter(item -> !item.getThing().isEmpty())
                    .peek(item -> {
                        item.setOrder(order);
                    })
                    .collect(Collectors.toSet());
            calculateTotalPrice(items);

            double sum = calculateResultSum(items);

            order.getCalculations().addAll(items);
            order.setResultPrice(sum);
            order.setDateOfLastChange(LocalDateTime.now());

            order.getClient().setDateOfLastChange(LocalDateTime.now());

            orderService.changeOrder(order);
        } else {
            throw new MismanagementOfTheClientException("You can't changes item's calculation if agreement is signed");
        }
    }

    private double calculateResultSum(Set<ItemForCalculation> items) {

        return items.stream()
                .filter(item -> item.getTotalPrice()>0)
                .mapToDouble(ItemForCalculation::getTotalPrice)
                .sum();
    }

    private void calculateTotalPrice(Set<ItemForCalculation> items) {
        items.stream()
                .filter(item -> item.getQuantity() >= 0 && item.getUnitPrice() >= 0)
                .forEach(item -> item.setTotalPrice(item.getQuantity() * item.getUnitPrice() * 1.1));
    }
}
