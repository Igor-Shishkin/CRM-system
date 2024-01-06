package com.crm.system.services;

import com.crm.system.exception.MismanagementOfTheClientException;
import com.crm.system.models.User;
import com.crm.system.models.order.ItemForCalcualtion;
import com.crm.system.models.order.Order;
import org.springframework.stereotype.Service;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ItemsForCalculationService {
    private final UserService userService;
    private final OrderService orderService;

    public ItemsForCalculationService(UserService userService, OrderService orderService) {
        this.userService = userService;
        this.orderService = orderService;
    }

    public void saveItems(Set<ItemForCalcualtion> items, long orderId) throws UserPrincipalNotFoundException {
        Order order = orderService.getOrder(orderId);
        if (!order.isAgreementSigned()) {
            order.getCalculations().clear();

            items = items.stream()
                    .filter(item -> !item.getThing().isEmpty())
                    .peek(item -> {
                        item.setItemId(null);
                        item.setOrder(order);
                    })
                    .collect(Collectors.toSet());
            calculateTotalPrice(items);

            double sum = calcualteResultSum(items);

            order.getCalculations().addAll(items);
            order.setResultPrice(sum);
            order.setDateOfLastChange(LocalDateTime.now());
            orderService.changeOrder(order);
        } else {
            throw new MismanagementOfTheClientException("You can't changes item's calculation if agreement is signed");
        }
    }

    private double calcualteResultSum(Set<ItemForCalcualtion> items) {
        double sum = 0;
        for (ItemForCalcualtion item : items) {
            if (item.getTotalPrice() > 0) {
                sum += item.getTotalPrice();
            } else {
                return 0;
            }
        }
        return sum;
    }

    private void calculateTotalPrice(Set<ItemForCalcualtion> items) {
        for (ItemForCalcualtion item : items) {
            if (item.getQuantity() >= 0 && item.getUnitPrice() >= 0) {
                item.setTotalPrice(item.getQuantity() * item.getUnitPrice() * 1.1);
            }
        }
    }
}
