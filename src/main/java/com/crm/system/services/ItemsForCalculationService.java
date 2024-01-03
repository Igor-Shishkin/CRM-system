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
        Optional<User> optionalUser = userService.getActiveUser();
        if (optionalUser.isEmpty()) {
            throw new UserPrincipalNotFoundException("I can't found active user");
        }
        Order order = orderService.getOrder(orderId);
        order.setCalculations(null);
        Set<ItemForCalcualtion> calcualtionSet = new HashSet<>();
        for (ItemForCalcualtion item : items) {
            if (!item.getThing().isEmpty() &&
                item.getQuantity()>=0 &&
                item.getUnitPrice()>=0) {
                item.setTotalPrice(item.getQuantity() * item.getUnitPrice() * 1.1);
                calcualtionSet.add(item);
            }
        }
        double sum = 0;
        for (ItemForCalcualtion item: calcualtionSet) {
            if (item.getTotalPrice()>0) {
                sum += item.getTotalPrice();
            } else {
                sum = 0;
                break;
            }
        }
        order.setCalculations(calcualtionSet);
        order.setResultPrice(sum);
    }
}
