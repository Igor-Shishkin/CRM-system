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


@RestController
@RequestMapping("api/user-board")
@CrossOrigin(origins = "http://localhost:8081", maxAge = 3600, allowCredentials = "true")
@Tag(name = "Client controller", description = "Client management APIs")
@Slf4j
public class ClientController {
    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }



    @PostMapping("/add-new-client")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Add new Client",
            description = """
                    Creates a new client based on the provided information in AddClientDTO. \
                    Requires ROLE_USER authorization.

                    Expects an AddClientDTO object containing the details for the new client.

                    Throws a ClientAlreadyExistException if:
                    - A client with the given email already exists.

                    If successful in adding the new client:
                    - Saves the new client to the database. ClientStatus is LEAD.
                    - Logs an entry indicating the new client has been added.
                    - Returns the ID of the newly created client.

                    The AddClientDTO object should include:
                    - `fullName` (String, NotEmpty, max=50): The full name of the client.
                    - `email` (String, verified as Email, NotBlank, max=100): The email address of the client.
                    - `phoneNumber` (String, max=50): The phone number of the client.
                    - `address` (String, max=300): The address of the client.""",
            responses = {
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
                            content = @Content(schema = @Schema())),
                    @ApiResponse(
                            responseCode = "400", description = "Request data is incorrect",
                            content = @Content(schema = @Schema()))
            })
    public ResponseEntity<Long> addNewClient(@Valid @RequestBody AddClientDTO addLeadRequest)
            throws UserPrincipalNotFoundException {
        long leadId = clientService.addNewClient(addLeadRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(leadId);
    }





    @PatchMapping("/send-client-to-black-list")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Sends client by ID to the blacklist",
            description = """
                    Sends a client identified by clientId to the blacklist without deleting any data. \
                    Requires ROLE_USER authorization.

                    Expects a clientId as a request parameter to identify the client to be blacklisted.

                    Throws a UserPrincipalNotFoundException if:
                    - The user doesn't have client with this ID.

                    If successful in blacklisting the client:
                    - Updates the client's status to BLACKLIST and sets the date of last change.
                    - Logs an entry indicating the client has been blacklisted.
                    - Returns a message indicating successful blacklisting.

                    The request parameter should include:
                    - `clientId` (long): The ID of the client to be blacklisted.""",
            responses = {
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
    public ResponseEntity<MessageResponse> sendClientToBlackList(@RequestParam long clientId)
            throws UserPrincipalNotFoundException {
        clientService.sentToBlackList(clientId);
        return ResponseEntity.ok(new MessageResponse(String.format("Lead with ID=%d is in blacklist", clientId)));
    }




    @PatchMapping("/restore-client-from-black-list")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Restore client from blackList",
            description = """
                    Restores a client identified by clientId from the blacklist. 
                    Requires ROLE_USER authorization.

                    Expects a clientId parameter identifying the client to be restored.

                    Throws a RequestOptionalIsEmpty exception if:
                    - The user doesn't have client with this ID.

                    If successful in restoring the client:
                    - Updates the client's status based on their order history.
                    - If the client has any paid orders, their status is set to CLIENT.
                    - If the client has no paid orders, their status is set to LEAD.
                    - Updates the date of last change for the client.
                    - Creates a log entry indicating the client has been restored from the blacklist.
                    - Returns a message response indicating the client has been restored.""",
            responses = {
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
    public ResponseEntity<MessageResponse> restoreClientFromBlackList(@RequestParam long clientId)
            throws UserPrincipalNotFoundException {
        clientService.restoreClientFromBlackList(clientId);
        return ResponseEntity.ok(new MessageResponse(
                String.format("Client with ID=%d is restored from black list", clientId))
        );
    }




    @GetMapping("/clients")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Get clients info",
            description = """
                    Retrieves information about all clients with the status of 'CLIENT' for the active user. \
                    Requires ROLE_USER authorization.

                    The response contains an array of ClientInfoDTO objects.""",
            responses = {
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
    @Operation(summary = "Get LEAD clients info",
            description = """
                    Retrieves information about all clients with the status of 'LEAD' for the active user. \
                    Requires ROLE_USER authorization.

                    The response contains an array of ClientInfoDTO objects.""",
            responses = {
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
            description = """
                    Retrieves information about all clients with the status of 'BLACKLIST' for the active user. \
                    Requires ROLE_USER authorization.

                    The response contains an array of ClientInfoDTO objects.""",
            responses = {
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
            description = """
                    Retrieves detailed information about a specific client, including their orders, for the active user. \
                    Requires ROLE_USER authorization.

                    Throws a RequestOptionalIsEmpty exception if:
                    - The user doesn't have client with this ID.
                    
                    The response includes the following details about the client:
                    - Client information
                    - List of Orders (List<Order>) with order-specific information excluding additional purchases.
                    """,
            responses = {
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





    @PatchMapping("edit-client-data")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Edit Client's info",
            description = """
                    Allows the active user to edit the information of an existing client. \
                    Requires ROLE_USER authorization.

                    Expects an EditClientDataDTO object containing the updated details for the client. \
                    The EditClientDataDTO object should include:
                    - `clientId` (Long, NotNull, positive): The ID of the client whose data is being edited.
                    - `fullName` (String, NotEmpty, max: 255): The updated full name of the client.
                    - `email` (String, NotBlank, verified email, max: 100): The updated email address of the client.
                    - `phoneNumber` (String, max: 50): The updated phone number of the client.
                    - `address` (String, max: 300): The updated address of the client.

                    Throws a RequestOptionalIsEmpty exception if:
                    - The user doesn't have client with this ID.

                    If successful, the client's information is updated in the database and a confirmation message is returned.""",
            responses = {
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
                            content = @Content(schema = @Schema())),
                    @ApiResponse(
                            responseCode = "400", description = "Request data is incorrect",
                            content = @Content(schema = @Schema()))
            })
    public ResponseEntity<MessageResponse> editClientInfo(@RequestBody @Valid EditClientDataDTO request) {
        clientService.editClientData(request);
        return ResponseEntity.ok(new MessageResponse("Changes are saved"));
    }
}
