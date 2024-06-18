package com.crm.system.controllers;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.*;

import com.crm.system.playload.request.LoginDTO;
import com.crm.system.playload.request.SignUpDTO;
import com.crm.system.playload.response.MessageResponse;
import com.crm.system.playload.response.UserInfoDTO;
import com.crm.system.security.services.UserDetailsServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "Auth controller", description = "Auth management APIs")
@CrossOrigin(origins = "http://localhost:8081", maxAge = 3600, allowCredentials = "true")
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
    @Operation(summary = "Login in system", description = "Allows you to register in the system")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "The client is returned from blacklist",
                    content = @Content(schema = @Schema(implementation = UserInfoDTO.class),
                            mediaType = "application/json")),
            @ApiResponse(
                    responseCode = "401", description = "Unauthorized - bad credentials",
                    content = @Content(schema = @Schema()))
    })
    public ResponseEntity<UserInfoDTO> authenticateUser(@Valid @RequestBody LoginDTO loginDTO) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword()));

        ResponseEntity<UserInfoDTO> responseEntity = userDetailsService.authenticateUser(authentication);
        log.info("User {} is logged", Objects.requireNonNull(responseEntity.getBody()));

        return responseEntity;
    }





    @PostMapping("/signup")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "New user registration", tags = {"auth", "registration"})
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201", description = "New user has been successfully added to the database",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class),
                            mediaType = "application/json")),
            @ApiResponse(
                    responseCode = "403", description = "ROLE_ADMIN is required",
                    content = @Content(schema = @Schema())),
            @ApiResponse(
                    responseCode = "401", description = "Full authentication is required to access this resource",
                    content = @Content(schema = @Schema())),
            @ApiResponse(
                    responseCode = "400", description = "Invalid request content.",
                    content = @Content(schema = @Schema())),
    })
    public ResponseEntity<MessageResponse> registerUser(@Valid @RequestBody SignUpDTO signUpRequest)
                            throws UserPrincipalNotFoundException {
        userDetailsService.registerUser(signUpRequest);
        log.info("User registered successfully!");
        return ResponseEntity.status(201).body(new MessageResponse("User registered successfully!"));
    }





    @PostMapping("/signout")
    @Operation(summary = "Logout", description = "Allows the user to Logout")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Allows you to register in the system",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class),
                            mediaType = "application/json"))
    })
    public ResponseEntity<MessageResponse> logoutUser() {
        ResponseCookie cookie = userDetailsService.logoutUser();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new MessageResponse("You've been signed out!"));
    }




    @DeleteMapping("/delete-user")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Delete user", tags = {"auth", "admin", "delete"})
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Delete user by ID",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class),
                            mediaType = "application/json")),
            @ApiResponse(
                    responseCode = "403", description = "ROLE_ADMIN is required",
                    content = @Content(schema = @Schema())),
            @ApiResponse(
                    responseCode = "401", description = "Full authentication is required to access this resource",
                    content = @Content(schema = @Schema())),
            @ApiResponse(
                    responseCode = "404", description = "User with this ID doesn't exist",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class))),
    })
    public ResponseEntity<MessageResponse> deleteUserById(@RequestParam long userId)
                            throws UserPrincipalNotFoundException {
        String responseText = userDetailsService.deleteUserById(userId);
        return ResponseEntity.ok(new MessageResponse(responseText));
    }






    @GetMapping("/check-authorization/user-role")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "check authorization", description = "Returns true if a user has ROLE_USER")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "User has ROLE_USER",
                    content = @Content(schema = @Schema(type = "boolean"),
                            mediaType = "application/json")),
            @ApiResponse(
                    responseCode = "403", description = "ROLE_USER is required",
                    content = @Content(schema = @Schema())),
            @ApiResponse(
                    responseCode = "401", description = "Full authentication is required to access this resource",
                    content = @Content(schema = @Schema())),
    })
    public ResponseEntity<Boolean> checkAuthorizationForUserRole() {
        return ResponseEntity.ok(true);
    }




    @GetMapping("/check-authorization/admin-role")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "check authorization", tags = {"auth", "check"})
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "User has ROLE_ADMIN",
                    content = @Content(schema = @Schema(type = "boolean"),
                            mediaType = "application/json")),
            @ApiResponse(
                    responseCode = "403", description = "ROLE_ADMIN is required",
                    content = @Content(schema = @Schema())),
            @ApiResponse(
                    responseCode = "401", description = "Full authentication is required to access this resource",
                    content = @Content(schema = @Schema())),
    })
    public ResponseEntity<Boolean> checkAuthorizationForAdminRole() {
        return ResponseEntity.ok(true);
    }




    @Operation(summary = "check authorization", tags = {"auth", "check"})
    @GetMapping("/check-authorization")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "User is authorized",
                    content = @Content(schema = @Schema(type = "boolean"),
                            mediaType = "application/json")),
            @ApiResponse(
                    responseCode = "401", description = "Full authentication is required to access this resource",
                    content = @Content(schema = @Schema()))
    })
    public ResponseEntity<Boolean> checkAuthorization() {
        return ResponseEntity.ok(true);
    }
}

