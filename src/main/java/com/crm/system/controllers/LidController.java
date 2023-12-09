package com.crm.system.controllers;

import com.crm.system.exception.SubjectNotBelongToActiveUser;
import com.crm.system.playload.request.AddLidRequest;
import com.crm.system.playload.response.ClientInfoResponse;
import com.crm.system.playload.response.LidInfoResponse;
import com.crm.system.playload.response.MessageResponse;
import com.crm.system.repository.UserRepository;
import com.crm.system.services.LidService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.List;

@Slf4j
@CrossOrigin(origins = "http://localhost:8081", maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("api/user-board")
public class LidController {
    private final LidService lidService;

    public LidController(LidService clientService, UserRepository userRepository) {
        this.lidService = clientService;
    }

    @Operation(summary = "Add new LID", tags = { "Lid", "add"})
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_MODERATOR')")
    @PostMapping()
    public ResponseEntity<?> addNewLid(@Valid @RequestBody AddLidRequest addLidRequest) {
        try {
            lidService.addNewLid(addLidRequest);
            log.info(String.format("Lid %s  is deleted", addLidRequest.getFullName()));
            return ResponseEntity.ok(new MessageResponse("New LID is save"));
        } catch (UserPrincipalNotFoundException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("User isn't defined. " + e.getMessage()));
        }

    }

    @Operation(summary = "Delete LID by ID", tags = { "Lid", "delete"})
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_MODERATOR')")
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteLidById(@RequestParam long lidId) {
        System.out.println("\n" + lidId + "\n");
        try {
            lidService.deleteLidById(lidId);
            log.info(String.format("Lid with %d id is deleted", lidId));
            return ResponseEntity.ok(new MessageResponse(String.format("Lid with %d id is deleted", lidId)));
        } catch (IllegalArgumentException | SubjectNotBelongToActiveUser | UserPrincipalNotFoundException e) {
            log.error("Delete user error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Delete user error: " + e.getMessage()));
        }
    }

    @Operation(summary = "Get all clients", tags = { "clients", "get"})
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_MODERATOR')")
    @GetMapping("/clients")
    public ResponseEntity<?> getAllClientsForUser() {
        try {
            List<ClientInfoResponse> clients = lidService.getAllClients();
            return ResponseEntity.ok(clients);
        } catch (UserPrincipalNotFoundException e) {
            log.error("Authorisation Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("User isn't defined: " + e.getMessage()));
        }
    }
    @Operation(summary = "Get all Lids", tags = { "lids", "get"})
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_MODERATOR')")
    @GetMapping("/lids")
    public ResponseEntity<?> getAllLidsForUser() {
        try {
            List<LidInfoResponse> lids = lidService.getAllLids();
            return ResponseEntity.ok(lids);
        } catch (UserPrincipalNotFoundException e) {
            log.error("Authorisation Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("User isn't defined. " + e.getMessage()));
        }
    }
}
