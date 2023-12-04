package com.crm.system.controllers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import com.crm.system.exception.UserAlreadyExistsException;
import com.crm.system.exception.UserIdNotFoundException;
import com.crm.system.playload.request.LoginRequest;
import com.crm.system.playload.request.SignupRequest;
import com.crm.system.playload.response.MessageResponse;
import com.crm.system.playload.response.UserInfoResponse;
import com.crm.system.security.services.UserDetailsServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Tag(name = "Auth controller", description = "Auth management APIs")
@CrossOrigin(origins = "http://localhost:8081", maxAge = 3600, allowCredentials="true")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserDetailsServiceImpl userDetailsService, AuthenticationManager authenticationManager) {
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/signin")
    @Operation(summary = "Login in system", tags = { "auth", "login" })
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
            ResponseEntity<?> responseEntity = userDetailsService.authenticateUser(authentication);
            log.info("User {} is logged", Objects.requireNonNull(responseEntity.getBody()).toString());
            return responseEntity;
        } catch (AuthenticationCredentialsNotFoundException e) {
            log.error("Authorisation Error: " + e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse("Authorisation Error: " + e.getMessage()));
        }
    }

    @PostMapping("/signup")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "New user registration", tags = { "auth", "registration" })
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        try {
            userDetailsService.registerUser(signUpRequest);
            log.info("User registered successfully!");
            return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
        } catch (UserAlreadyExistsException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Registration error: " + e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse("Registration error: " + e.getMessage()));
        }
    }
    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser() {
        ResponseCookie cookie = userDetailsService.logoutUser();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new MessageResponse("You've been signed out!"));
    }
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUsers() {
        List<UserInfoResponse> userInfoResponseList = userDetailsService.getInfoAllUsers();
        return ResponseEntity.ok(userInfoResponseList);
    }
    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUserById(@RequestParam long userId) {
        String responseText;
        try {
            responseText = userDetailsService.deleteUserById(userId);
            return ResponseEntity.ok(new MessageResponse(responseText));
        } catch (UserIdNotFoundException | IllegalArgumentException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    @PostMapping("/photo")
    public ResponseEntity<?> uploadPhoto(@RequestParam("file") MultipartFile file) throws IOException, UserIdNotFoundException {
        try {
            String responseText = userDetailsService.uploadPhoto(file);
            return ResponseEntity.ok(new MessageResponse(responseText));
        }catch (UserIdNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        } catch (IOException e) {
            log.error("Error reading file" + e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse("Error reading file" + e.getMessage()));
        }
    }

    @GetMapping(value = "/photo", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<?> getPhoto() {
        try {
            return userDetailsService.getPhoto();
        } catch (FileNotFoundException | UserIdNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
}

