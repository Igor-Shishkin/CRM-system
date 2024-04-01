package com.crm.system.controllers;

import com.crm.system.playload.request.SentEmailDTO;
import com.crm.system.playload.response.MessageResponse;
import com.crm.system.services.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.attribute.UserPrincipalNotFoundException;

@Slf4j
@Tag(name = "Email controller", description = "Email management APIs")
@CrossOrigin(origins = "http://localhost:8081", maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("/api/email")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/sent-email")
    @Operation(summary = "Sent email", tags = {"email", "sent"})
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_MODERATOR')")
    public ResponseEntity<MessageResponse> authenticateUser(@Valid @RequestBody SentEmailDTO sentEmailDTO)
                    throws UserPrincipalNotFoundException {
        emailService.sentEmail(sentEmailDTO);
        return ResponseEntity.ok(new MessageResponse("Payment is confirm"));
    }
}
