package com.crm.system.security.services;

import com.crm.system.exception.UserAlreadyExistsException;
import com.crm.system.models.User;
import com.crm.system.models.security.ERole;
import com.crm.system.models.security.Role;
import com.crm.system.playload.request.SignupRequest;
import com.crm.system.playload.response.UserInfoResponse;
import com.crm.system.repository.RoleRepository;
import com.crm.system.repository.UserRepository;
import com.crm.system.security.PasswordConfig;
import com.crm.system.security.jwt.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordConfig passwordConfig;
    private final RoleRepository roleRepository;
    private final JwtUtils jwtUtils;

    public UserDetailsServiceImpl(UserRepository userRepository, PasswordConfig passwordConfig,
                                  RoleRepository roleRepository, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordConfig = passwordConfig;
        this.roleRepository = roleRepository;
        this.jwtUtils = jwtUtils;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        return UserDetailsImpl.build(user);
    }

    public List<User> getAllUsers() {
        List<User> allUser = userRepository.findAll();
        return allUser;
    }

    public ResponseEntity<?> authenticateUser(Authentication authentication) {

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(new UserInfoResponse.Builder()
                        .withId(userDetails.getId())
                        .withEmail(userDetails.getEmail())
                        .withUsername(userDetails.getUsername())
                        .withRoles(roles)
                        .build());
    }

    public void registerUser(SignupRequest signUpRequest) throws UserAlreadyExistsException {
        for (String role : signUpRequest.getRole()) {
            System.out.println(role);
        }
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new UserAlreadyExistsException("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new UserAlreadyExistsException("Error: Email is already in use!");
        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                passwordConfig.passwordEncoder().encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = identifyRoles(strRoles);

        user.setRoles(roles);
        userRepository.save(user);
    }

    public ResponseCookie logoutUser() {
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        return cookie;
    }
    public String deleteUserById(long userId) throws UserPrincipalNotFoundException {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new UserPrincipalNotFoundException("User with this ID doesn't exist");
        }
        String username = user.get().getUsername();
        userRepository.deleteById(userId);
        return String.format("User '%s' is deleted", user.get().getUsername());
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
                    case "Moderator":
                        Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);

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
    private long getActiveUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((UserDetailsImpl) authentication.getPrincipal()).getId();
        return userId;
    }
}