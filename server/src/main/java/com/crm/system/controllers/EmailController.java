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
    @Operation(summary = "Send an email to a specified recipient",
            description = """
                    Allows the active user to send an email to a specified recipient. \
                    Requires ROLE_USER or ROLE_ADMIN authorization.

                    Expects a SentEmailDTO object containing the details of the email to be sent. \
                    The SentEmailDTO object should include:
                    - `email` (String, NotEmpty, verified as Email): The recipient's email address.
                    - `subjectOfMail` (String, NotEmpty): The subject of the email.
                    - `textOfEmail` (String, NotEmpty): The body text of the email.

                    If successful:
                    - The email is sent to the recipient
                    - Logs an entry indicating sending email.
                    - A confirmation message is returned.""",
            responses = {
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
