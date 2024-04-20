package com.crm.system.repository;

import com.crm.system.models.logForUser.LogEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface LogEntryRepository extends JpaRepository<LogEntry, Long> {

    @Override
    Optional<LogEntry> findById(Long aLong);

    @Override
    void deleteById(Long aLong);

    @Override
    void delete(LogEntry entity);

    @Query("SELECT message FROM LogEntry message " +
            "WHERE message.entryId = :messageId AND message.user.userId = :userId")
    Optional<LogEntry> getHistoryMessageByMessageIdAndUserId(Long messageId, Long userId);

    @Query("SELECT entry FROM LogEntry AS entry WHERE entry.user.userId = :userId")
    Set<LogEntry> getLogForUser(long userId);
}
