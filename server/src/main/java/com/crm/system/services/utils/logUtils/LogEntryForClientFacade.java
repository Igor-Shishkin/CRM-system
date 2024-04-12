package com.crm.system.services.utils.logUtils;

import com.crm.system.models.Client;
import com.crm.system.models.logForUser.LogEntry;
import com.crm.system.models.logForUser.TagName;
import com.crm.system.repository.LogEntryRepository;
import com.crm.system.services.LogEntryService;
import com.crm.system.services.utils.logUtils.decoratorsForLogEntry.LogEntryDecorator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LogEntryForClientFacade implements LogEntryFacade<Client>{
    private final LogEntryService logEntryService;
    private final LogEntryTextFactory<Client> logEntryTextFactory;

    public LogEntryForClientFacade(LogEntryService logEntryService,
                                   LogEntryTextForClientFactory logEntryTextFactory) {
        this.logEntryService = logEntryService;
        this.logEntryTextFactory = logEntryTextFactory;
    }

    @Override
    public void createAndSaveMessage(Client client, EntryType entryType, LogEntryDecorator... decorators) {

        assert (client != null);
        String text = logEntryTextFactory.generateText(client, entryType);

        LogEntry logEntry = new LogEntry.Builder()
                .withText(text)
                .withTagId(client.getClientId())
                .withTagName(TagName.CLIENT)
                .withUser(client.getUser())
                .build();

        for (LogEntryDecorator decorator : decorators) {
            logEntry = decorator.decorate(logEntry);
        }

        logEntryService.automaticallyCreateMessage(logEntry);
    }

//    private Client getClientFromData(Object data) {
//        if (!(data instanceof Client)) {
//            log.error("LofEntryFacade: Entity isn't instance of Client");
//            throw new IllegalArgumentException("Expected CLIENT data");
//        }
//        return (Client) data;
//    }
}
