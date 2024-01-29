package com.crm.system.controllers;

import com.crm.system.exception.ClientAlreadyExistException;
import com.crm.system.exception.NameOrEmailIsEmptyException;
import com.crm.system.exception.RequestOptionalIsEmpty;
import com.crm.system.exception.SubjectNotBelongToActiveUser;
import com.crm.system.models.Client;
import com.crm.system.playload.request.AddLeadDTO;
import com.crm.system.playload.request.EditClientDataDTO;
import com.crm.system.playload.response.ClientInfoDTO;
import com.crm.system.playload.response.MessageResponse;
import com.crm.system.repository.UserRepository;
import com.crm.system.services.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.List;
import java.util.Set;

@Slf4j
@Tag(name = "Client controller", description = "Client management APIs")
@CrossOrigin(origins = "http://localhost:8081", maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("api/user-board")
public class ClientController {
    private final ClientService clientService;

    public ClientController(ClientService clientService, UserRepository userRepository) {
        this.clientService = clientService;
    }

    @Operation(summary = "Add new Lead", tags = { "Client", "add"})
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/add-new-lead")
    public ResponseEntity<?> addNewLead(@Valid @RequestBody AddLeadDTO addLidRequest) {
        try {
            long leadId = clientService.addNewLead(addLidRequest);
            return ResponseEntity.ok(leadId);
        } catch (UserPrincipalNotFoundException | ClientAlreadyExistException e) {
            log.error("Adding new lead error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Adding new lead error: " + e.getMessage()));
        }
    }

    @Operation(summary = "Sent client to blackList by ID", tags = { "client", "lead", "black list"})
    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping("/send-client-to-black-list")
    public ResponseEntity<?> sendClientToBlackList(@RequestParam long clientId) {
        try {
            clientService.sentToBlackList(clientId);
            return ResponseEntity.ok(new MessageResponse(String.format("Lead with %d id is in blacklist", clientId)));
        } catch (IllegalArgumentException | SubjectNotBelongToActiveUser | UserPrincipalNotFoundException e) {
            log.error("Sending to blacklist error: " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Sending to blacklist error: " + e.getMessage()));
        }
    }
    @Operation(summary = "Restore client from blackList by ID", tags = { "client", "lead", "black list"})
    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping("/restore-client-from-black-list")
    public ResponseEntity<?> restoreClientFromBlackList(@RequestParam long clientId) {
        try {
            clientService.restoreClientFromBlackList(clientId);
            return ResponseEntity.ok(new MessageResponse(String.format("Client with %d id is restored from black list", clientId)));
        } catch (IllegalArgumentException | SubjectNotBelongToActiveUser | UserPrincipalNotFoundException e) {
            log.error("Restore from blacklist error: " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Restore from blacklist error: " + e.getMessage()));
        }
    }

    @Operation(summary = "Get all clients", tags = { "clients", "get"})
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/clients")
    public ResponseEntity<?> getAllClientsForUser() {
        try {
            Set<ClientInfoDTO> clients = clientService.getClientsForUser();
            return ResponseEntity.ok(clients);
        } catch (UserPrincipalNotFoundException e) {
            log.error("Authorisation Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("User isn't defined: " + e.getMessage()));
        }
    }
    @Operation(summary = "Get all Leads", tags = { "leads", "get"})
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/leads")
    public ResponseEntity<?> getAllLeadsForUser() {
        try {
            List<ClientInfoDTO> leads = clientService.getLeadsForUser();
            return ResponseEntity.ok(leads);
        } catch (UserPrincipalNotFoundException e) {
            log.error("Authorisation Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("User isn't defined. " + e.getMessage()));
        }
    }
    @Operation(summary = "Get all blacklist clients", tags = { "blacklist", "get"})
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/get-black-list-clients")
    public ResponseEntity<?> getBlackListClientsForUser() {
        try {
            List<ClientInfoDTO> clients = clientService.getBlackListClientsForUser();
            return ResponseEntity.ok(clients);
        } catch (UserPrincipalNotFoundException e) {
            log.error("Authorisation Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("User isn't defined. " + e.getMessage()));
        }
    }
    @Operation(summary = "Get Client's info", tags = { "client", "info"})
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/client-info")
    public ResponseEntity<?> getClient(@RequestParam long clientId) {
        try {
            Client client = clientService.getClient(clientId);
            return ResponseEntity.ok(client);
        } catch (RequestOptionalIsEmpty | SubjectNotBelongToActiveUser | UserPrincipalNotFoundException e) {
            log.error(e.getMessage() + ". Error: " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse(e.getMessage() + ". Error: " + e));
        }
    }

    @Operation(summary = "Edit Client's info", tags = { "client", "info"})
    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping("edit-client-data")
    public ResponseEntity<?> editClientInfo(@RequestBody EditClientDataDTO request) {
        try {
            clientService.editClientData(request);
            return ResponseEntity.ok(new MessageResponse("Changes are saved!"));
        } catch (RequestOptionalIsEmpty | SubjectNotBelongToActiveUser |
                 NameOrEmailIsEmptyException | UserPrincipalNotFoundException e) {
            log.error("Save client data error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Save client data error: " + e.getMessage()));
        }
    }



}
