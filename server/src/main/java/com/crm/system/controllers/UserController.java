package com.crm.system.controllers;

import com.crm.system.playload.response.MessageResponse;
import com.crm.system.playload.response.UserInfoDTO;
import com.crm.system.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
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

@RequestMapping("/api/user")
@RestController
@Tag(name = "User controller", description = "Order management APIs")
@CrossOrigin(origins = "http://localhost:8081", maxAge = 3600, allowCredentials = "true")
@Slf4j
public class UserController {


    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/users")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(
            summary = "Get all users info",
            description = """
                    This endpoint allows administrators to retrieve information for all users in the system.
                    Requires ROLE_ADMIN authorization.
                            
                    Returns a list of UserInfoDTO objects, each containing:
                    - `id` (long): The user's ID.
                    - `username` (String): The user's username.
                    - `email` (String): The user's email address.
                    - `roles` (List<String>): A list of roles assigned to the user.
                    - `clientsNumber` (int): The number of clients associated with the user.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved user information",
                            content = @Content(
                                    schema = @Schema(implementation = UserInfoDTO.class),
                                    mediaType = "application/json"
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403", description = "ROLE_ADMIN is required",
                            content = @Content(schema = @Schema())),
                    @ApiResponse(
                            responseCode = "401", content = @Content(schema = @Schema()),
                            description = "Unauthorized - you must log in with appropriate role")
            }
    )

    public ResponseEntity<List<UserInfoDTO>> getInfoAllUsers() {
        List<UserInfoDTO> userInfoDTOList = userService.getInfoAllUsers();
        return ResponseEntity.ok(userInfoDTOList);
    }




    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @PostMapping("/photo")
    @Operation(
            summary = "Add photo to user",
            description = """
        This endpoint allows users with the roles 'ROLE_USER' or 'ROLE_ADMIN' to upload a photo and add it to their profile.
        The uploaded photo is saved as part of the user's profile information.

        Requires ROLE_USER or ROLE_ADMIN authorization.

        Expects a file as a request parameter to be uploaded.

        Throws a UserPrincipalNotFoundException if:
        - The active user cannot be found.

        Throws an IOException if:
        - There is an error processing the file.

        If successful in uploading the photo:
        - Converts the uploaded file to a byte array.
        - Saves the byte array as the user's photo.
        - Updates the user profile in the database.
        - Returns a message indicating successful photo upload.

        The request parameter should include:
        - `file` (MultipartFile): The photo file to be uploaded.
        """,
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Photo successfully uploaded",
                            content = @Content(schema = @Schema(implementation = MessageResponse.class),
                                    mediaType = "application/json")),
                    @ApiResponse(
                            responseCode = "401", content = @Content(schema = @Schema()),
                            description = "Unauthorized - you must log in with appropriate role"),
                    @ApiResponse(
                            responseCode = "500", content = @Content(schema = @Schema()),
                            description = "Internal Server Error - if there is an issue with processing the file")
            }
    )

    public ResponseEntity<MessageResponse> uploadPhoto(@RequestParam("file") MultipartFile file) throws IOException {
        userService.uploadPhoto(file);
        return ResponseEntity.ok(new MessageResponse("Photo is upload"));
    }




    @GetMapping(value = "/photo", produces = MediaType.IMAGE_JPEG_VALUE)
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Photo request",
            description = """
        Allows users with the roles 'ROLE_USER' or 'ROLE_ADMIN' to retrieve their profile photo.

        Requires ROLE_USER or ROLE_ADMIN authorization.

        Throws a UserPrincipalNotFoundException if:
        - The active user cannot be found.

        Throws a UserDoesNotHavePhotoException if:
        - The user does not have a stored photo.

        Returns the user's photo as a JPEG image in the response body.

        No request parameters are required for this endpoint.
        """,
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Successfully retrieved user's photo",
                            content = @Content(mediaType = MediaType.IMAGE_JPEG_VALUE,
                                    schema = @Schema(type = "string", format = "binary")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401", description = "Unauthorized - you must log in with appropriate role",
                            content = @Content(schema = @Schema())),
                    @ApiResponse(
                            responseCode = "404", description = "Not Found - the user does not have a stored photo",
                            content = @Content(schema = @Schema(implementation = MessageResponse.class)))
            }
    )
    public ResponseEntity<byte[]> getPhoto() throws UserPrincipalNotFoundException, FileNotFoundException {
        return userService.getPhoto();
    }

}