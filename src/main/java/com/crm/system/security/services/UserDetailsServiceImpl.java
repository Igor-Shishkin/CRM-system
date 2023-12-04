package com.crm.system.security.services;

import com.crm.system.exception.UserAlreadyExistsException;
import com.crm.system.exception.UserIdNotFoundException;
import com.crm.system.models.User;
import com.crm.system.models.security.ERole;
import com.crm.system.models.security.Role;
import com.crm.system.playload.request.LoginRequest;
import com.crm.system.playload.request.SignupRequest;
import com.crm.system.playload.response.UserInfoResponse;
import com.crm.system.repository.RoleRepository;
import com.crm.system.repository.UserRepository;
import com.crm.system.security.PasswordConfig;
import com.crm.system.security.jwt.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordConfig passwordConfig;
    private final RoleRepository roleRepository;
    private final JwtUtils jwtUtils;

    public UserDetailsServiceImpl(UserRepository userRepository, PasswordConfig passwordConfig,
                                  RoleRepository roleRepository, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordConfig = passwordConfig;
        this.roleRepository = roleRepository;
        this.jwtUtils = jwtUtils;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        return UserDetailsImpl.build(user);
    }

    public List<User> getAllUsers() {
        List<User> allUser = userRepository.findAll();
        return allUser;
    }

    public ResponseEntity<?> authenticateUser(Authentication authentication) {

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(new UserInfoResponse.Builder()
                        .withId(userDetails.getId())
                        .withEmail(userDetails.getEmail())
                        .withUsername(userDetails.getUsername())
                        .withRoles(roles)
                        .build());
    }

    public void registerUser(SignupRequest signUpRequest) throws UserAlreadyExistsException {
        for (String role : signUpRequest.getRole()) {
            System.out.println(role);
        }
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new UserAlreadyExistsException("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new UserAlreadyExistsException("Error: Email is already in use!");
        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                passwordConfig.passwordEncoder().encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = identifyRoles(strRoles);

        user.setRoles(roles);
        userRepository.save(user);
    }
    public List<UserInfoResponse> getInfoAllUsers() {
        List<User> allUsers = userRepository.findAll();
        List<UserInfoResponse> userInfoResponseList = new LinkedList<>();

        long activeUserId = getActiveUserId();

        for (User user : allUsers) {
            if (user.getId()!=activeUserId) {
                List<String> roles = user.getRoles().stream()
                        .map(Object::toString)
                        .toList();
                UserInfoResponse userInfoResponse = new UserInfoResponse.Builder()
                        .withId(user.getId())
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
    public ResponseCookie logoutUser() {
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        return cookie;
    }
    public String deleteUserById(long userId) throws UserIdNotFoundException {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new UserIdNotFoundException("User with this ID doesn't exist");
        }
        String username = user.get().getUsername();
        userRepository.deleteById(userId);
        return String.format("User '%s' is deleted", user.get().getUsername());
    }
    public String uploadPhoto(MultipartFile file) throws UserIdNotFoundException, IOException {
        Long userId = getActiveUserId();
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new UserIdNotFoundException("User with this ID doesn't exist");
        }
        byte[] bytes = file.getBytes();
        User user = optionalUser.get();
        user.setPhotoOfUser(bytes);
        userRepository.save(user);
        return "Photo is upload";
    }
    public ResponseEntity<?> getPhoto() throws UserIdNotFoundException, FileNotFoundException {
        Long userId = getActiveUserId();
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new UserIdNotFoundException("There isn't User with this ID");
        }
        User user = optionalUser.get();
        if (user.getPhotoOfUser() == null) {
            throw new FileNotFoundException("This user doesn't have a photo");
        }
        byte[] photoOfUser = user.getPhotoOfUser();
        HttpHeaders headers = getHeaders(photoOfUser);
        return new ResponseEntity<>(photoOfUser, headers, HttpStatus.OK);
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
                    case "Moderator":
                        Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);

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