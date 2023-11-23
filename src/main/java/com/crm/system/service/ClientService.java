package com.crm.system.service;

import com.crm.system.models.Lid;
import com.crm.system.repository.LidRepository;
import com.crm.system.security.services.UserDetailsImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClientService {
    private final LidRepository clientRepository;

    public ClientService(LidRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public void addClient(Lid client) {
        clientRepository.save(client);
    }

    public List<Lid> getAllClients() {
        List<Lid> allClients = clientRepository.findAll();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((UserDetailsImpl) authentication.getPrincipal()).getId();

        System.out.println();

        return allClients.stream()
                .filter(client -> client.getUser().getId().equals(userId))
                .collect(Collectors.toList());
    }
}
