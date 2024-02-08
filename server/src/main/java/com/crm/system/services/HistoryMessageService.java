package com.crm.system.services;

import com.crm.system.exception.RequestOptionalIsEmpty;
import com.crm.system.exception.SubjectNotBelongToActiveUser;
import com.crm.system.models.Client;
import com.crm.system.models.history.HistoryMessage;
import com.crm.system.models.User;
import com.crm.system.models.history.TagName;
import com.crm.system.playload.response.TagForHistoryMessageDTO;
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

    public void createHistoryMessageForClient(Client client, String messageText) {
        HistoryMessage message = new HistoryMessage(messageText);
        message.setTagName(TagName.CLIENT);
        message.setTagId(client.getClientId());
        message.setUser(client.getUser());
        message.setImportant(false);
        message.setDone(true);
        message.setDateOfCreation(LocalDateTime.now());
        historyMessageRepository.save(message);
    }
    public void createImportantHistoryMessageForClient(Client client, String messageText) {
        HistoryMessage message = new HistoryMessage(messageText);
        message.setTagName(TagName.CLIENT);
        message.setTagId(client.getClientId());
        message.setUser(client.getUser());
        message.setImportant(true);
        message.setDone(true);
        message.setDateOfCreation(LocalDateTime.now());
        historyMessageRepository.save(message);
    }
    public void createHistoryMessageWithTagInfo(String messageText, String note, TagName tagName, long tagId,
                      boolean isImportant, boolean isDone) throws UserPrincipalNotFoundException {
        HistoryMessage message = new HistoryMessage(messageText);
        message.setTagName(tagName);
        message.setTagId(tagId);
        message.setUser(getActiveUser());
        message.setImportant(isImportant);
        message.setDone(isDone);
        message.setNote(note);
        message.setDateOfCreation(LocalDateTime.now());
        historyMessageRepository.save(message);
    }

    public Set<HistoryMessage> getUserHistory() throws UserPrincipalNotFoundException {
        User activeUser = getActiveUser();
        Set<HistoryMessage> historyMessages = activeUser.getHistory();
        return historyMessages;
    }

    public List<TagForHistoryMessageDTO> getListOfTags() throws UserPrincipalNotFoundException {
        User activeUser = getActiveUser();

            Set<Client> clients = activeUser.getClients();
            List<TagForHistoryMessageDTO> tags = new ArrayList<>(clients.size() * 2);
            for (Client client : clients) {
                TagForHistoryMessageDTO tag = new TagForHistoryMessageDTO();
                tag.setTagName(TagName.CLIENT);
                tag.setEntityName(client.getFullName());
                tag.setEntityId(client.getClientId());
                tags.add(tag);
            }
            return tags;

    }

    public void saveMessage(HistoryMessage message) throws UserPrincipalNotFoundException {
        User activeUser = getActiveUser();

        if (message.getMessageId().equals(-1L)) {
            message.setMessageId(null);
            message.setUser(activeUser);
            message.setDateOfCreation(LocalDateTime.now());
            historyMessageRepository.save(message);
        }
            else {
            if (isMessageBelongsToActiveUser(activeUser, message.getMessageId())) {
                message.setUser(activeUser);
                historyMessageRepository.save(message);
            } else {
                throw new SubjectNotBelongToActiveUser("It's not your history message");
            }
        }
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
