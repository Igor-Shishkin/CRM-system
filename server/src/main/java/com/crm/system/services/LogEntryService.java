package com.crm.system.services;

import com.crm.system.models.logForUser.LogEntry;
import com.crm.system.playload.response.TagForUserLogDTO;
import org.springframework.stereotype.Service;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.Set;

@Service
public interface LogEntryService {

    Set<LogEntry> getUserLog() throws UserPrincipalNotFoundException;
    Set<TagForUserLogDTO> getSetOfTags() throws UserPrincipalNotFoundException;
    void saveNewMessage(LogEntry message) throws UserPrincipalNotFoundException;
    void automaticallyCreateMessage(LogEntry message);
    void deleteMessage(long messageId);
    void changeImportantStatus(long messageId);
    void changeDoneStatus(long messageId);
}
