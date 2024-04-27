package com.crm.system.services.impl;

import com.crm.system.models.User;
import com.crm.system.playload.response.UserInfoDTO;
import com.crm.system.repository.UserRepository;
import com.crm.system.security.services.UserDetailsImpl;
import com.crm.system.services.UserService;
import com.crm.system.services.utils.userServiceUtils.PhotoDetailsManager;
import com.crm.system.services.utils.userServiceUtils.UserInformationProcessor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PhotoDetailsManager photoDetailsManager;
    private final UserInformationProcessor userInformationProcessor;

    public UserServiceImpl(UserRepository userRepository,
                           PhotoDetailsManager photoDetailsManager,
                           UserInformationProcessor userInformationProcessor) {
        this.userRepository = userRepository;
        this.photoDetailsManager = photoDetailsManager;
        this.userInformationProcessor = userInformationProcessor;
    }
    public ResponseEntity<byte[]> getPhoto() throws UserPrincipalNotFoundException, FileNotFoundException {
        User user = getActiveUser();
        byte[] photoOfUser = photoDetailsManager.getPhotoOfUser(user);
        HttpHeaders headers = photoDetailsManager.getHeaders(photoOfUser);
        return new ResponseEntity<>(photoOfUser, headers, HttpStatus.OK);
    }
    public void uploadPhoto(MultipartFile file) throws IOException {
        byte[] bytes = file.getBytes();
        userRepository.updatePhotoForUserById(bytes, getActiveUserId());
    }
    public List<UserInfoDTO> getInfoAllUsers() {

        List<User> allUsers = userRepository.findAll();
        long activeUserId = getActiveUserId();

        return userInformationProcessor.getUsersInformationDTO(allUsers, activeUserId);
    }
    public long getActiveUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ((UserDetailsImpl) authentication.getPrincipal()).getId();
    }
    public User getActiveUser() throws UserPrincipalNotFoundException {
        return userRepository.findById(getActiveUserId())
                .orElseThrow(() -> new UserPrincipalNotFoundException("There isn't User with this ID"));
    }
}
