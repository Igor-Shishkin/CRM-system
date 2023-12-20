package com.crm.system.services;

import com.crm.system.exception.RequestOptionalIsEmpty;
import com.crm.system.exception.SubjectNotBelongToActiveUser;
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
import java.time.LocalDateTime;
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

    public void saveMessage(HistoryMessage message) throws UserPrincipalNotFoundException {
        Optional<User> optionalUser = userService.getActiveUser();
        if (optionalUser.isPresent()) {
            if (message.getMessageId() == -1) {
                message.setMessageId(null);
                message.setUser(optionalUser.get());
                message.setDateOfCreation(LocalDateTime.now());
                historyMessageRepository.save(message);
                return;
            } else {
                if (optionalUser.get().getHistory().stream()
                        .anyMatch(m -> m.getMessageId().equals(message.getMessageId()))) {
                    message.setUser(optionalUser.get());
                    historyMessageRepository.save(message);
                } else {
                    throw new SubjectNotBelongToActiveUser("It's not your history message");
                }
            }
        } else {
            throw new UserPrincipalNotFoundException("User not found exception");
        }
    }

    public void deleteMessage(long messageId) throws UserPrincipalNotFoundException {
        Optional<User> optionalUser = userService.getActiveUser();
        if (optionalUser.isPresent()) {

            if (optionalUser.get().getHistory().stream()
                    .anyMatch(m -> m.getMessageId().equals(messageId))) {
                historyMessageRepository.deleteById(messageId);
            } else {
                throw new SubjectNotBelongToActiveUser("It's not your history message");
            }

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
