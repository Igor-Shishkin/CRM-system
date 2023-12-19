package com.crm.system.services;

import com.crm.system.exception.RequestOptionalIsEmpty;
import com.crm.system.models.Client;
import com.crm.system.models.history.HistoryMessage;
import com.crm.system.models.User;
import com.crm.system.models.history.TagName;
import com.crm.system.playload.response.TagForHistoryMessageResponse;
import com.crm.system.repository.HistoryMessageRepository;
import com.crm.system.repository.UserRepository;
import com.crm.system.security.services.UserDetailsImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class HistoryMessageService {

    private final HistoryMessageRepository historyMessageRepository;
    private final UserService userService;

    public HistoryMessageService(HistoryMessageRepository historyMessageRepository,
                                 UserRepository userRepository, UserService userService) {
        this.historyMessageRepository = historyMessageRepository;
        this.userService = userService;
    }

    public void createHistoryMessageForClient(Client client, User user, String messageText) {
        HistoryMessage message = new HistoryMessage(messageText);
        message.setTagName(TagName.CLIENT);
        message.setTagId(client.getId());
        message.setUser(user);
        message.setImportant(false);
        message.setDone(true);
        historyMessageRepository.save(message);
    }
    public Set<HistoryMessage> getUserHistory() {
        Optional<User> optionalUser = userService.getActiveUser();
        Optional<Set<HistoryMessage>> optionalHistoryMessages = optionalUser.map(User::getHistory);
        if (optionalHistoryMessages.isPresent()) {
            return optionalHistoryMessages.get();
        } else {
            throw new RequestOptionalIsEmpty("History is not found");
        }
    }
    public List<TagForHistoryMessageResponse> getListOfTags() throws UserPrincipalNotFoundException {
        Optional<User> optionalUser = userService.getActiveUser();
        if (optionalUser.isPresent()) {
            Set<Client> clients = optionalUser.get().getClients();
            List<TagForHistoryMessageResponse> tags = new ArrayList<>(clients.size() * 2);
            for (Client client : clients) {
                TagForHistoryMessageResponse tag = new TagForHistoryMessageResponse();
                tag.setTagName(TagName.CLIENT);
                tag.setEntityName(client.getFullName());
                tag.setEntityId(client.getId());
                tags.add(tag);
            }
            return tags;
        } else {
            throw new UserPrincipalNotFoundException("User not found exception");
        }
    }
    private long getActiveUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((UserDetailsImpl) authentication.getPrincipal()).getId();
        return userId;
    }
}
