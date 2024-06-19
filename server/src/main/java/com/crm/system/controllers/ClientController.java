package com.crm.system.controllers;

import com.crm.system.models.Client;
import com.crm.system.playload.request.AddClientDTO;
import com.crm.system.playload.request.EditClientDataDTO;
import com.crm.system.playload.response.ClientInfoDTO;
import com.crm.system.playload.response.MessageResponse;
import com.crm.system.services.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.attribute.UserPrincipalNotFoundException;
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




    @PostMapping("/add-new-client")
    @Operation(summary = "Add new Lead",
            description = "Adds a new client to the system with the provided details. " +
            "<br/><br/>" +
            "**Request Body:**<br/>" +
            "- `fullName` (String): The full name of the client. Must not be null, empty, or consist " +
            "solely of whitespace. Maximum length is 50 characters.<br/>" +
            "- `email` (String): The email address of the client. Must be a valid email format and must " +
            "not be null, empty, or consist solely of whitespace. Maximum length is 80 characters.<br/>" +
            "- `phoneNumber` (String): The phone number of the client. " +
            "The maximum length is 50 characters.<br/>" +
            "- `address` (String): The address of the client. " +
            "The maximum length is 300 characters.<br/><br/>")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    content = @Content(schema = @Schema(type = "integer", format = "int64", name = "leadId",
                            description = "Returns the new client ID"),
                            mediaType = "application/json")),
            @ApiResponse(
                    responseCode = "409", description = "Client with this email already exists",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class),
                            mediaType = "application/json")),
            @ApiResponse(
                    responseCode = "403", description = "ROLE_USER is required",
                    content = @Content(schema = @Schema())),
            @ApiResponse(
                    responseCode = "401", description = "Unauthorized -  you must log in",
                    content = @Content(schema = @Schema()))
    })
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Long> addNewClient(@Valid @RequestBody AddClientDTO addLeadRequest)
                        throws UserPrincipalNotFoundException {
        long leadId = clientService.addNewLead(addLeadRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(leadId);
    }






    @PutMapping("/send-client-to-black-list")
    @Operation(summary = "Sent client to blackList",
            description = "Sends client by ID to the blacklist without deleting any data and add entry to log about it")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "The client has been blacklisted",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class),
                            mediaType = "application/json")),
            @ApiResponse(
                    responseCode = "404", description = "Active User doesn't have client with this ID",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class),
                            mediaType = "application/json")),
            @ApiResponse(
                    responseCode = "403", description = "ROLE_USER is required",
                    content = @Content(schema = @Schema())),
            @ApiResponse(
                    responseCode = "401", description = "Unauthorized -  you must log in",
                    content = @Content())
    })
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<MessageResponse> sendClientToBlackList(@RequestParam long clientId) {
        clientService.sentToBlackList(clientId);
        return ResponseEntity.ok(new MessageResponse(String.format("Lead with ID=%d is in blacklist", clientId)));
    }






    @PutMapping("/restore-client-from-black-list")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Restore client from blackList",
            description = "Restore client from blackList by ID and add entry to log about it")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "The client is returned from blacklist",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class),
                            mediaType = "application/json")),
            @ApiResponse(
                    responseCode = "404", description = "Active User doesn't have client with this ID",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class),
                            mediaType = "application/json")),
            @ApiResponse(
                    responseCode = "403", description = "ROLE_USER is required",
                    content = @Content(schema = @Schema())),
            @ApiResponse(
                    responseCode = "401", description = "Unauthorized -  you must log in",
                    content = @Content(schema = @Schema()))
    })
    public ResponseEntity<MessageResponse> restoreClientFromBlackList(@RequestParam long clientId) {
        clientService.restoreClientFromBlackList(clientId);
        return ResponseEntity.ok(new MessageResponse(
                String.format("Client with ID=%d is restored from black list", clientId))
        );
    }






    @GetMapping("/clients")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Get clients info",
            description = "Get main information about all the user's clients with Status.CLIENT")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Clients information successfully sent",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = ClientInfoDTO.class,
                            description = "Returns main information about all the user's clients with Status.CLIENT")),
                            mediaType = "application/json")),
            @ApiResponse(
                    responseCode = "403", description = "ROLE_USER is required",
                    content = @Content(schema = @Schema())),
            @ApiResponse(
                    responseCode = "401", description = "Unauthorized -  you must log in",
                    content = @Content(schema = @Schema()))
    })
    public ResponseEntity<Set<ClientInfoDTO>> getAllClientsForUser() {
        Set<ClientInfoDTO> clients = clientService.getClientsWithClientStatusForUser();
        return ResponseEntity.ok(clients);
    }






    @GetMapping("/leads")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Get leads info",
            description = "Get main information about all the user's clients with Status.LEAD")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Leads information successfully sent",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = ClientInfoDTO.class,
                              description = "Returns main information about all the user's clients with Status.LEAD")),
                            mediaType = "application/json")),
            @ApiResponse(
                    responseCode = "403", description = "ROLE_USER is required",
                    content = @Content(schema = @Schema())),
            @ApiResponse(
                    responseCode = "401", description = "Unauthorized -  you must log in",
                    content = @Content(schema = @Schema()))
    })
    public ResponseEntity<Set<ClientInfoDTO>> getAllLeadsForUser() {
        Set<ClientInfoDTO> leads = clientService.getClientsWithLeadStatusForUser();
        return ResponseEntity.ok(leads);
    }






    @GetMapping("/get-black-list-clients")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Get blacklist clients info",
            description = "Get main information about all the user's clients with Status.BLACKLIST")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Clients information successfully sent",
                    content = @Content(
                         array = @ArraySchema(schema = @Schema(implementation = ClientInfoDTO.class,
                          description = "Returns main information about all the user's clients with Status.BLACKLIST")),
                         mediaType = "application/json")),
            @ApiResponse(
                    responseCode = "403", description = "ROLE_USER is required",
                    content = @Content(schema = @Schema())),
            @ApiResponse(
                    responseCode = "401", description = "Unauthorized -  you must log in",
                    content = @Content(schema = @Schema()))
    })
    public ResponseEntity<Set<ClientInfoDTO>> getBlackListClientsForUser() {
        Set<ClientInfoDTO> clients = clientService.getClientsWithBlacklistStatusForUser();
        return ResponseEntity.ok(clients);
    }






    @GetMapping("/client-info")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Get Client's info",
            description = "Get detailed information about the client and his/her orders by ID")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Client's information successfully sent",
                    content = @Content(schema = @Schema(implementation = Client.class,
                            description = "Returns detailed information about the client and his/her orders"),
                            mediaType = "application/json")),
            @ApiResponse(
                    responseCode = "404", description = "Active User doesn't have client with this ID",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class),
                            mediaType = "application/json")),
            @ApiResponse(
                    responseCode = "403", description = "ROLE_USER is required",
                    content = @Content(schema = @Schema())),
            @ApiResponse(
                    responseCode = "401", description = "Unauthorized -  you must log in",
                    content = @Content(schema = @Schema()))
    })
    public ResponseEntity<Client> getClient(@RequestParam long clientId) {
        Client client = clientService.getInfoWithOrdersClient(clientId);
        return ResponseEntity.ok(client);
    }






    @PutMapping("edit-client-data")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Edit Client's info",
                description = "Adds a new client to the system with the provided details. " +
                        "<br/><br/>" +
                        "**Request Body:**<br/>" +
                        "- `clientId` (Long): The unique identifier for the client. Must not be null or empty.<br/>" +
                        "- `fullName` (String): The full name of the client. Must not be null, empty, or consist solely " +
                        "of whitespace.<br/>" +
                        "- `email` (String): The email address of the client. Must be a valid email format and must not " +
                        "be null, empty, or consist solely of whitespace.<br/>" +
                        "- `address` (String): The address of the client. The maximum length is 300 characters.<br/>" +
                        "- `phoneNumber` (String): The phone number of the client. The maximum length is 50 characters." +
                        "<br/><br/>")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Client's information was successfully edited",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class),
                            mediaType = "application/json")),
            @ApiResponse(
                    responseCode = "404", description = "Active User doesn't have client with this ID",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class),
                            mediaType = "application/json")),
            @ApiResponse(
                    responseCode = "403", description = "ROLE_USER is required",
                    content = @Content(schema = @Schema())),
            @ApiResponse(
                    responseCode = "401", description = "Unauthorized -  you must log in",
                    content = @Content(schema = @Schema()))
    })
    public ResponseEntity<MessageResponse> editClientInfo(@RequestBody EditClientDataDTO request) {
        clientService.editClientData(request);
        return ResponseEntity.ok(new MessageResponse("Changes are saved"));
    }
}
