package com.crm.system.repository;

import com.crm.system.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

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
}
