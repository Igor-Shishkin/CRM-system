package com.crm.system.controllers;

import com.crm.system.models.logForUser.LogEntry;
import com.crm.system.playload.response.ClientInfoDTO;
import com.crm.system.playload.response.MessageResponse;
import com.crm.system.playload.response.TagForUserLogDTO;
import com.crm.system.services.LogEntryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.Set;

@RestController
@RequestMapping("api/log")
@Tag(name = "Log controller", description = "Log management APIs")
@Slf4j
@CrossOrigin(origins = "http://localhost:8081", maxAge = 3600, allowCredentials = "true")
public class LogEntryController {
    private final LogEntryService logEntryService;

    public LogEntryController(LogEntryService logEntryService) {
        this.logEntryService = logEntryService;
    }


    @GetMapping("/get-user-log")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get User Log",
            description = """
                    Retrieves the log entries for the active user.
                    This endpoint is accessible by users with either the 'ROLE_USER' or 'ROLE_ADMIN' authority.

                    The response contains an array of LogEntry objects representing the user's activity log.

                    If the user is not authenticated, a 401 Unauthorized response is returned.""",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "log is returns successfully",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = LogEntry.class)),
                                    mediaType = "application/json")),
                    @ApiResponse(
                            responseCode = "401", description = "Unauthorized -  you must log in",
                            content = @Content(schema = @Schema()))
            })
    public ResponseEntity<Set<LogEntry>> getUserLog() throws UserPrincipalNotFoundException {
        Set<LogEntry> log = logEntryService.getUserLog();
        return ResponseEntity.ok(log);
    }


    @GetMapping("tags")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Fetch Tags for New Log Entry",
            description = """
                    Retrieves the log tags available for the active user to use in a new log entry.
                    This endpoint is accessible by users with either the 'ROLE_USER' or 'ROLE_ADMIN' authority.

                    The response contains an array of TagForUserLogDTO objects representing the tags.

                    If the user is not authenticated, a 401 Unauthorized response is returned.""",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Tags are returns successfully",
                            content = @Content(
                                    array = @ArraySchema(schema = @Schema(implementation = TagForUserLogDTO.class)),
                                    mediaType = "application/json")),
                    @ApiResponse(
                            responseCode = "401", description = "Unauthorized -  you must log in",
                            content = @Content(schema = @Schema()))
            })
    public ResponseEntity<Set<TagForUserLogDTO>> getTagsForNewEntry() throws UserPrincipalNotFoundException {
        Set<TagForUserLogDTO> tags = logEntryService.getSetOfTags();
        return ResponseEntity.ok(tags);
    }


    @PostMapping()
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Save new entry to log",
            description = """
                    This endpoint allows users to save a new entry to the log. The entry is created with the current 
                    timestamp and is associated with the active user. 

                    Requires ROLE_USER or ROLE_ADMIN authorization.
                        
                    Expects a LogEntry object in the request body containing the details of the entry to be saved.
                        
                    Throws a UserPrincipalNotFoundException if:
                    - The active user cannot be found.
                        
                    If successful in saving the new entry:
                    - Sets the date of creation to the current timestamp.
                    - Associates the entry with the active user.
                    - Saves the entry to the database.
                    - Returns a message indicating successful saving.
                        
                    The request body should include.""",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = MessageResponse.class),
                                    mediaType = "application/json")),
                    @ApiResponse(
                            responseCode = "401", description = "Unauthorized -  you must log in",
                            content = @Content(schema = @Schema()))
            })
    public ResponseEntity<MessageResponse> saveNewEntryForLog(@RequestBody LogEntry entry)
            throws UserPrincipalNotFoundException {
        logEntryService.saveNewEntryToLog(entry);
        return ResponseEntity.ok(new MessageResponse("Entry is saved"));
    }


    @DeleteMapping()
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Delete entry", description = """
            This endpoint allows users with the roles 'ROLE_USER' or 'ROLE_ADMIN' to delete an entry by its ID. 
            The entry must belong to the active user. If the entry does not exist or does not belong to the user, 
            a 404 error is returned. Unauthorized requests will result in a 401 error.
                
            Expects an entryId as a request parameter to identify the entry to be deleted.
                
            Throws a RequestOptionalIsEmpty exception if:
            - The user doesn't have an entry with this ID.
                
            If successful in deleting the entry:
            - Removes the entry from the database.
            - Returns a message indicating successful deletion.
                
            The request parameter should include:
            - `entryId` (long): The ID of the entry to be deleted.""",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = MessageResponse.class),
                                    mediaType = "application/json")),
                    @ApiResponse(
                            responseCode = "404", description = "User doesn't have entry with this ID",
                            content = @Content(schema = @Schema(implementation = MessageResponse.class),
                                    mediaType = "application/json")),
                    @ApiResponse(
                            responseCode = "401", description = "Unauthorized -  you must log in",
                            content = @Content(schema = @Schema()))
            })
    public ResponseEntity<MessageResponse> deleteMessage(@RequestParam long entryId) {
        logEntryService.deleteMessage(entryId);
        return ResponseEntity.ok(new MessageResponse("Entry is deleted"));
    }


    @PatchMapping("/change-important-status")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Change __IMPORTANT__ status of entry",
            description = """
                    This endpoint allows users with the roles 'ROLE_USER' or 'ROLE_ADMIN' to change the __IMPORTANT__ status of an entry by its ID.
                    The entry must belong to the active user. If the entry does not exist or does not belong to the user, a 404 error is returned.
                    Unauthorized requests will result in a 401 error.

                    Expects an entryId as a request parameter to identify the entry whose IMPORTANT status is to be changed.

                    Throws a RequestOptionalIsEmpty exception if:
                    - The user doesn't have an entry with this ID.

                    If successful in changing the IMPORTANT status:
                    - Toggles the IMPORTANT status of the entry.
                    - Saves the updated entry to the database.
                    - Returns a message indicating successful status change.

                    The request parameter should include:
                    - `entryId` (long): The ID of the entry whose IMPORTANT status is to be changed.""",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = MessageResponse.class),
                                    mediaType = "application/json")),
                    @ApiResponse(
                            responseCode = "404", description = "User doesn't have entry with this ID",
                            content = @Content(schema = @Schema(implementation = MessageResponse.class),
                                    mediaType = "application/json")),
                    @ApiResponse(
                            responseCode = "401", description = "Unauthorized -  you must log in",
                            content = @Content(schema = @Schema()))
            })
    public ResponseEntity<MessageResponse> changeImportantStatus(@RequestParam long entryId) {
        logEntryService.changeImportantStatus(entryId);
        return ResponseEntity.ok(new MessageResponse("Status is changed"));
    }




    @PatchMapping("/change-done-status")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Change __DONE__ status of entry",
            description = """
                    This endpoint allows users with the roles 'ROLE_USER' or 'ROLE_ADMIN' to change the __DONE__ status of an entry by its ID.
                    The entry must belong to the active user. If the entry does not exist or does not belong to the user, a 404 error is returned.
                    Unauthorized requests will result in a 401 error.

                    Expects an entryId as a request parameter to identify the entry whose IMPORTANT status is to be changed.

                    Throws a RequestOptionalIsEmpty exception if:
                    - The user doesn't have an entry with this ID.

                    If successful in changing the IMPORTANT status:
                    - Toggles the DONE status of the entry.
                    - Saves the updated entry to the database.
                    - Returns a message indicating successful status change.

                    The request parameter should include:
                    - `entryId` (long): The ID of the entry whose IMPORTANT status is to be changed.""")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class),
                            mediaType = "application/json")),
            @ApiResponse(
                    responseCode = "404", description = "User doesn't have entry with this ID",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class),
                            mediaType = "application/json")),
            @ApiResponse(
                    responseCode = "401", description = "Unauthorized -  you must log in",
                    content = @Content(schema = @Schema()))
    })
    public ResponseEntity<MessageResponse> changeDoneStatus(@RequestParam long entryId) {
        logEntryService.changeDoneStatus(entryId);
        return ResponseEntity.ok(new MessageResponse("Status is changed"));
    }
}
