package com.crm.system.controllers;

import com.crm.system.models.User;
import com.crm.system.repository.ClientRepository;
import com.crm.system.repository.LogEntryRepository;
import com.crm.system.repository.UserRepository;
import com.crm.system.services.ClientService;
import com.crm.system.services.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.nio.file.attribute.UserPrincipalNotFoundException;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Sql({"/schema.sql", "/data.sql"})
class LogEntryControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ClientService clientService;
    @Autowired
    ClientRepository clientRepository;
    @MockBean
    private UserService userService;
    @Autowired
    LogEntryRepository logEntryRepository;
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
                .standaloneSetup(LogEntryController.class)
                .build();
    }

    @BeforeEach
    public void initialize() throws UserPrincipalNotFoundException {
        when(userService.getActiveUserId()).thenReturn(1L);
        User user = userRepository.findById(1L)
                .orElseThrow(() -> new UserPrincipalNotFoundException("There isn't User with this ID"));
        when(userService.getActiveUser()).thenReturn(user);
    }

    @BeforeAll
    static void beforeAll() {  mySQLContainer.start(); }
    @AfterAll
    static void afterAll() {
        mySQLContainer.stop();
    }


    @Transactional
    @Test
    @WithMockUser(roles = "USER")
    public void get_log_with_user_role_success() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/log/get-user-log")
                .contentType("application/json")
                .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(4))
                .andExpect(jsonPath("$[*].text", containsInAnyOrder(
                        "Installation of kitchen furniture completed",
                        "Bedroom layout design requested",
                        "Create new user",
                        "Bedroom furniture installation started"
                )))
                .andExpect(jsonPath("$[*].dateOfCreation", containsInAnyOrder(
                        "2023-12-31T00:00:00",
                        "2023-12-23T00:00:00",
                        "2024-01-11T00:00:00",
                        "2023-12-31T00:00:00"
                )))
                .andExpect(jsonPath("$[*].additionalInformation", containsInAnyOrder(
                        null,
                        "Pending approval",
                        null,
                        null
                )))
                .andExpect(jsonPath("$[*].tagName",
                        containsInAnyOrder("CLIENT", "CLIENT", "ADMINISTRATION", "CLIENT")))
                .andExpect(jsonPath("$[*].tagId", containsInAnyOrder(3, 3, 3, 4)));
    }

    @Transactional
    @Test
    @WithMockUser(roles = "ADMIN")
    public void get_log_with_admin_role_success() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/log/get-user-log")
                        .contentType("application/json")
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(4))
                .andExpect(jsonPath("$[*].text", containsInAnyOrder(
                        "Installation of kitchen furniture completed",
                        "Bedroom layout design requested",
                        "Create new user",
                        "Bedroom furniture installation started"
                )))
                .andExpect(jsonPath("$[*].dateOfCreation", containsInAnyOrder(
                        "2023-12-31T00:00:00",
                        "2023-12-23T00:00:00",
                        "2024-01-11T00:00:00",
                        "2023-12-31T00:00:00"
                )))
                .andExpect(jsonPath("$[*].additionalInformation", containsInAnyOrder(
                        null,
                        "Pending approval",
                        null,
                        null
                )))
                .andExpect(jsonPath("$[*].tagName",
                        containsInAnyOrder("CLIENT", "CLIENT", "ADMINISTRATION", "CLIENT")))
                .andExpect(jsonPath("$[*].tagId", containsInAnyOrder(3, 3, 3, 4)));
    }

    @Transactional
    @Test
    @WithMockUser(roles = "USER")
    public void get_tags_with_user_role_success() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/log/tags")
                        .contentType("application/json")
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(9))
                .andExpect(jsonPath("$[*].entityName", containsInAnyOrder("Piotr Kaczka",
                        "Sara Bernard",
                        "Monika Bałut",
                        "Marta Czajka",
                        "Solomon Duda",
                        "Jonny Depp",
                        "Sauron",
                        "user-admin",
                        "user")))
                .andExpect(jsonPath("$[*].tagName", containsInAnyOrder("CLIENT", "CLIENT",
                        "CLIENT", "CLIENT", "CLIENT", "CLIENT", "CLIENT", "ADMINISTRATION", "ADMINISTRATION")));
    }


    private String writeObjectToJsonFormat(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }
}