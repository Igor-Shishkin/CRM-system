package com.crm.system.services;

import com.crm.system.models.User;
import com.crm.system.playload.response.UserInfoDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.*;

@Service
public interface UserService {
    ResponseEntity<byte[]> getPhoto() throws UserPrincipalNotFoundException, FileNotFoundException;
    void uploadPhoto(MultipartFile file) throws IOException;
    List<UserInfoDTO> getInfoAllUsers();
    long getActiveUserId();
    User getActiveUser() throws UserPrincipalNotFoundException;

}
