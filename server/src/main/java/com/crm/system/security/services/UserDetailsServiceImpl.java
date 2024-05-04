package com.crm.system.security.services;

import com.crm.system.exception.UserAlreadyExistsException;
import com.crm.system.models.User;
import com.crm.system.playload.request.SignUpDTO;
import com.crm.system.playload.response.UserInfoDTO;
import com.crm.system.repository.UserRepository;
import com.crm.system.security.jwt.JwtUtils;
import com.crm.system.security.services.utils.NewUserProcessing;
import com.crm.system.services.UserService;
import com.crm.system.services.utils.logUtils.decoratorsForLogEntry.MarkAsDoneDecorator;
import com.crm.system.services.utils.logUtils.decoratorsForLogEntry.MarkAsImportantDecorator;
import com.crm.system.services.utils.logUtils.facadeForLogEntry.LogEntryForUserFacade;
import com.crm.system.services.utils.logUtils.facadeForLogEntry.EntryType;
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
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final NewUserProcessing newUserProcessing;
    private final JwtUtils jwtUtils;
    private final LogEntryForUserFacade logEntryForUserFacade;
    private final UserService userService;

    public UserDetailsServiceImpl(UserRepository userRepository,
                                  NewUserProcessing newUserProcessing,
                                  JwtUtils jwtUtils,
                                  LogEntryForUserFacade logEntryForUserFacade, UserService userService) {
        this.userRepository = userRepository;
        this.newUserProcessing = newUserProcessing;
        this.logEntryForUserFacade = logEntryForUserFacade;
        this.jwtUtils = jwtUtils;
        this.userService = userService;
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

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtUtils.generateJwtCookie(userDetails).toString())
                .body(new UserInfoDTO.Builder()
                        .withId(userDetails.getId())
                        .withEmail(userDetails.getEmail())
                        .withUsername(userDetails.getUsername())
                        .withRoles(roles)
                        .build());
    }

    public void registerUser(SignUpDTO signUpRequest) throws UserAlreadyExistsException, UserPrincipalNotFoundException {


        User newUser = newUserProcessing.getNewUser(signUpRequest);
        User savedUser = userRepository.save(newUser);

        logEntryForUserFacade.createAndSaveMessage(savedUser,
                EntryType.SAVE_NEW_USER,
                userService.getActiveUser(),
                new MarkAsDoneDecorator(), new MarkAsImportantDecorator());
    }

    public ResponseCookie logoutUser() {
        return jwtUtils.getCleanJwtCookie();
    }

    public String deleteUserById(long userId) throws UserPrincipalNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserPrincipalNotFoundException("User with this ID doesn't exist"));
        userRepository.deleteById(userId);

        logEntryForUserFacade.createAndSaveMessage(user,
                EntryType.DELETE_USER,
                userService.getActiveUser(),
                new MarkAsDoneDecorator(), new MarkAsImportantDecorator());

        return String.format("User with ID=%d is deleted", userId);
    }
}