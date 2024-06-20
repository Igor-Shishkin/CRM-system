package com.crm.system.services.utils.logUtils.facadeForLogEntry;

import com.crm.system.services.utils.logUtils.decoratorsForLogEntry.LogEntryDecorator;
import org.springframework.stereotype.Component;

import java.nio.file.attribute.UserPrincipalNotFoundException;


@Component
public interface LogEntryFacade<T> {
    void createAndSaveMessage(T data,
                              EntryType entryType,
                              LogEntryDecorator... decorators) throws UserPrincipalNotFoundException;
}
