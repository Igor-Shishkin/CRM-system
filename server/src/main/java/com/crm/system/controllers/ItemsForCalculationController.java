package com.crm.system.controllers;

import com.crm.system.models.order.ItemForAdditionalPurchases;
import com.crm.system.playload.response.MessageResponse;
import com.crm.system.services.ItemsForCalculationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Slf4j
@Tag(name = "Calculations controller", description = "Items for calculation management APIs")
@CrossOrigin(origins = "http://localhost:8081", maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("api/user-board/items-for-addition-purchases")
public class ItemsForCalculationController {
    private final ItemsForCalculationService itemsForCalculationService;

    public ItemsForCalculationController(ItemsForCalculationService itemsForCalculationService) {
        this.itemsForCalculationService = itemsForCalculationService;
    }

    @Operation(summary = "Get order's items for calculation", tags = {"item", "calculation"})
    @PostMapping("/save-items")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> getUserHistory(@RequestBody Set<ItemForAdditionalPurchases> items, @RequestParam long orderId) {
        itemsForCalculationService.saveItems(items, orderId);
        return ResponseEntity.ok(new MessageResponse("Calculations are saved"));
    }
}
