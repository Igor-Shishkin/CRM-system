package com.crm.system.controllers;

import com.crm.system.models.Lid;
import com.crm.system.models.User;
import com.crm.system.playload.request.LidRequest;
import com.crm.system.playload.response.MessageResponse;
import com.crm.system.repository.UserRepository;
import com.crm.system.security.services.UserDetailsImpl;
import com.crm.system.services.LidService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:8081", maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("api/client")
public class LidController {
    private final LidService lidService;
    private final UserRepository userRepository;

    public LidController(LidService clientService, UserRepository userRepository) {
        this.lidService = clientService;
        this.userRepository = userRepository;
    }


    @PostMapping()
    public ResponseEntity<?> addNewClient(@Valid @RequestBody LidRequest clientRequest) {
        long userId = -1;

        try {
            userId = getActiveUserId();
        } catch (ClassCastException e) {
            return ResponseEntity.badRequest().build();
        }
        Optional<User> user = lidService.getActiveUser(userId);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User isn't defined");
        }
        Lid client = new Lid(clientRequest.getName(),
                clientRequest.getSurname(),
                clientRequest.getEmail(),
                clientRequest.getPhoneNumber(),
                false,
                user.get());
        lidService.addClient(client);
        return ResponseEntity.ok(new MessageResponse("New LID is save"));
    }

    @DeleteMapping()
    public ResponseEntity<?> deleteLid(long clientId) {
        return ResponseEntity.ok(new MessageResponse("Lid is deleted"));
    }

    @GetMapping()
    public ResponseEntity<?> getAllLidsForUser() {
        long userId = -1;
        try {
            userId = getActiveUserId();
        } catch (ClassCastException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User isn't defined");
        }
        List<Lid> clients = lidService.getAllClients(userId);
        return ResponseEntity.ok(clients);
    }


    private long getActiveUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((UserDetailsImpl) authentication.getPrincipal()).getId();
        return userId;
    }


}
