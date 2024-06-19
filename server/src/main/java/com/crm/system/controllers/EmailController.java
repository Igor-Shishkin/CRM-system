package com.crm.system.controllers;

import com.crm.system.playload.request.SentEmailDTO;
import com.crm.system.playload.response.MessageResponse;
import com.crm.system.services.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Sent email", description = "Sends an e-mail to the specified address. " +
            "The email address must be valid and not blank. " +
            "The subject and text of the email must not be blank.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Email successfully sent",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class),
                            mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid request content.",
                content = @Content(schema = @Schema())),
            @ApiResponse(
                    responseCode = "401", description = "Unauthorized - User not found",
                    content = @Content(schema = @Schema()))
    })
    public ResponseEntity<MessageResponse> sentEmail(@Valid @RequestBody SentEmailDTO sentEmailDTO)
            throws UserPrincipalNotFoundException {
        emailService.sentEmail(sentEmailDTO);
        return ResponseEntity.ok(new MessageResponse("Email sent successfully"));
    }
}
