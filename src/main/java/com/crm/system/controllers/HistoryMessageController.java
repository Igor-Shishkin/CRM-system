package com.crm.system.controllers;

import com.crm.system.exception.RequestOptionalIsEmpty;
import com.crm.system.exception.SubjectNotBelongToActiveUser;
import com.crm.system.models.history.HistoryMessage;
import com.crm.system.playload.response.MessageResponse;
import com.crm.system.playload.response.TagForHistoryMessageResponse;
import com.crm.system.services.HistoryMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_MODERATOR') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getUserHistory(){
        try {
            Set<HistoryMessage> history = historyMessageService.getUserHistory();
            return ResponseEntity.ok(history);
        } catch (RequestOptionalIsEmpty | UserPrincipalNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    @Operation(summary = "Get tags for new history message", tags = {"history", "tags", "new message"})
    @GetMapping("tags")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_MODERATOR') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getTagsForNewHistoryMessage() {
        try {
            List<TagForHistoryMessageResponse> tags = historyMessageService.getListOfTags();
            return ResponseEntity.ok(tags);
        } catch (UserPrincipalNotFoundException e) {
            log.error("History Service, sending tags. Error: " + e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("History Service, sending tags. Error: " + e.getMessage()));
        }
    }
    @Operation(summary = "Save new history message", tags = {"history", "new message"})
    @PostMapping()
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_MODERATOR') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> saveMessage(@RequestBody HistoryMessage message) {
        try {
            historyMessageService.saveMessage(message);
            return ResponseEntity.ok(new MessageResponse("Message is saved"));
        } catch (UserPrincipalNotFoundException | SubjectNotBelongToActiveUser e) {
            log.error("History Service, saving message. Error: " + e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("History Service, saving message. Error: " + e.getMessage()));
        }
    }
    @Operation(summary = "Save new history message", tags = {"history", "new message"})
    @DeleteMapping()
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_MODERATOR') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteMessage(@RequestParam long messageId) {
        try {
            historyMessageService.deleteMessage(messageId);
            return ResponseEntity.ok(new MessageResponse("Message is saved"));
        } catch (UserPrincipalNotFoundException | SubjectNotBelongToActiveUser e) {
            log.error("History Service, deleting message. Error: " + e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("History Service, deleting message. Error: " + e.getMessage()));
        }
    }
}
