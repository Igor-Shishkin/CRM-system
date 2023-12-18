package com.crm.system.services;

import com.crm.system.exception.RequestOptionalIsEmpty;
import com.crm.system.models.Client;
import com.crm.system.models.history.HistoryMessage;
import com.crm.system.models.User;
import com.crm.system.models.history.TagName;
import com.crm.system.repository.ClientRepository;
import com.crm.system.repository.HistoryMessageRepository;
import com.crm.system.repository.UserRepository;
import com.crm.system.security.services.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class HistoryMessageService {

    private final HistoryMessageRepository historyMessageRepository;
    private final UserRepository userRepository;

    public HistoryMessageService(HistoryMessageRepository historyMessageRepository,
                                 UserRepository userRepository) {
        this.historyMessageRepository = historyMessageRepository;
        this.userRepository = userRepository;
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
        long activeUserId = getActiveUserId();
        Optional<User> optionalUser = userRepository.findById(activeUserId);
        Optional<Set<HistoryMessage>> optionalHistoryMessages = optionalUser.map(User::getHistory);
        if (optionalHistoryMessages.isPresent()) {
            return optionalHistoryMessages.get();
        } else {
            throw new RequestOptionalIsEmpty("History is not found");
        }
    }
    public Optional<User> getActiveUser(long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user;
    }

    private long getActiveUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((UserDetailsImpl) authentication.getPrincipal()).getId();
        return userId;
    }
}
