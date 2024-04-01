package com.crm.system.controllers;

import com.crm.system.playload.request.ChangeOrderDTO;
import com.crm.system.playload.request.CreateNewOrderDTO;
import com.crm.system.playload.response.MessageResponse;
import com.crm.system.playload.response.ItemsForAdditionalPurchasesDTO;
import com.crm.system.playload.response.OrderInfoDTO;
import com.crm.system.services.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.attribute.UserPrincipalNotFoundException;

@Slf4j
@Tag(name = "Order controller", description = "Order management APIs")
@CrossOrigin(origins = "http://localhost:8081", maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("api/user-board/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "Get order", tags = {"Order", "get"})
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping()
    public ResponseEntity<OrderInfoDTO> getOrder(@RequestParam long orderId) {
        OrderInfoDTO orderInfoDTO = orderService.getOrderInfoResponce(orderId);
        return ResponseEntity.ok(orderInfoDTO);
    }

    @Operation(summary = "Get additional purchases for order", tags = {"Order", "get", "purchases"})
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/additional-purchases")
    public ResponseEntity<ItemsForAdditionalPurchasesDTO> getAdditionalPurchases(@Valid @RequestParam long orderId) {
        ItemsForAdditionalPurchasesDTO calculations = orderService.getAdditionalPurchases(orderId);
        return ResponseEntity.ok(calculations);
    }

    @Operation(summary = "Sign agreement", tags = {"Order", "agreement"})
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/sign-agreement")
    public ResponseEntity<MessageResponse> signAgreement(@Valid @RequestParam long orderId) {
        orderService.signAgreement(orderId);
        return ResponseEntity.ok(new MessageResponse("Agreement's signed"));
    }

    @Operation(summary = "Cancel agreement", tags = {"Order", "agreement"})
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/cancel-agreement")
    public ResponseEntity<MessageResponse> cancelAgreement(@Valid @RequestParam long orderId) {
        orderService.cancelAgreement(orderId);
        return ResponseEntity.ok(new MessageResponse("Agreement's status is canceled"));
    }

    @Operation(summary = "Cancel payment", tags = {"Order", "payment"})
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/cancel-payment")
    public ResponseEntity<MessageResponse> cancelPayment(@Valid @RequestParam long orderId){
        orderService.cancelPayment(orderId);
        return ResponseEntity.ok(new MessageResponse("Payment by client is canceled"));
    }

    @Operation(summary = "Confirm payment", tags = {"Order", "payment"})
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/confirm-payment")
    public ResponseEntity<MessageResponse> confirmPayment(@Valid @RequestParam long orderId) {
        orderService.confirmPayment(orderId);
        return ResponseEntity.ok(new MessageResponse("Payment is confirm"));
    }

    @Operation(summary = "Save changes", tags = {"Order", "change"})
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/save-order-changes")
    public ResponseEntity<MessageResponse> saveOrderChanges(@Valid @RequestBody ChangeOrderDTO changeOrderDTO) {
        orderService.saveOrderChanges(changeOrderDTO);
        return ResponseEntity.ok(new MessageResponse("Changes are saved"));
    }

    @Operation(summary = "Create new order", tags = {"Order", "new"})
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/create-new-order")
    public ResponseEntity<Long> createNewOrder(@Valid @RequestBody CreateNewOrderDTO createNewOrderDTO) {
        long newOrderID = orderService.createNewOrder(createNewOrderDTO);
        return ResponseEntity.ok(newOrderID);
    }
}
