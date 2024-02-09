package com.crm.system.repository;

import com.crm.system.models.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Override
    <S extends Order> S save(S entity);

    @Override
    Optional<Order> findById(Long aLong);

    @Override
    List<Order> findAll();

    @Override
    void deleteById(Long aLong);

    @Query("SELECT o FROM Order o WHERE o.orderId = :orderId AND o.client.user.userId = :userId")
    Optional<Order> getOrderByOrderIdAndUserId(Long orderId, Long userId);
}
