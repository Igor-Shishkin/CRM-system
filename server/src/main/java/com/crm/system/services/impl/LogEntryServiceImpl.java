package com.crm.system.services.impl;

import com.crm.system.exception.RequestOptionalIsEmpty;
import com.crm.system.models.User;
import com.crm.system.models.logForUser.LogEntry;
import com.crm.system.models.logForUser.TagName;
import com.crm.system.playload.response.TagForHistoryMessageDTO;
import com.crm.system.repository.LogEntryRepository;
import com.crm.system.services.LogEntryService;
import com.crm.system.services.UserService;
import org.springframework.stereotype.Service;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LogEntryServiceImpl implements LogEntryService {
    private final LogEntryRepository logEntryRepository;
    private final UserService userService;

    public LogEntryServiceImpl(LogEntryRepository logEntryRepository, UserService userService) {
        this.logEntryRepository = logEntryRepository;
        this.userService = userService;
    }

    public Set<LogEntry> getUserHistory() throws UserPrincipalNotFoundException {
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

    public void saveMessage(LogEntry message) throws UserPrincipalNotFoundException {
        message.setEntryId(null);
        message.setDateOfCreation(LocalDateTime.now());
        message.setUser(userService.getActiveUser());

        logEntryRepository.save(message);
    }

    public void automaticallyCreateMessage(LogEntry message) {
        logEntryRepository.save(message);
    }

    public void deleteMessage(long messageId)  {
        LogEntry messageForDeleting = getMessageById(messageId);
        logEntryRepository.delete(messageForDeleting);
    }

    public void changeImportantStatus(long messageId) {
        LogEntry message = getMessageById(messageId);
        message.setImportant(!message.isImportant());
        logEntryRepository.save(message);
    }

    public void changeDoneStatus(long messageId) {
        LogEntry message = getMessageById(messageId);
        message.setDone(!message.isDone());
        logEntryRepository.save(message);
    }

    private LogEntry getMessageById(long messageId) {
        long activeUserId = userService.getActiveUserId();
        return logEntryRepository.getHistoryMessageByMessageIdAndUserId(messageId, activeUserId)
                .orElseThrow(() -> new RequestOptionalIsEmpty("You don't have message with this ID"));
    }

    private User getActiveUser() throws UserPrincipalNotFoundException {
        return userService.getActiveUser();
    }
}
