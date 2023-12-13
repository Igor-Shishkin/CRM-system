package com.crm.system.controllers;

import com.crm.system.exception.ClientAlreadyExistException;
import com.crm.system.exception.RequestOptionalIsEmpty;
import com.crm.system.exception.SubjectNotBelongToActiveUser;
import com.crm.system.models.Client;
import com.crm.system.playload.request.AddLidRequest;
import com.crm.system.playload.response.ClientInfoResponse;
import com.crm.system.playload.response.MessageResponse;
import com.crm.system.repository.UserRepository;
import com.crm.system.services.ClientService;
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
public class ClientController {
    private final ClientService lidService;

    public ClientController(ClientService clientService, UserRepository userRepository) {
        this.lidService = clientService;
    }

    @Operation(summary = "Add new Lead", tags = { "Client", "add"})
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_MODERATOR')")
    @PostMapping()
    public ResponseEntity<?> addNewLead(@Valid @RequestBody AddLidRequest addLidRequest) {
        try {
            lidService.addNewLead(addLidRequest);
            log.info(String.format("Lid %s is added", addLidRequest.getFullName()));
            return ResponseEntity.ok(new MessageResponse("New LEAD is save"));
        } catch (UserPrincipalNotFoundException e) {
            log.error("User isn't defined. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("User isn't defined. " + e.getMessage()));
        } catch (ClientAlreadyExistException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse(e.getMessage()));
        }

    }

    @Operation(summary = "Sent client to blackList by ID", tags = { "client", "lead", "black list"})
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_MODERATOR')")
    @PutMapping("/to-black-list")
    public ResponseEntity<?> deleteClientById(@RequestParam long leadId) {
        System.out.println("\n" + leadId + "\n");
        try {
            lidService.sentToBlackList(leadId);
            log.info(String.format("Client with %d id on the black list", leadId));
            return ResponseEntity.ok(new MessageResponse(String.format("Lead with %d id is deleted", leadId)));
        } catch (IllegalArgumentException | SubjectNotBelongToActiveUser | UserPrincipalNotFoundException e) {
            log.error("Delete user error: " + e.toString());
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
    @Operation(summary = "Get all Leads", tags = { "leads", "get"})
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_MODERATOR')")
    @GetMapping("/lids")
    public ResponseEntity<?> getAllLeadsForUser() {
        try {
            List<ClientInfoResponse> leads = lidService.getAllLeads();
            return ResponseEntity.ok(leads);
        } catch (UserPrincipalNotFoundException e) {
            log.error("Authorisation Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("User isn't defined. " + e.getMessage()));
        }
    }
    @Operation(summary = "Get Client's info", tags = { "client", "info"})
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_MODERATOR')")
    @GetMapping("/client-info")
    public ResponseEntity<?> getClientInfo(@RequestParam long clientId) {
        try {
            Client client = lidService.getClient(clientId);
            return ResponseEntity.ok(client);
        } catch (RequestOptionalIsEmpty | SubjectNotBelongToActiveUser e) {
            log.error(e.getMessage() + ". Error: " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse(e.getMessage() + ". Error: " + e));
        }
    }



}
