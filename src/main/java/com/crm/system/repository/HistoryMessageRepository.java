package com.crm.system.repository;

import com.crm.system.models.history.HistoryMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HistoryMessageRepository extends JpaRepository<HistoryMessage, Long> {
    @Override
    <S extends HistoryMessage> S save(S entity);

    @Override
    Optional<HistoryMessage> findById(Long aLong);

    @Override
    void deleteById(Long aLong);

    @Override
    void delete(HistoryMessage entity);

    @Query("SELECT message FROM HistoryMessage message " +
            "WHERE message.messageId = :messageId AND message.user.userId = :userId")
    Optional<HistoryMessage> getHistoryMessageByMessageIdAndUserId(Long messageId, Long userId);
}
