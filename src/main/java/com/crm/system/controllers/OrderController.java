package com.crm.system.controllers;

import com.crm.system.exception.RequestOptionalIsEmpty;
import com.crm.system.exception.SubjectNotBelongToActiveUser;
import com.crm.system.models.Client;
import com.crm.system.models.order.Order;
import com.crm.system.playload.response.MessageResponse;
import com.crm.system.playload.response.OrderInfoResponse;
import com.crm.system.services.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.attribute.UserPrincipalNotFoundException;

@Slf4j
@CrossOrigin(origins = "http://localhost:8081", maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("api/user-board/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "Get order", tags = { "Order", "get"})
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_MODERATOR')")
    @GetMapping()
    public ResponseEntity<?> getOrder(@RequestParam long orderId) {
        try {
            OrderInfoResponse orderInfoResponse = orderService.getOrder(orderId);
            return ResponseEntity.ok(orderInfoResponse);
        } catch (RequestOptionalIsEmpty | SubjectNotBelongToActiveUser | UserPrincipalNotFoundException e) {
            log.error("Order controller: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Order controller: " + e.getMessage()));
        }
    }
}
