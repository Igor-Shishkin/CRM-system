package com.crm.system.services;

import com.crm.system.models.history.HistoryMessage;
import com.crm.system.playload.response.TagForHistoryMessageDTO;
import org.springframework.stereotype.Service;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.List;
import java.util.Set;

@Service
public interface HistoryMessageService {

    Set<HistoryMessage> getUserHistory() throws UserPrincipalNotFoundException;
    List<TagForHistoryMessageDTO> getListOfTags() throws UserPrincipalNotFoundException;
    void saveMessage(HistoryMessage message) throws UserPrincipalNotFoundException;
    void automaticallyCreateMessage(HistoryMessage message);
    void deleteMessage(long messageId);
    void changeImportantStatus(long messageId);
    void changeDoneStatus(long messageId);
}
