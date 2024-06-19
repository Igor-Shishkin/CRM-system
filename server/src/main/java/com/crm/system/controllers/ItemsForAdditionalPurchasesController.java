package com.crm.system.controllers;

import com.crm.system.models.order.ItemForAdditionalPurchases;
import com.crm.system.playload.response.MessageResponse;
import com.crm.system.services.ItemsForAdditionalPurchasesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;


@RestController
@RequestMapping("api/user-board/items-for-addition-purchases")
@Slf4j
@Tag(name = "Items for additional purchases controller", description = "Items for additional purchases management APIs")
@CrossOrigin(origins = "http://localhost:8081", maxAge = 3600, allowCredentials = "true")

public class ItemsForAdditionalPurchasesController {
    private final ItemsForAdditionalPurchasesService itemsForAdditionalPurchasesService;

    public ItemsForAdditionalPurchasesController(ItemsForAdditionalPurchasesService itemsForAdditionalPurchasesService) {
        this.itemsForAdditionalPurchasesService = itemsForAdditionalPurchasesService;
    }



    @PostMapping("/save-items")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(
            summary = "Get order's items for addition purchases",
            description = "Adds a new items for purchases to the order. " +
                    "<br/><br/>" +
                    "**Request Body:**<br/> " +
                    "__Array__ [ <br/>" +
                    "- `itemId` (String): The name of the item. Can be null (For already existing items).<br/>" +
                    "- `itemName` (String): The name of the item. Must not be null or empty.<br/>" +
                    "- `quantity` (int): The quantity of the item. Must be a positive integer.<br/>" +
                    "- `unitPrice` (double): The unit price of the item. Must be a positive number.<br/>" +
                    "- `totalPrice` (double): The total price of the item. ]<br/> <br/>" +
                    "- `orderId` (long): order ID.<br/><br/>")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201", description = "Item successfully added",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class), mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "400", description = "Invalid request content.",
                    content = @Content(schema = @Schema())),
            @ApiResponse(
                    responseCode = "403", description = "ROLE_USER is required",
                    content = @Content(schema = @Schema())),
            @ApiResponse(
                    responseCode = "401", description = "Unauthorized -  you must log in",
                    content = @Content(schema = @Schema())),
            @ApiResponse(
                    responseCode = "409", description = "It is impossible to correctly process the sent information",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class)))
    })
    public ResponseEntity<MessageResponse> getUserHistory(@RequestBody Set<ItemForAdditionalPurchases> items,
                                                          @RequestParam long orderId) {
        itemsForAdditionalPurchasesService.saveItems(items, orderId);
        return ResponseEntity.status(201).body(new MessageResponse("Items are saved"));
    }
}
