package com.crm.system.controllers;

import com.crm.system.models.User;
import com.crm.system.repository.UserRepository;
import com.crm.system.utils.mockAnnotations.WithMockCustomUserByUsername;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Sql({"/schema.sql", "/data.sql"})
class UserControllerTest {


    @Autowired
    MockMvc mockMvc;
    @Autowired
    UserRepository userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Container
    static MySQLContainer mySQLContainer = new MySQLContainer("mysql:8.0")
            .withUsername("root")
            .withPassword("00000000A!")
            .withDatabaseName("marton_db");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
    }

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(ClientController.class)
                .build();
    }

    @BeforeAll
    static void beforeAll() {  mySQLContainer.start(); }
    @AfterAll
    static void afterAll() {
        mySQLContainer.stop();
    }


    @Test
    @Transactional
    @WithMockCustomUserByUsername(username = "user-admin")
    void upload_photo_for_user_success() throws Exception {

        MockMultipartFile file = new MockMultipartFile("file",
                "test.jpg",
                "image/jpeg",
                "test".getBytes());

        mockMvc.perform(MockMvcRequestBuilders
                .multipart("/api/user/photo")
                .file(file)
                .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Photo is upload"));

        Optional<User> optionalUser = userRepository.findById(1L);
        assertThat(optionalUser).isNotEmpty();
        assertThat(optionalUser.get().getPhotoOfUser()).isNotNull();
        assertThat(optionalUser.get().getPhotoOfUser()).isEqualTo("test".getBytes());
    }
    @Test
    @WithMockCustomUserByUsername(username = "user-admin")
    void upload_photo_for_user_without_file() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .multipart("/api/user/photo")
                        .accept("application/json"))
                .andExpect(status().isBadRequest());
    }
    @Test
    void upload_photo_for_user_without_authorization() throws Exception {

        MockMultipartFile file = new MockMultipartFile("file",
                "test.jpg",
                "image/jpeg",
                "test".getBytes());

        mockMvc.perform(MockMvcRequestBuilders
                        .multipart("/api/user/photo")
                        .file(file)
                        .accept("application/json"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    @WithMockCustomUserByUsername(username = "user-admin")
    void get_photo_for_user_success() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file",
                "test.jpg",
                "image/jpeg",
                "test".getBytes());
        Optional<User> optionalUser = userRepository.findById(1L);
        User user = optionalUser.get();
        user.setPhotoOfUser(file.getBytes());
        userRepository.save(user);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/user/photo")
                        .accept(MediaType.IMAGE_JPEG))
                .andExpect(status().isOk())
                .andExpect(header().exists("Content-Type"))
                .andExpect(header().string("Content-Type", MediaType.IMAGE_JPEG_VALUE))
                .andExpect(header().exists("Content-Length"))
                .andReturn();

        byte[] responseContent = result.getResponse().getContentAsByteArray();
        assertThat(file.getBytes()).isEqualTo(responseContent);
    }

    private String writeObjectToJsonFormat(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }


}