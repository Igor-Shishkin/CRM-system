package com.crm.system.controllers;

import com.crm.system.exception.RequestOptionalIsEmpty;
import com.crm.system.models.HistoryMessage;
import com.crm.system.playload.response.MessageResponse;
import com.crm.system.playload.response.UserInfoResponse;
import com.crm.system.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.List;
import java.util.Set;

@Slf4j
@CrossOrigin(origins = "http://localhost:8081", maxAge = 3600, allowCredentials="true")
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Get all users", tags = { "auth", "admin", "users" })
    @GetMapping("/users")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAllUsers() {
        List<UserInfoResponse> userInfoResponseList = userService.getInfoAllUsers();
        return ResponseEntity.ok(userInfoResponseList);
    }
    @Operation(summary = "Add photo to user", tags = {"user", "photo", "avatar"})
    @PostMapping("/photo")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_MODERATOR') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> uploadPhoto(@RequestParam("file") MultipartFile file) throws IOException, UserPrincipalNotFoundException {
        try {
            String responseText = userService.uploadPhoto(file);
            return ResponseEntity.ok(new MessageResponse(responseText));
        }catch (UserPrincipalNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        } catch (IOException e) {
            log.error("Error reading file" + e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse("Error reading file" + e.getMessage()));
        }
    }
    @Operation(summary = "Get user's history", tags = {"user", "history"})
    @GetMapping("/history")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_MODERATOR') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getUserHistory(){
        try {
            Set<HistoryMessage> history = userService.getUserHistory();
            return ResponseEntity.ok(history);
        } catch (RequestOptionalIsEmpty e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    @Operation(summary = "Photo request", tags = {"user", "photo", "avatar"})
    @GetMapping(value = "/photo", produces = MediaType.IMAGE_JPEG_VALUE)
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_MODERATOR') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getPhoto() {
        try {
            return userService.getPhoto();
        } catch (FileNotFoundException | UserPrincipalNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
}