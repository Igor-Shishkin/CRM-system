package com.crm.system.services.utils.userServiceUtils;

import com.crm.system.models.User;
import com.crm.system.playload.response.UserInfoDTO;
import com.crm.system.repository.ClientRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserInformationProcessor {
    private final ClientRepository clientRepository;

    public UserInformationProcessor(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public List<UserInfoDTO> getUsersInformationDTO(List<User> allUsers) {
        return allUsers.stream()
                .map(user -> {
                    List<String> roles = user.getRoles().stream()
                            .map(Object::toString)
                            .toList();
                    return new UserInfoDTO.Builder()
                            .withId(user.getUserId())
                            .withUsername(user.getUsername())
                            .withEmail(user.getEmail())
                            .withRoles(roles)
                            .withClientsNumber(clientRepository.getNumberOfClientsForUser(user.getUserId()))
                            .build();
                })
                .collect(Collectors.toList());
    }
}
