package com.crm.system.security.services.utils;

import com.crm.system.exception.UserAlreadyExistsException;
import com.crm.system.models.User;
import com.crm.system.models.security.ERole;
import com.crm.system.models.security.Role;
import com.crm.system.playload.request.SignUpDTO;
import com.crm.system.repository.RoleRepository;
import com.crm.system.repository.UserRepository;
import com.crm.system.security.PasswordConfig;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class NewUserProcessing {
    private final UserRepository userRepository;
    private final PasswordConfig passwordConfig;
    private final RoleRepository roleRepository;


    public NewUserProcessing(UserRepository userRepository, PasswordConfig passwordConfig, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordConfig = passwordConfig;
        this.roleRepository = roleRepository;
    }

    public User getNewUser(SignUpDTO signUpRequest) {
        checkWhetherUsernameOrEmailExist(signUpRequest);

        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                passwordConfig.passwordEncoder().encode(signUpRequest.getPassword()));

        Set<Role> roles = identifyRoles(signUpRequest.getRole());
        user.setRoles(roles);

        return user;
    }


    private void checkWhetherUsernameOrEmailExist(SignUpDTO signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail()) ||
                userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new UserAlreadyExistsException("Error: Username or email is already taken!");
        }
    }

    private Set<Role> identifyRoles(Set<String> strRoles) {
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "Admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);

                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }
        return roles;
    }
}
