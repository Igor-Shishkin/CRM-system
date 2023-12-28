package com.crm.system.services;

import com.crm.system.exception.RequestOptionalIsEmpty;
import com.crm.system.exception.SubjectNotBelongToActiveUser;
import com.crm.system.models.User;
import com.crm.system.models.order.Order;
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


    public OrderInfoResponse getOrder(long orderId) throws UserPrincipalNotFoundException {
        Optional<User> optionalUser = userService.getActiveUser();
        if (optionalUser.isEmpty()) {
            throw new UserPrincipalNotFoundException("I can't found active user");
        }
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isEmpty()){
            throw new RequestOptionalIsEmpty("There isn't order with this ID");
        }
        Order order = optionalOrder.get();
        User activeUser = optionalUser.get();

        if (activeUser.getClients().stream()
                        .anyMatch(client -> client.getId().equals(order.getClient().getId()))
        ) {
            OrderInfoResponse orderInfoResponse = new OrderInfoResponse(order);
            return orderInfoResponse;
        } else {
            throw new SubjectNotBelongToActiveUser("It's not your order");
        }
    }
}
