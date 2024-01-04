package com.crm.system.services;

import com.crm.system.models.User;
import com.crm.system.models.order.ItemForCalcualtion;
import com.crm.system.models.order.Order;
import org.springframework.stereotype.Service;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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
        order.setCalculations(null);

        Set<ItemForCalcualtion> calcualtionSet = new HashSet<>();
        calculateTotalPrice(items, calcualtionSet);

        double sum = calcualteResultSum(calcualtionSet);

        order.setCalculations(calcualtionSet);
        order.setResultPrice(sum);
        orderService.changeOrder(order);
    }
    private double calcualteResultSum(Set<ItemForCalcualtion> calcualtionSet) {
        double sum = 0;
        for (ItemForCalcualtion item: calcualtionSet) {
            if (item.getTotalPrice()>0) {
                sum += item.getTotalPrice();
            } else {
                return  0;
            }
        }
        return sum;
    }
    private void calculateTotalPrice(Set<ItemForCalcualtion> items, Set<ItemForCalcualtion> calcualtionSet) {
        for (ItemForCalcualtion item : items) {
            if (!item.getThing().isEmpty() &&
                    item.getQuantity()>=0 &&
                    item.getUnitPrice()>=0) {
                item.setTotalPrice(item.getQuantity() * item.getUnitPrice() * 1.1);
                calcualtionSet.add(item);
            }
        }

    }
}
