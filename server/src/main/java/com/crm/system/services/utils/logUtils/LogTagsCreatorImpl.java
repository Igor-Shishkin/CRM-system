package com.crm.system.services.utils.logUtils;

import com.crm.system.models.User;
import com.crm.system.models.logForUser.TagName;
import com.crm.system.models.security.Role;
import com.crm.system.playload.response.TagForUserLogDTO;
import com.crm.system.repository.ClientRepository;
import com.crm.system.repository.UserRepository;
import com.crm.system.services.ClientService;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class LogTagsCreatorImpl implements LogTagsCreator {
    private final UserRepository userRepository;
    private final ClientService clientService;

    public LogTagsCreatorImpl(UserRepository userRepository, ClientRepository clientRepository, ClientService clientService) {
        this.userRepository = userRepository;
        this.clientService = clientService;
    }

    public Set<TagForUserLogDTO> getTags(User activeUser) {
        Set<TagForUserLogDTO> tags = new HashSet<>();

        Set<Role> roles = activeUser.getRoles();

        for (Role role : roles) {
            switch (role.getName()) {
                case ROLE_USER -> tags.addAll(getTagsForUserRole(activeUser));
                case ROLE_ADMIN -> tags.addAll(getAdministrationTags());
            }
        }
        return tags;
    }

    private Set<TagForUserLogDTO> getAdministrationTags() {

        return userRepository.findAll().stream()
                .map(user -> new TagForUserLogDTO(TagName.ADMINISTRATION,
                        user.getUserId(),
                        user.getUsername()))
                .collect(Collectors.toSet());
    }

    private Set<TagForUserLogDTO> getTagsForUserRole(User activeUser) {

        return clientService.getAllClientsForUser(activeUser.getUserId())
                .stream()
                .map(client -> new TagForUserLogDTO(TagName.CLIENT,
                        client.getClientId(),
                        client.getFullName()))
                .collect(Collectors.toSet());
    }
}
