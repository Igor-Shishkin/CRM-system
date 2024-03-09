package com.crm.system.services;

import com.crm.system.exception.RequestOptionalIsEmpty;
import com.crm.system.exception.SubjectNotBelongToActiveUser;
import com.crm.system.models.history.HistoryMessage;
import com.crm.system.models.User;
import com.crm.system.models.history.TagName;
import com.crm.system.playload.response.TagForHistoryMessageDTO;
import com.crm.system.repository.HistoryMessageRepository;
import com.crm.system.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class HistoryMessageService {

    private final HistoryMessageRepository historyMessageRepository;
    private final UserService userService;

    public HistoryMessageService(HistoryMessageRepository historyMessageRepository,
                                 UserRepository userRepository, UserService userService) {
        this.historyMessageRepository = historyMessageRepository;
        this.userService = userService;
    }

    public Set<HistoryMessage> getUserHistory() throws UserPrincipalNotFoundException {
        User activeUser = getActiveUser();
        return activeUser.getHistory();
    }

    public List<TagForHistoryMessageDTO> getListOfTags() throws UserPrincipalNotFoundException {
        User activeUser = getActiveUser();
        return activeUser.getClients().stream()
                .map(client -> new TagForHistoryMessageDTO(TagName.CLIENT,
                        client.getClientId(),
                        client.getFullName()))
                .collect(Collectors.toList());
    }

    public void saveMessage(HistoryMessage message) throws UserPrincipalNotFoundException {
        message.setMessageId(null);
        message.setDateOfCreation(LocalDateTime.now());
        message.setUser(userService.getActiveUser());

        historyMessageRepository.save(message);
    }

    public void automaticallyCreateMessage(HistoryMessage message) throws UserPrincipalNotFoundException {
        historyMessageRepository.save(message);
    }

    public void deleteMessage(long messageId) throws UserPrincipalNotFoundException {
        HistoryMessage messageForDeleting = getMessageById(messageId);
        historyMessageRepository.delete(messageForDeleting);
    }

    public void changeImportantStatus(long messageId) throws UserPrincipalNotFoundException {
        HistoryMessage message = getMessageById(messageId);
        message.setImportant(!message.isImportant());
        historyMessageRepository.save(message);
    }

    public void changeDoneStatus(long messageId) throws UserPrincipalNotFoundException {
        HistoryMessage message = getMessageById(messageId);
        message.setDone(!message.isDone());
        historyMessageRepository.save(message);
    }

    private HistoryMessage getMessageById(long messageId) throws UserPrincipalNotFoundException {
        long activeUserId = userService.getActiveUserId();
        return historyMessageRepository.getHistoryMessageByMessageIdAndUserId(messageId, activeUserId)
                .orElseThrow(() -> new RequestOptionalIsEmpty("You don't have message with this ID"));
    }

    private User getActiveUser() throws UserPrincipalNotFoundException {
        return userService.getActiveUser();
    }

    private boolean isMessageBelongsToActiveUser(User activeUser, long messageId) {
        return activeUser.getHistory().stream()
                .anyMatch(m -> m.getMessageId().equals(messageId));
    }
}
