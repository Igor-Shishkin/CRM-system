package com.crm.system.repository;

import com.crm.system.models.Lid;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LidRepository extends JpaRepository<Lid, Long> {

    @Override
    <S extends Lid> S save(S entity);

    @Override
    Optional<Lid> findById(Long aLong);

    @Override
    List<Lid> findAll();

    @Override
    void deleteById(Long aLong);
}
