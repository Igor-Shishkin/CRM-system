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
    @Operation(summary = "Get user's log", description = "Returns to the user all entries from his log")
    @ApiResponses({
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
    @Operation(summary = "Get tags for user's log", description = "Returns all log's tags for the user")
    @ApiResponses({
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
            description = "Endpoint to save a new log entry. Requires ROLE_USER or ROLE_ADMIN authorization. " +
                    "The request body should contain the following fields:\n\n" +
                    "- `text` (String, required): The main content of the log entry.\n" +
                    "- `dateOfCreation` (LocalDateTime): Date and time when the log entry was created.\n" +
                    "- `deadline` (LocalDateTime): Optional deadline for the log entry.\n" +
                    "- `isImportant` (boolean): Indicates if the log entry is marked as important.\n" +
                    "- `isDone` (boolean): Indicates if the log entry is marked as completed.\n" +
                    "- `additionalInformation` (String): Additional details or notes related to the log entry.\n" +
                    "- `tagName` (TagName enum): Enum representing the tag name associated with the log entry.\n" +
                    "- `tagId` (long): ID related to the tag associated with the log entry.")
    @ApiResponses({
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
    @Operation(summary = "Delete entry", description = "Delete entry by ID")
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
    public ResponseEntity<MessageResponse> deleteMessage(@RequestParam long entryId) {
        logEntryService.deleteMessage(entryId);
        return ResponseEntity.ok(new MessageResponse("Entry is deleted"));
    }





    @PatchMapping("/change-important-status")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Change __IMPORTANT__ status of entry",
            description = "Change the status of the entry __IMPORTANT__ to the opposite. Required entry ID.")
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
    public ResponseEntity<MessageResponse> changeImportantStatus(@RequestParam long entryId) {
        logEntryService.changeImportantStatus(entryId);
        return ResponseEntity.ok(new MessageResponse("Status is changed"));
    }





    @PatchMapping("/change-done-status")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Change __DONE__ status of entry",
            description = "Change the status of the entry __DONE__ to the opposite. Required entry ID.")
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
