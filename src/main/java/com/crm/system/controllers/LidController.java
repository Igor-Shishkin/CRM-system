package com.crm.system.controllers;

import com.crm.system.exception.UserIdNotFoundException;
import com.crm.system.models.Lid;
import com.crm.system.models.User;
import com.crm.system.playload.request.LidRequest;
import com.crm.system.playload.response.ClientInfoResponse;
import com.crm.system.playload.response.LidInfoResponse;
import com.crm.system.playload.response.MessageResponse;
import com.crm.system.repository.UserRepository;
import com.crm.system.services.LidService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.List;
import java.util.Optional;

@Slf4j
@CrossOrigin(origins = "http://localhost:8081", maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("api/client")
public class LidController {
    private final LidService lidService;

    public LidController(LidService clientService, UserRepository userRepository) {
        this.lidService = clientService;
    }


    @PostMapping()
    public ResponseEntity<?> addNewLid(@Valid @RequestBody LidRequest lidRequest) {
        try {
            lidService.addNewLid(lidRequest);
            return ResponseEntity.ok(new MessageResponse("New LID is save"));
        } catch (UserPrincipalNotFoundException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("User isn't defined. " + e.getMessage()));
        }

    }

    @DeleteMapping()
    public ResponseEntity<?> deleteLid(long lidId) {
        try {
            lidService.deleteLidById(lidId);
            log.error(String.format("Lid with %d id is deleted", lidId));
            return ResponseEntity.ok(new MessageResponse(String.format("Lid with %d id is deleted", lidId)));
        } catch (IllegalArgumentException e) {
            log.error("Delete user error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Delete user error: " + e.getMessage()));
        }
    }

    @GetMapping("/clients")
    public ResponseEntity<?> getAllClientsForUser() {
        try {
            List<ClientInfoResponse> clients = lidService.getAllClients();
            return ResponseEntity.ok(clients);
        } catch (UserPrincipalNotFoundException e) {
            log.error("Authorisation Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("User isn't defined: " + e.getMessage()));
        }
    }
    @GetMapping("/lids")
    public ResponseEntity<?> getAllLidsForUser() {
        try {
            List<LidInfoResponse> lids = lidService.getAllLids();
            return ResponseEntity.ok(lids);
        } catch (UserPrincipalNotFoundException e) {
            log.error("Authorisation Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("User isn't defined. " + e.getMessage()));
        }
    }



}
