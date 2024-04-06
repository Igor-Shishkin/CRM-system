package com.crm.system.security.services;

import com.crm.system.exception.UserAlreadyExistsException;
import com.crm.system.models.User;
import com.crm.system.models.history.HistoryMessage;
import com.crm.system.models.history.TagName;
import com.crm.system.models.security.ERole;
import com.crm.system.models.security.Role;
import com.crm.system.playload.request.SignUpDTO;
import com.crm.system.playload.response.UserInfoDTO;
import com.crm.system.repository.RoleRepository;
import com.crm.system.repository.UserRepository;
import com.crm.system.security.PasswordConfig;
import com.crm.system.security.jwt.JwtUtils;
import com.crm.system.services.HistoryMessageService;
import com.crm.system.services.UserService;
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
    private final UserService userService;
    private final PasswordConfig passwordConfig;
    private final RoleRepository roleRepository;
    private final JwtUtils jwtUtils;
    private final HistoryMessageService historyMessageService;

    public UserDetailsServiceImpl(UserRepository userRepository,
                                  UserService userService,
                                  PasswordConfig passwordConfig,
                                  RoleRepository roleRepository,
                                  JwtUtils jwtUtils,
                                  HistoryMessageService historyMessageService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.passwordConfig = passwordConfig;
        this.roleRepository = roleRepository;
        this.jwtUtils = jwtUtils;
        this.historyMessageService = historyMessageService;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        return UserDetailsImpl.build(user);
    }

    public ResponseEntity<UserInfoDTO> authenticateUser(Authentication authentication) {

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(new UserInfoDTO.Builder()
                        .withId(userDetails.getId())
                        .withEmail(userDetails.getEmail())
                        .withUsername(userDetails.getUsername())
                        .withRoles(roles)
                        .build());
    }

    public void registerUser(SignUpDTO signUpRequest) throws UserAlreadyExistsException, UserPrincipalNotFoundException {
        if (doesUsernameExist(signUpRequest.getUsername()) && doesEmailExist(signUpRequest.getEmail())) {
            throw new UserAlreadyExistsException("Error: Username or email is already taken!");
        }

        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                passwordConfig.passwordEncoder().encode(signUpRequest.getPassword()));

        Set<Role> roles = identifyRoles(signUpRequest.getRole());
        user.setRoles(roles);

        User savedUser = userRepository.save(user);

        String messageText = String.format("User %s is added", user.getUsername());

        historyMessageService.automaticallyCreateMessage(new HistoryMessage.Builder()
                .withMessageText(messageText)
                .withIsDone(true)
                .withIsImportant(true)
                .withTagName(TagName.ADMINISTRATION)
                .withTagId(savedUser.getUserId())
                .withUser(userService.getActiveUser())
                .build());
    }

    public ResponseCookie logoutUser() {
        return jwtUtils.getCleanJwtCookie();
    }

    public String deleteUserById(long userId) throws UserPrincipalNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserPrincipalNotFoundException("User with this ID doesn't exist"));
        String username = user.getUsername();
        userRepository.deleteById(userId);
        return String.format("User '%s' is deleted", username);
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
    private boolean doesEmailExist(String email) {
        return userRepository.existsByEmail(email);
    }
    private boolean doesUsernameExist(String username) {
        return userRepository.existsByUsername(username);
    }
}