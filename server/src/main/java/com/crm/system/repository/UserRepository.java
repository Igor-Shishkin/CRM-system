package com.crm.system.repository;

import java.util.List;
import java.util.Optional;

import com.crm.system.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    @Override
    Optional<User> findById(Long aLong);

    @Override
    List<User> findAll();
}