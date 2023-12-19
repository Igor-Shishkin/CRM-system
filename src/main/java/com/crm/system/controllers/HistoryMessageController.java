package com.crm.system.controllers;

import com.crm.system.exception.RequestOptionalIsEmpty;
import com.crm.system.models.history.HistoryMessage;
import com.crm.system.playload.response.MessageResponse;
import com.crm.system.playload.response.TagForHistoryMessageResponse;
import com.crm.system.services.HistoryMessageService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.List;
import java.util.Set;

@Slf4j
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
        } catch (RequestOptionalIsEmpty e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    @Operation(summary = "Get tags for new history message", tags = {"histiry", "tags", "new message"})
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
}
