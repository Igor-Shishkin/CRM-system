package com.crm.system.controllers;

import com.crm.system.models.logForUser.LogEntry;
import com.crm.system.playload.response.MessageResponse;
import com.crm.system.playload.response.TagForHistoryMessageDTO;
import com.crm.system.services.LogEntryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("api/history-message")
public class LogEntryController {
    private final LogEntryService logEntryService;

    public LogEntryController(LogEntryService logEntryService) {
        this.logEntryService = logEntryService;
    }

    @Operation(summary = "Get user's logForUser", tags = {"user", "logForUser"})
    @GetMapping("/get-history")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Set<LogEntry>> getUserHistory() throws UserPrincipalNotFoundException {
        Set<LogEntry> history = logEntryService.getUserHistory();
        return ResponseEntity.ok(history);
    }

    @Operation(summary = "Get tags for logForUser message", tags = {"logForUser", "tags"})
    @GetMapping("tags")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<TagForHistoryMessageDTO>> getTagsForNewHistoryMessage() throws UserPrincipalNotFoundException {
        List<TagForHistoryMessageDTO> tags = logEntryService.getListOfTags();
        return ResponseEntity.ok(tags);
    }

    @Operation(summary = "Save logForUser message", tags = {"logForUser", "new message"})
    @PostMapping()
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<MessageResponse> saveMessage(@RequestBody LogEntry message) throws UserPrincipalNotFoundException {
        logEntryService.saveNewMessage(message);
        return ResponseEntity.ok(new MessageResponse("Message is saved"));
    }

    @Operation(summary = "Delete logForUser message", tags = {"logForUser", "delete"})
    @DeleteMapping()
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<MessageResponse> deleteMessage(@RequestParam long messageId) {
        logEntryService.deleteMessage(messageId);
        return ResponseEntity.ok(new MessageResponse("Message is deleted"));
    }

    @Operation(summary = "Change important status of message", tags = {"logForUser", "status"})
    @PutMapping("/change-important-status")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<MessageResponse> changeImportantStatus(@RequestParam long messageId) {
        logEntryService.changeImportantStatus(messageId);
        return ResponseEntity.ok(new MessageResponse("Status is changed"));
    }

    @Operation(summary = "Change important status of message", tags = {"logForUser", "status"})
    @PutMapping("/change-done-status")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<MessageResponse> changeDoneStatus(@RequestParam long messageId) {
        logEntryService.changeDoneStatus(messageId);
        return ResponseEntity.ok(new MessageResponse("Status is changed"));
    }
}
