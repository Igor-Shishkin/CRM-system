package com.crm.system.repository;

import com.crm.system.models.Client;
import com.crm.system.models.ClientStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ClientRepository extends JpaRepository<Client, Long> {

    @Override
    <S extends Client> S save(S entity);

    @Override
    Optional<Client> findById(Long aLong);

    @Override
    List<Client> findAll();

    @Override
    void deleteById(Long aLong);

    boolean existsByEmail(String email);

    @Query("SELECT c FROM Client AS c WHERE c.user.userId = :userId AND c.clientId = :clientId")
    Optional<Client> findClientByClientIdAndUserId(Long userId, Long clientId);

    @Query("SELECT c FROM Client AS c WHERE c.user.userId = :userId AND c.status = 'CLIENT'")
    Set<Client> getClientsWithClientStatusForUser(Long userId);

    @Query("SELECT c FROM Client AS c WHERE c.user.userId = :userId AND c.status = 'LEAD'")
    Set<Client> getClientsWithLeadStatusForUser(long userId);

    @Query("SELECT c FROM Client AS c WHERE c.user.userId = :userId AND c.status = 'BLACKLIST'")
    Set<Client> getClientsWithBlackListStatusForUser(long userId);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
            "FROM Client c WHERE c.user.userId = :activeUserId AND c.clientId = :clientId")
    boolean checkWhetherClientBelongToActiveUser(long activeUserId, long clientId);

    @Transactional
    @Modifying
    @Query("UPDATE Client c " +
            "SET c.status = :status, c.dateOfLastChange = :dateOfLastChange " +
            "WHERE c.clientId = :clientId")
    void updateClientStatusAndDateOfLastChange(@Param("clientId") long clientId,
                                               @Param("status") ClientStatus status,
                                               @Param("dateOfLastChange") LocalDateTime dateOfLastChange);

    @Query("SELECT c FROM Client AS c WHERE c.user.userId = :userId")
    Set<Client> getAllClientsForUser(Long userId);

    @Query("SELECT COUNT(c) FROM Client AS c WHERE c.user.userId = :userId")
    int getNumberOfClientsForUser(long userId);
}
