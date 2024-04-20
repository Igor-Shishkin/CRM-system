package com.crm.system.controllers;

import com.crm.system.models.logForUser.LogEntry;
import com.crm.system.playload.response.MessageResponse;
import com.crm.system.playload.response.TagForUserLogDTO;
import com.crm.system.services.LogEntryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.Set;

@Slf4j
@Tag(name = "Log controller", description = "Log management APIs")
@CrossOrigin(origins = "http://localhost:8081", maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("api/log")
public class LogEntryController {
    private final LogEntryService logEntryService;

    public LogEntryController(LogEntryService logEntryService) {
        this.logEntryService = logEntryService;
    }

    @Operation(summary = "Get user's log", tags = {"log for user", "log"})
    @GetMapping("/get-user-log")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Set<LogEntry>> getUserLog() throws UserPrincipalNotFoundException {
        Set<LogEntry> log = logEntryService.getUserLog();
        return ResponseEntity.ok(log);
    }

    @Operation(summary = "Get tags for log", tags = {"log for user", "tags"})
    @GetMapping("tags")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Set<TagForUserLogDTO>> getTagsForNewEntry() throws UserPrincipalNotFoundException {
        Set<TagForUserLogDTO> tags = logEntryService.getSetOfTags();
        return ResponseEntity.ok(tags);
    }

    @Operation(summary = "Save new entry to log", tags = {"log for user", "new entry"})
    @PostMapping()
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<MessageResponse> saveNewEntryForLog(@RequestBody LogEntry entry)
            throws UserPrincipalNotFoundException {
        logEntryService.saveNewEntryToLog(entry);
        return ResponseEntity.ok(new MessageResponse("Entry is saved"));
    }

    @Operation(summary = "Delete entry", tags = {"log for user", "delete"})
    @DeleteMapping()
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<MessageResponse> deleteMessage(@RequestParam long messageId) {
        logEntryService.deleteMessage(messageId);
        return ResponseEntity.ok(new MessageResponse("Message is deleted"));
    }

    @Operation(summary = "Change important status of entry", tags = {"log for user", "status"})
    @PutMapping("/change-important-status")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<MessageResponse> changeImportantStatus(@RequestParam long messageId) {
        logEntryService.changeImportantStatus(messageId);
        return ResponseEntity.ok(new MessageResponse("Status is changed"));
    }

    @Operation(summary = "Change important status of entry", tags = {"log for user", "status"})
    @PutMapping("/change-done-status")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<MessageResponse> changeDoneStatus(@RequestParam long messageId) {
        logEntryService.changeDoneStatus(messageId);
        return ResponseEntity.ok(new MessageResponse("Status is changed"));
    }
}
