package com.crm.system.utils;

import com.crm.system.models.User;
import com.crm.system.models.security.ERole;
import com.crm.system.models.security.Role;
import com.crm.system.repository.RoleRepository;
import com.crm.system.repository.UserRepository;
import com.crm.system.security.PasswordConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
public class AdminUserInitializer implements ApplicationRunner {

    @Value("${app.crm.admin.name}")
    private String userName;
    @Value("${app.crm.admin.password}")
    private String userPassword;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordConfig passwordConfig;

    public AdminUserInitializer(UserRepository userRepository, RoleRepository roleRepository, PasswordConfig passwordConfig) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordConfig = passwordConfig;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        try {
            if (!userRepository.existsByUsername(userName)) {
                User adminUser = new User();
                adminUser.setUsername(userName);
                adminUser.setPassword(passwordConfig.passwordEncoder().encode(userPassword));
                adminUser.setEmail("marton@gmail.com");
                roleRepository.findByName(ERole.ROLE_ADMIN).ifPresent(role -> adminUser.setRoles(Set.of(role)));

                userRepository.save(adminUser);
            }
        } catch (Exception e) {
            log.error(e.toString() + e.fillInStackTrace());
        }
    }
}