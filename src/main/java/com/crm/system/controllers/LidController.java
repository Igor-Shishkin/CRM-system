package com.crm.system.controllers;

import com.crm.system.models.Lid;
import com.crm.system.models.User;
import com.crm.system.playload.request.LidRequest;
import com.crm.system.playload.response.MessageResponse;
import com.crm.system.repository.UserRepository;
import com.crm.system.service.ClientService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:8081", maxAge = 3600, allowCredentials="true")
@RestController
@RequestMapping("api/client")
public class LidController {
    private final ClientService clientService;
    private final UserRepository userRepository;

    public LidController(ClientService clientService, UserRepository userRepository) {
        this.clientService = clientService;
        this.userRepository = userRepository;
    }

    @PostMapping()
    public ResponseEntity<?> addNewClient(@Valid @RequestBody LidRequest clientRequest) {
        Optional<User> user = userRepository.findById(clientRequest.getUserId());

        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: User with this ID isn't found"));
        }
        Lid client = new Lid(clientRequest.getName(),
                clientRequest.getSurname(),
                clientRequest.getEmail(),
                clientRequest.getPhoneNumber(),
                false,
                user.get());
        clientService.addClient(client);
        return ResponseEntity.ok(new MessageResponse("New LID is save"));
    }

    @GetMapping()
    public ResponseEntity<?> getAllClientsForUser() {
        List<Lid> clients = clientService.getAllClients();
        return ResponseEntity.ok(clients);
    }



}
