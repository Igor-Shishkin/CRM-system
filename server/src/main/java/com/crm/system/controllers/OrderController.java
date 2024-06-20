package com.crm.system.controllers;

import com.crm.system.playload.request.ChangeOrderDTO;
import com.crm.system.playload.request.CreateNewOrderDTO;
import com.crm.system.playload.response.MessageResponse;
import com.crm.system.playload.response.ItemsForAdditionalPurchasesDTO;
import com.crm.system.playload.response.OrderInfoDTO;
import com.crm.system.services.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.attribute.UserPrincipalNotFoundException;

@RestController
@RequestMapping("api/user-board/order")
@Slf4j
@Tag(name = "Order controller", description = "Order management APIs")
@CrossOrigin(origins = "http://localhost:8081", maxAge = 3600, allowCredentials = "true")
public class OrderController {


    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }




    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping()
    @Operation(    summary = "Get Order Information",
            description = """
                    Requires ROLE_USER authorization.
                    Retrieves detailed information for the specified order identified by orderId. \
                    The response includes comprehensive details about the order and the client associated with it.

                    Throws a RequestOptionalIsEmpty exception if:
                    - The user does not have an order with this ID.

                    Returns an OrderInfoDTO object containing Order information plus:
                    - `clientFullName` (String): The full name of the client.
                    - `clientPhoneNumber` (String): The phone number of the client.
                    - `clientId` (Long): The ID of the client.
                    - `clientEmail` (String): The email address of the client.
                    """)
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = OrderInfoDTO.class),
                            mediaType = "application/json")),
            @ApiResponse(
                    responseCode = "404", description = "User doesn't have a order with this ID",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class),
                            mediaType = "application/json")),
            @ApiResponse(
                    responseCode = "403", description = "ROLE_USER is required",
                    content = @Content(schema = @Schema())),
            @ApiResponse(
                    responseCode = "401", description = "Unauthorized -  you must log in",
                    content = @Content(schema = @Schema()))
    })
    public ResponseEntity<OrderInfoDTO> getOrder(@RequestParam long orderId) {
        OrderInfoDTO orderInfoDTO = orderService.getOrderInfoResponse(orderId);
        return ResponseEntity.ok(orderInfoDTO);
    }




    @GetMapping("/additional-purchases")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Get list of Additional Purchases for Order",
            description = """
                    Requires ROLE_USER authorization.
                    Retrieves additional purchases and the total result price for the specified order identified by orderId. 
                    The response includes the list of additional items and the resulting price.

                    Throws a RequestOptionalIsEmpty exception if:
                    - The user does not have an order with this ID.

                    Returns an ItemsForAdditionalPurchasesDTO object containing:
                    - `items` (List<PurchaseItem>): A list of additional purchase items associated with the order.
                    - `resultPrice` (BigDecimal): The total resulting price after recalculation).""")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = ItemsForAdditionalPurchasesDTO.class),
                            mediaType = "application/json")),
            @ApiResponse(
                    responseCode = "404", description = "User doesn't have a order with this ID",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class),
                            mediaType = "application/json")),
            @ApiResponse(
                    responseCode = "403", description = "ROLE_USER is required",
                    content = @Content(schema = @Schema())),
            @ApiResponse(
                    responseCode = "401", description = "Unauthorized -  you must log in",
                    content = @Content(schema = @Schema()))
    })
    public ResponseEntity<ItemsForAdditionalPurchasesDTO> getAdditionalPurchases(@Valid @RequestParam long orderId) {
        ItemsForAdditionalPurchasesDTO itemsForAdditionalPurchasesDTO = orderService.getAdditionalPurchases(orderId);
        return ResponseEntity.ok(itemsForAdditionalPurchasesDTO);
    }





    @PatchMapping("/sign-agreement")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Sign order agreement",
            description = """
                    Signs an agreement for the specified order identified by orderId.
                    Requires ROLE_USER authorization.
                    Ensures calculations are correct and shown to the client before signing.

                    Throws a MismanagementOfTheClientException if:
                    - The agreement for the order is already signed.
                    - The calculations are not correct or haven't been shown to the client.
                    
                    Throws a RequestOptionalIsEmpty exception if:
                    - The user does not have an order with this ID.

                    If successful:
                    - updates the agreement's status
                    - records the date of the last change, 
                    - creates a log entry for the client.
                    - returns: MessageResponse("Agreement's signed")
                    """)
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class),
                            mediaType = "application/json")),
            @ApiResponse(
                    responseCode = "409", description = "The order processing logic was broken",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class),
                            mediaType = "application/json")),
            @ApiResponse(
                    responseCode = "404", description = "User doesn't have a order with this ID",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class),
                            mediaType = "application/json")),
            @ApiResponse(
                    responseCode = "403", description = "ROLE_USER is required",
                    content = @Content(schema = @Schema())),
            @ApiResponse(
                    responseCode = "401", description = "Unauthorized -  you must log in",
                    content = @Content(schema = @Schema()))
    })
    public ResponseEntity<MessageResponse> signAgreement(@Valid @RequestParam long orderId)
            throws UserPrincipalNotFoundException {
        orderService.signAgreement(orderId);
        return ResponseEntity.ok(new MessageResponse("Agreement's signed"));
    }





    @PatchMapping("/cancel-agreement")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Cancel agreement",
            description = """
                    Cancels the agreement for the specified order identified by orderId.
                    Requires ROLE_USER authorization.

                    Throws a MismanagementOfTheClientException if:
                    - The agreement for the order isn't signed.
                    - The order has already been paid.
                    
                    Throws a RequestOptionalIsEmpty exception if:
                    - The user does not have an order with this ID.

                    If successful in canceling the agreement:
                    - Updates the agreement status and records the date of the last change.
                    - Creates a log entry indicating the cancellation of the agreement.
                    - Updates the date of last change for the client associated with the order.
                    - Returns: MessageResponse("Agreement's status is canceled")
                    """)
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class),
                            mediaType = "application/json")),
            @ApiResponse(
                    responseCode = "409", description = "The order processing logic was broken",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class),
                            mediaType = "application/json")),
            @ApiResponse(
                    responseCode = "404", description = "User doesn't have a order with this ID",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class),
                            mediaType = "application/json")),
            @ApiResponse(
                    responseCode = "403", description = "ROLE_USER is required",
                    content = @Content(schema = @Schema())),
            @ApiResponse(
                    responseCode = "401", description = "Unauthorized -  you must log in",
                    content = @Content(schema = @Schema()))
    })
    public ResponseEntity<MessageResponse> cancelAgreement(@Valid @RequestParam long orderId)
            throws UserPrincipalNotFoundException {
        orderService.cancelAgreement(orderId);
        return ResponseEntity.ok(new MessageResponse("Agreement's status is canceled"));
    }






    @PatchMapping("/cancel-payment")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Cancel payment",
            description = """
                    Cancels the payment for the specified order identified by orderId. \
                    Requires ROLE_USER authorization.

                    Throws a MismanagementOfTheClientException if:
                    - The payment for the order has not been made.
                    
                    Throws a RequestOptionalIsEmpty exception if:
                    - The user does not have an order with this ID.

                    If successful in canceling the payment:
                    - Updates the payment status and records the date of the last change for the order and the client.
                    - Adjusts the client status based on their payment history.
                    - Creates a log entry indicating the cancellation of the payment.
                    - Updates the date of last change for the client associated with the order.
                    - Returns: MessageResponse("Payment by client is canceled")
                    """)
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class),
                            mediaType = "application/json")),
            @ApiResponse(
                    responseCode = "409", description = "The order processing logic was broken",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class),
                            mediaType = "application/json")),
            @ApiResponse(
                    responseCode = "404", description = "User doesn't have a order with this ID",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class),
                            mediaType = "application/json")),
            @ApiResponse(
                    responseCode = "403", description = "ROLE_USER is required",
                    content = @Content(schema = @Schema())),
            @ApiResponse(
                    responseCode = "401", description = "Unauthorized -  you must log in",
                    content = @Content(schema = @Schema()))
    })
    public ResponseEntity<MessageResponse> cancelPayment(@Valid @RequestParam long orderId)
            throws UserPrincipalNotFoundException {
        orderService.cancelPayment(orderId);
        return ResponseEntity.ok(new MessageResponse("Payment by client is canceled"));
    }





    @PatchMapping("/confirm-payment")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Confirm payment",
            description = """
                    Confirms the payment for the specified order identified by orderId. \
                    Requires ROLE_USER authorization.

                    Throws a MismanagementOfTheClientException if:
                    - The payment for the order was already made.
                    - The agreement for the order is not signed.
                    
                    Throws a RequestOptionalIsEmpty exception if:
                    - The user does not have an order with this ID.

                    If successful in confirming the payment:
                    - Updates the payment status and records the date of the last change for the order and the client.
                    - Sets the client's status to CLIENT.
                    - Creates a log entry indicating the confirmation of the payment.
                    - Updates the date of last change for the client associated with the order.
                    - Returns: MessageResponse("Payment is confirm")
                    """)
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class),
                            mediaType = "application/json")),
            @ApiResponse(
                    responseCode = "409", description = "The order processing logic was broken",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class),
                            mediaType = "application/json")),
            @ApiResponse(
                    responseCode = "404", description = "User doesn't have a order with this ID",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class),
                            mediaType = "application/json")),
            @ApiResponse(
                    responseCode = "403", description = "ROLE_USER is required",
                    content = @Content(schema = @Schema())),
            @ApiResponse(
                    responseCode = "401", description = "Unauthorized -  you must log in",
                    content = @Content(schema = @Schema()))
    })
    public ResponseEntity<MessageResponse> confirmPayment(@Valid @RequestParam long orderId)
            throws UserPrincipalNotFoundException {
        orderService.confirmPayment(orderId);
        return ResponseEntity.ok(new MessageResponse("Payment is confirm"));
    }




    @PatchMapping("/save-order-changes")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(    summary = "Save order's changes",
            description = """
                    Saves changes to an existing order identified by orderId. \
                    Requires ROLE_USER authorization.

                    Expects a ChangeOrderDTO object containing the updated order details:

                    Throws a RequestOptionalIsEmpty exception if:
                    - The user does not have an order with this ID.

                    If successful in saving the changes:
                    - Updates the order details with the new values.
                    - Sets the date of the last change for the order.
                    - Persists the updated order details in the database.
                    - Updates the date of last change for the client associated with the order.
                    - Returns: MessageResponse("Changes are saved")
                    """)
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class),
                            mediaType = "application/json")),
            @ApiResponse(
                    responseCode = "409", description = "The order processing logic was broken",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class),
                            mediaType = "application/json")),
            @ApiResponse(
                    responseCode = "404", description = "User doesn't have a order with this ID",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class),
                            mediaType = "application/json")),
            @ApiResponse(
                    responseCode = "403", description = "ROLE_USER is required",
                    content = @Content(schema = @Schema())),
            @ApiResponse(
                    responseCode = "401", description = "Unauthorized -  you must log in",
                    content = @Content(schema = @Schema()))
    })
    public ResponseEntity<MessageResponse> saveOrderChanges(@Valid @RequestBody ChangeOrderDTO changeOrderDTO) {
        orderService.saveOrderChanges(changeOrderDTO);
        return ResponseEntity.ok(new MessageResponse("Changes are saved"));
    }




    @PostMapping("/create-new-order")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Create new order",
            description = """
                    Creates a new order for the client identified by clientId. \
                    Requires ROLE_USER authorization.

                    Expects a CreateNewOrderDTO object containing the details for the new order.

                    Throws a RequestOptionalIsEmpty exception if:
                    - The user does not have a Client with this ID.

                    If successful in creating the new order:
                    - Saves the new order to the database.
                    - Creates a log entry for the client indicating the new order.
                    - Updates the date of last change for the client.
                    - Returns the ID of the newly created order.
                    """)
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class),
                            mediaType = "application/json")),
            @ApiResponse(
                    responseCode = "409", description = "The order processing logic was broken",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class),
                            mediaType = "application/json")),
            @ApiResponse(
                    responseCode = "404", description = "User doesn't have a client with this ID",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class),
                            mediaType = "application/json")),
            @ApiResponse(
                    responseCode = "403", description = "ROLE_USER is required",
                    content = @Content(schema = @Schema())),
            @ApiResponse(
                    responseCode = "401", description = "Unauthorized -  you must log in",
                    content = @Content(schema = @Schema()))
    })
    public ResponseEntity<Long> createNewOrder(@Valid @RequestBody CreateNewOrderDTO createNewOrderDTO) throws UserPrincipalNotFoundException {
        long newOrderID = orderService.createNewOrder(createNewOrderDTO);
        return ResponseEntity.ok(newOrderID);
    }
}
