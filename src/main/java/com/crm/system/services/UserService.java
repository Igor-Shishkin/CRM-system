package com.crm.system.services;

import com.crm.system.exception.RequestOptionalIsEmpty;
import com.crm.system.models.history.HistoryMessage;
import com.crm.system.models.User;
import com.crm.system.playload.response.UserInfoResponse;
import com.crm.system.repository.UserRepository;
import com.crm.system.security.services.UserDetailsImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public ResponseEntity<?> getPhoto() throws UserPrincipalNotFoundException, FileNotFoundException {
        Long userId = getActiveUserId();
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new UserPrincipalNotFoundException("There isn't User with this ID");
        }
        User user = optionalUser.get();
        if (user.getPhotoOfUser() == null) {
            throw new FileNotFoundException("This user doesn't have a photo");
        }
        byte[] photoOfUser = user.getPhotoOfUser();
        HttpHeaders headers = getHeaders(photoOfUser);
        return new ResponseEntity<>(photoOfUser, headers, HttpStatus.OK);
    }
    public String uploadPhoto(MultipartFile file) throws UserPrincipalNotFoundException, IOException {
        Long userId = getActiveUserId();
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new UserPrincipalNotFoundException("User with this ID doesn't exist");
        }
        byte[] bytes = file.getBytes();
        User user = optionalUser.get();
        user.setPhotoOfUser(bytes);
        userRepository.save(user);
        return "Photo is upload";
    }
    public List<UserInfoResponse> getInfoAllUsers() {
        List<User> allUsers = userRepository.findAll();
        List<UserInfoResponse> userInfoResponseList = new LinkedList<>();

        long activeUserId = getActiveUserId();

        for (User user : allUsers) {
            if (user.getUserId()!=activeUserId) {
                List<String> roles = user.getRoles().stream()
                        .map(Object::toString)
                        .toList();
                UserInfoResponse userInfoResponse = new UserInfoResponse.Builder()
                        .withId(user.getUserId())
                        .withUsername(user.getUsername())
                        .withEmail(user.getEmail())
                        .withRoles(roles)
                        .withLidsNumber(user.getClients().size())
                        .build();
                userInfoResponseList.add(userInfoResponse);
            }
        }
        return userInfoResponseList;
    }



    private long getActiveUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((UserDetailsImpl) authentication.getPrincipal()).getId();
        return userId;
    }
    private HttpHeaders getHeaders(byte[] photoOfUser) {
        HttpHeaders headers = new HttpHeaders();
        String imageType = getImageType(photoOfUser);
        if (imageType == null) {
            headers.setContentType(MediaType.IMAGE_JPEG);
        } else {
            headers.setContentType(MediaType.parseMediaType(imageType));
        }
        headers.setContentLength(photoOfUser.length);
        return headers;
    }
    private String getImageType(byte[] imageData) {
        if (imageData.length >= 2) {
            if (imageData[0] == (byte) 0xFF && imageData[1] == (byte) 0xD8) {
                return "image/jpeg";
            } else if (imageData[0] == (byte) 0x89 && imageData[1] == (byte) 0x50) {
                return "image/png";
            }
        }
        return null;
    }
}
