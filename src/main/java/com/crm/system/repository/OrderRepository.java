package com.crm.system.repository;

import com.crm.system.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Override
    <S extends Order> S save(S entity);

    @Override
    Optional<Order> findById(Long aLong);

    @Override
    List<Order> findAll();

    @Override
    void deleteById(Long aLong);
}
