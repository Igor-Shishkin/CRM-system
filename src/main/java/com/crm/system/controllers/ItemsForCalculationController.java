package com.crm.system.controllers;

import com.crm.system.exception.RequestOptionalIsEmpty;
import com.crm.system.exception.SubjectNotBelongToActiveUser;
import com.crm.system.models.history.HistoryMessage;
import com.crm.system.models.order.ItemForCalcualtion;
import com.crm.system.playload.response.MessageResponse;
import com.crm.system.services.ItemsForCalculationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.Set;

@Slf4j
@CrossOrigin(origins = "http://localhost:8081", maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("api/items-for-calculation")
public class ItemsForCalculationController {
    private final ItemsForCalculationService itemsForCalculationService;

    public ItemsForCalculationController(ItemsForCalculationService itemsForCalculationService) {
        this.itemsForCalculationService = itemsForCalculationService;
    }

    @Operation(summary = "Get order's items for calculation", tags = {"item", "calculation"})
    @PostMapping("/save-items")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_MODERATOR')")
    public ResponseEntity<?> getUserHistory
            (@RequestBody Set<ItemForCalcualtion> items, @RequestParam long orderId){
        try {
            itemsForCalculationService.saveItems(items, orderId);
            return ResponseEntity.ok(new MessageResponse("Calculations are saved"));
        } catch (RequestOptionalIsEmpty | SubjectNotBelongToActiveUser | UserPrincipalNotFoundException  e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
}
