package com.crm.system.services;

import com.crm.system.models.Lid;
import com.crm.system.models.User;
import com.crm.system.repository.LidRepository;
import com.crm.system.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LidService {
    private final LidRepository lidRepository;
    private final UserRepository userRepository;

    public LidService(LidRepository clientRepository, UserRepository userRepository) {
        this.lidRepository = clientRepository;
        this.userRepository = userRepository;
    }

    public void addClient(Lid client) {
        lidRepository.save(client);
    }

    public List<Lid> getAllClients(long userId) {
        List<Lid> lids = lidRepository.findAll().stream()
                .filter(lid -> lid.getUser().getUserId().equals(userId))
                .collect(Collectors.toList());
        return lids;
    }

    public Optional<User> getActiveUser(long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user;
    }

}
