package com.crm.system.services;

import com.crm.system.models.Client;
import com.crm.system.models.HistoryMessage;
import com.crm.system.models.User;
import com.crm.system.repository.HistoryMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HistoryMessageService {

    private final HistoryMessageRepository historyMessageRepository;

    public void createHistoryMessageAboutNewLead(Client lead, User user) {
        String messageText = String.format("Lead %s is created", lead.getFullName());
        HistoryMessage message = new HistoryMessage(messageText);
        message.setClient(lead);
        message.setUser(user);
        message.setImportant(false);
        message.setDone(true);
        historyMessageRepository.save(message);
    }
}
