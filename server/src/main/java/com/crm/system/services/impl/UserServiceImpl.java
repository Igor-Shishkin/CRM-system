package com.crm.system.services.impl;

import com.crm.system.models.User;
import com.crm.system.playload.response.UserInfoDTO;
import com.crm.system.repository.ClientRepository;
import com.crm.system.repository.UserRepository;
import com.crm.system.security.services.UserDetailsImpl;
import com.crm.system.services.ClientService;
import com.crm.system.services.UserService;
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
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;

    public UserServiceImpl(UserRepository userRepository, ClientRepository clientRepository) {
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
    }
    public ResponseEntity<byte[]> getPhoto() throws UserPrincipalNotFoundException, FileNotFoundException {
        User user = getActiveUser();
        if (user.getPhotoOfUser() == null) {
            throw new FileNotFoundException("This user doesn't have a photo");
        }
        byte[] photoOfUser = user.getPhotoOfUser();
        HttpHeaders headers = getHeaders(photoOfUser);
        return new ResponseEntity<>(photoOfUser, headers, HttpStatus.OK);
    }
    public String uploadPhoto(MultipartFile file) throws IOException {
        User activeUser = getActiveUser();

        byte[] bytes = file.getBytes();

        activeUser.setPhotoOfUser(bytes);
        userRepository.save(activeUser);
        return "Photo is upload";
    }
    public List<UserInfoDTO> getInfoAllUsers() {

        List<User> allUsers = userRepository.findAll();
        long activeUserId = getActiveUserId();

        return allUsers.stream()
                .filter(user -> user.getUserId()!=activeUserId)
                .map(user -> {
                    List<String> roles = user.getRoles().stream()
                            .map(Object::toString)
                            .toList();
                    return new UserInfoDTO.Builder()
                            .withId(user.getUserId())
                            .withUsername(user.getUsername())
                            .withEmail(user.getEmail())
                            .withRoles(roles)
                            .withLidsNumber(clientRepository.getNumberOfClientsForUser(user.getUserId()))
                            .build();
                })
                .collect(Collectors.toList());
    }

    public long getActiveUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ((UserDetailsImpl) authentication.getPrincipal()).getId();
    }
    private HttpHeaders getHeaders(byte[] photoOfUser) {
        HttpHeaders headers = new HttpHeaders();
        String imageType = getImageType(photoOfUser);

        headers.setContentType((imageType == null) ? MediaType.IMAGE_JPEG : MediaType.parseMediaType(imageType));
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
    public User getActiveUser() throws UserPrincipalNotFoundException {
        return userRepository.findById(getActiveUserId())
                .orElseThrow(() -> new UserPrincipalNotFoundException("There isn't User with this ID"));
    }
}
