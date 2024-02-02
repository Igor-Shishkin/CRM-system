package com.crm.system.controllers;

import com.crm.system.exception.MismanagementOfTheClientException;
import com.crm.system.exception.RequestOptionalIsEmpty;
import com.crm.system.exception.SubjectNotBelongToActiveUser;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    @Operation(summary = "Get order", tags = { "Order", "get"})
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping()
    public ResponseEntity<?> getOrder(@RequestParam long orderId) {
        try {
            OrderInfoDTO orderInfoDTO = orderService.getOrderInfoResponce(orderId);
            return ResponseEntity.ok(orderInfoDTO);
        } catch (RequestOptionalIsEmpty e) {
            log.error("Order controller: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Order controller: " + e.getMessage()));
        }
    }
    @Operation(summary = "Get calculations for order", tags = { "Order", "get"})
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/calculations")
    public ResponseEntity<?> getCalculations(@Valid @RequestParam long orderId) {
        try {
            ItemsForAdditionalPurchasesDTO calculations = orderService.getCalculations(orderId);
            return ResponseEntity.ok(calculations);
        } catch (RequestOptionalIsEmpty e) {
            log.error("Order controller: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Order controller: " + e.getMessage()));
        }
    }
    @Operation(summary = "Sign agreement", tags = { "Order", "agreement"})
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/sign-agreement")
    public ResponseEntity<?> signAgreement(@Valid  @RequestParam long orderId) {
        try {
            orderService.signAgreement(orderId);
            return ResponseEntity.ok(new MessageResponse("Agreement's signed"));
        } catch (RequestOptionalIsEmpty | MismanagementOfTheClientException e) {
            log.error("Order controller: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Order controller: " + e.getMessage()));
        }
    }
    @Operation(summary = "Cancel agreement", tags = { "Order", "agreement"})
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/cancel-agreement")
    public ResponseEntity<?> cancelAgreement(@Valid  @RequestParam long orderId) {
        try {
            orderService.cancelAgreement(orderId);
            return ResponseEntity.ok(new MessageResponse("Agreement's status is canceled"));
        } catch (RequestOptionalIsEmpty | MismanagementOfTheClientException e) {
            log.error("Order controller: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Order controller: " + e.getMessage()));
        }
    }
    @Operation(summary = "Cancel payment", tags = { "Order", "payment"})
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/cancel-payment")
    public ResponseEntity<?> cancelPayment(@Valid  @RequestParam long orderId) {
        try {
            orderService.cancelPayment(orderId);
            return ResponseEntity.ok(new MessageResponse("Payment by client is canceled"));
        } catch (RequestOptionalIsEmpty | MismanagementOfTheClientException e) {
            log.error("Order controller: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Order controller: " + e.getMessage()));
        }
    }
    @Operation(summary = "Confirm payment", tags = { "Order", "payment"})
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/confirm-payment")
    public ResponseEntity<?> confirmPayment(@Valid  @RequestParam long orderId) {
        try {
            orderService.confirmPayment(orderId);
            return ResponseEntity.ok(new MessageResponse("Payment is confirm"));
        } catch (RequestOptionalIsEmpty | MismanagementOfTheClientException e) {
            log.error("Order controller: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Order controller: " + e.getMessage()));
        }
    }
    @Operation(summary = "Save changes", tags = { "Order", "change"})
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/save-order-changes")
    public ResponseEntity<?> saveOrderChanges(@Valid  @RequestBody ChangeOrderDTO changeOrderDTO) {
        try {
            orderService.saveOrderChanges(changeOrderDTO);
            return ResponseEntity.ok(new MessageResponse("Changes are saved"));
        } catch (RequestOptionalIsEmpty | SubjectNotBelongToActiveUser e) {
            log.error("Order controller: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Order controller: " + e.getMessage()));
        }
    }
    @Operation(summary = "Create new order", tags = { "Order", "new"})
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/create-new-order")
    public ResponseEntity<?> createNewOrder(@Valid  @RequestBody CreateNewOrderDTO createNewOrderDTO) {
        try {
            long newOrderID = orderService.createNewOrder(createNewOrderDTO);
            return ResponseEntity.ok(newOrderID);
        } catch (RequestOptionalIsEmpty | SubjectNotBelongToActiveUser e) {
            log.error("Order controller: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Order controller: " + e.getMessage()));
        }
    }
}
