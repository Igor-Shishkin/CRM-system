package com.crm.system.services;

import com.crm.system.models.logForUser.LogEntry;
import com.crm.system.playload.response.TagForHistoryMessageDTO;
import org.springframework.stereotype.Service;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.List;
import java.util.Set;

@Service
public interface LogEntryService {

    Set<LogEntry> getUserHistory() throws UserPrincipalNotFoundException;
    List<TagForHistoryMessageDTO> getListOfTags() throws UserPrincipalNotFoundException;
    void saveNewMessage(LogEntry message) throws UserPrincipalNotFoundException;
    void automaticallyCreateMessage(LogEntry message);
    void deleteMessage(long messageId);
    void changeImportantStatus(long messageId);
    void changeDoneStatus(long messageId);
}
