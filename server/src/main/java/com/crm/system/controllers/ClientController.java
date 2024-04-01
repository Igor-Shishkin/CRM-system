package com.crm.system.controllers;

import com.crm.system.models.Client;
import com.crm.system.playload.request.AddLeadDTO;
import com.crm.system.playload.request.EditClientDataDTO;
import com.crm.system.playload.response.ClientInfoDTO;
import com.crm.system.playload.response.MessageResponse;
import com.crm.system.services.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
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

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @Operation(summary = "Add new Lead", tags = {"Client", "add"})
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/add-new-lead")
    public ResponseEntity<Long> addNewLead(@Valid @RequestBody AddLeadDTO addLeadRequest)
                        throws UserPrincipalNotFoundException {
        long leadId = clientService.addNewLead(addLeadRequest);
        return ResponseEntity.ok(leadId);
    }

    @Operation(summary = "Sent client to blackList by ID", tags = {"client", "lead", "black list"})
    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping("/send-client-to-black-list")
    public ResponseEntity<MessageResponse> sendClientToBlackList(@RequestParam long clientId) {
        clientService.sentToBlackList(clientId);
        return ResponseEntity.ok(new MessageResponse(String.format("Lead with %d id is in blacklist", clientId)));
    }

    @Operation(summary = "Restore client from blackList by ID", tags = {"client", "lead", "black list"})
    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping("/restore-client-from-black-list")
    public ResponseEntity<MessageResponse> restoreClientFromBlackList(@RequestParam long clientId) {
        clientService.restoreClientFromBlackList(clientId);
        return ResponseEntity.ok(new MessageResponse(String.format("Client with %d id is restored from black list", clientId)));
    }

    @Operation(summary = "Get all clients", tags = {"clients", "get"})
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/clients")
    public ResponseEntity<Set<ClientInfoDTO>> getAllClientsForUser()
                        throws UserPrincipalNotFoundException {
        Set<ClientInfoDTO> clients = clientService.getClientsForUser();
        return ResponseEntity.ok(clients);
    }

    @Operation(summary = "Get all Leads", tags = {"leads", "get"})
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/leads")
    public ResponseEntity<List<ClientInfoDTO>> getAllLeadsForUser()
                        throws UserPrincipalNotFoundException {
        List<ClientInfoDTO> leads = clientService.getLeadsForUser();
        return ResponseEntity.ok(leads);
    }

    @Operation(summary = "Get all blacklist clients", tags = {"blacklist", "get"})
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/get-black-list-clients")
    public ResponseEntity<List<ClientInfoDTO>> getBlackListClientsForUser()
                        throws UserPrincipalNotFoundException {
        List<ClientInfoDTO> clients = clientService.getBlackListClientsForUser();
        return ResponseEntity.ok(clients);
    }

    @Operation(summary = "Get Client's info", tags = {"client", "info"})
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/client-info")
    public ResponseEntity<Client> getClient(@RequestParam long clientId) {
        Client client = clientService.getClient(clientId);
        return ResponseEntity.ok(client);
    }

    @Operation(summary = "Edit Client's info", tags = {"client", "info"})
    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping("edit-client-data")
    public ResponseEntity<MessageResponse> editClientInfo(@RequestBody EditClientDataDTO request) {
        clientService.editClientData(request);
        return ResponseEntity.ok(new MessageResponse("Changes are saved!"));
    }

    @Operation(summary = "test")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("test")
    public ResponseEntity<MessageResponse> test() {
        log.info("CLIENT CONTROLLER");
        return ResponseEntity.ok(new MessageResponse("Changes are saved!"));
    }
}
