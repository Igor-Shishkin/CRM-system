package com.crm.system.controllers;

import com.crm.system.models.history.HistoryMessage;
import com.crm.system.playload.response.MessageResponse;
import com.crm.system.playload.response.TagForHistoryMessageDTO;
import com.crm.system.services.HistoryMessageService;
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
public class HistoryMessageController {
    private final HistoryMessageService historyMessageService;

    public HistoryMessageController(HistoryMessageService historyMessageService) {
        this.historyMessageService = historyMessageService;
    }

    @Operation(summary = "Get user's history", tags = {"user", "history"})
    @GetMapping("/get-history")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getUserHistory() throws UserPrincipalNotFoundException {
        Set<HistoryMessage> history = historyMessageService.getUserHistory();
        return ResponseEntity.ok(history);
    }

    @Operation(summary = "Get tags for history message", tags = {"history", "tags"})
    @GetMapping("tags")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getTagsForNewHistoryMessage() throws UserPrincipalNotFoundException {
        List<TagForHistoryMessageDTO> tags = historyMessageService.getListOfTags();
        return ResponseEntity.ok(tags);
    }

    @Operation(summary = "Save history message", tags = {"history", "new message"})
    @PostMapping()
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> saveMessage(@RequestBody HistoryMessage message) throws UserPrincipalNotFoundException {
        historyMessageService.saveMessage(message);
        return ResponseEntity.ok(new MessageResponse("Message is saved"));
    }

    @Operation(summary = "Delete history message", tags = {"history", "delete"})
    @DeleteMapping()
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteMessage(@RequestParam long messageId) throws UserPrincipalNotFoundException {
        historyMessageService.deleteMessage(messageId);
        return ResponseEntity.ok(new MessageResponse("Message is deleted"));
    }

    @Operation(summary = "Change important status of message", tags = {"history", "status"})
    @PutMapping("/change-important-status")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> changeImportantStatus(@RequestParam long messageId) throws UserPrincipalNotFoundException {
        historyMessageService.changeImportantStatus(messageId);
        return ResponseEntity.ok(new MessageResponse("Status is changed"));
    }

    @Operation(summary = "Change important status of message", tags = {"history", "status"})
    @PutMapping("/change-done-status")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> changeDoneStatus(@RequestParam long messageId) throws UserPrincipalNotFoundException {
        historyMessageService.changeDoneStatus(messageId);
        return ResponseEntity.ok(new MessageResponse("Status is changed"));
    }
}
