package com.crm.system.services;

import com.crm.system.exception.RequestOptionalIsEmpty;
import com.crm.system.exception.SubjectNotBelongToActiveUser;
import com.crm.system.models.User;
import com.crm.system.models.order.Order;
import com.crm.system.playload.response.NewCalculationsForOrderResponse;
import com.crm.system.playload.response.OrderInfoResponse;
import com.crm.system.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.Optional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserService userService;

    public OrderService(OrderRepository orderRepository, UserService userService) {
        this.orderRepository = orderRepository;
        this.userService = userService;
    }


    public OrderInfoResponse getOrderInfoResponce(long orderId) throws UserPrincipalNotFoundException {
        Order order = getOrderById(orderId);
        OrderInfoResponse orderInfoResponse = new OrderInfoResponse(order);
        return orderInfoResponse;
    }



    public Order getOrder(long orderId) throws UserPrincipalNotFoundException {
        Order order = getOrderById(orderId);
        return order;
    }

    public NewCalculationsForOrderResponse getNewCalculations(long orderId) throws UserPrincipalNotFoundException {
        Order order = getOrderById(orderId);
        NewCalculationsForOrderResponse newCalculations = new NewCalculationsForOrderResponse();
        newCalculations.setItems(order.getCalculations());
        newCalculations.setResultPrice(order.getResultPrice());
        return newCalculations;
    }

    public void changeOrder(Order order) {
        orderRepository.save(order);
    }
    private Order getOrderById(long orderId) throws UserPrincipalNotFoundException {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isEmpty()){
            throw new RequestOptionalIsEmpty("There isn't order with this ID");
        }
        Order order = optionalOrder.get();
        if (isOrderBelongsActiveUser(order) ) {
            return order;
        } else {
            throw new SubjectNotBelongToActiveUser("It's not your order");
        }
    }

    private boolean isOrderBelongsActiveUser(Order order) throws UserPrincipalNotFoundException {
        Optional<User> optionalUser = userService.getActiveUser();
        if (optionalUser.isEmpty()) {
            throw new UserPrincipalNotFoundException("I can't found active user");
        }
        User activeUser = optionalUser.get();
        return activeUser.getClients().stream()
                .anyMatch(client -> client.getId().equals(order.getClient().getId()));
    }
}
