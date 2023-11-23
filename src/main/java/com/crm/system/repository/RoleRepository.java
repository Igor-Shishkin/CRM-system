package com.crm.system.repository;

import java.util.Optional;

import com.crm.system.models.security.ERole;
import com.crm.system.models.security.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}