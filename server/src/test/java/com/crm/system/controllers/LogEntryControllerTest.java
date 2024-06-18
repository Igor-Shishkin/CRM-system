package com.crm.system.controllers;

import com.crm.system.models.User;
import com.crm.system.models.logForUser.LogEntry;
import com.crm.system.models.logForUser.TagName;
import com.crm.system.repository.ClientRepository;
import com.crm.system.repository.LogEntryRepository;
import com.crm.system.repository.UserRepository;
import com.crm.system.services.ClientService;
import com.crm.system.services.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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
                .orElseThrow(() -> new UserPrincipalNotFoundException("Unauthorized - User not found"));
        when(userService.getActiveUser()).thenReturn(user);
    }

    @BeforeAll
    static void beforeAll() {
        mySQLContainer.start();
    }

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
    public void get_log_without_authorization() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/log/get-user-log")
                        .contentType("application/json")
                        .accept("application/json"))
                .andExpect(status().isUnauthorized());
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
                .andExpect(jsonPath("$.size()").value(10))
                .andExpect(jsonPath("$[*].entityName", containsInAnyOrder("Piotr Kaczka",
                        "Sara Bernard",
                        "Monika Bałut",
                        "Marta Czajka",
                        "Solomon Duda",
                        "Jonny Depp",
                        "Sauron",
                        "user-admin",
                        "user",
                        "test-user")))
                .andExpect(jsonPath("$[*].tagName", containsInAnyOrder("CLIENT", "CLIENT",
                        "CLIENT", "CLIENT", "CLIENT", "CLIENT", "CLIENT", "ADMINISTRATION", "ADMINISTRATION", "ADMINISTRATION")));
    }

    @Transactional
    @Test
    @WithMockUser(roles = "ADMIN")
    public void get_tags_with_admin_role_success() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/log/tags")
                        .contentType("application/json")
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(10))
                .andExpect(jsonPath("$[*].entityName", containsInAnyOrder("Piotr Kaczka",
                        "Sara Bernard",
                        "Monika Bałut",
                        "Marta Czajka",
                        "Solomon Duda",
                        "Jonny Depp",
                        "Sauron",
                        "user-admin",
                        "user",
                        "test-user")))
                .andExpect(jsonPath("$[*].tagName", containsInAnyOrder("CLIENT", "CLIENT",
                        "CLIENT", "CLIENT", "CLIENT", "CLIENT", "CLIENT", "ADMINISTRATION", "ADMINISTRATION", "ADMINISTRATION")));
    }

    @Transactional
    @Test
    public void get_tags_without_authorization() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/log/tags")
                        .contentType("application/json")
                        .accept("application/json"))
                .andExpect(status().isUnauthorized());
    }

    @Transactional
    @Test
    @WithMockUser(roles = "USER")
    public void save_new_entry_to_log_with_user_role_success() throws Exception {

        LogEntry entry = new LogEntry.Builder()
                .withText("test text")
                .withTagName(TagName.CLIENT)
                .withIsDone(true)
                .withTagId(2)
                .withAdditionalInformation("test additional information")
                .withDeadline(LocalDateTime.of(2023, 12, 23, 1, 2, 3))
                .build();

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/log")
                        .contentType("application/json")
                        .content(writeObjectToJsonFormat(entry))
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Entry is saved"));

        Optional<LogEntry> savedEntryOptional = logEntryRepository.findById(6L);
        assertThat(savedEntryOptional).isNotEmpty();
        assertThat(savedEntryOptional.get().getText()).isEqualTo(entry.getText());
        assertThat(savedEntryOptional.get().isImportant()).isEqualTo(entry.isImportant());
        assertThat(savedEntryOptional.get().isDone()).isEqualTo(entry.isDone());
        assertThat(savedEntryOptional.get().getDateOfCreation()).isNotNull();
        assertThat(savedEntryOptional.get().getDeadline()).isEqualTo(entry.getDeadline());
        assertThat(savedEntryOptional.get().getAdditionalInformation()).isEqualTo(entry.getAdditionalInformation());
    }

    @Transactional
    @Test
    @WithMockUser(roles = "ADMIN")
    public void save_new_entry_to_log_with_admin_role_success() throws Exception {

        LogEntry entry = new LogEntry.Builder()
                .withText("test text")
                .withTagName(TagName.CLIENT)
                .withIsDone(true)
                .withTagId(2)
                .withAdditionalInformation("test additional information")
                .withDeadline(LocalDateTime.of(2023, 12, 23, 1, 2, 3))
                .build();

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/log")
                        .contentType("application/json")
                        .content(writeObjectToJsonFormat(entry))
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Entry is saved"));

        Optional<LogEntry> savedEntryOptional = logEntryRepository.findById(6L);
        assertThat(savedEntryOptional).isNotEmpty();
        assertThat(savedEntryOptional.get().getText()).isEqualTo(entry.getText());
        assertThat(savedEntryOptional.get().isImportant()).isEqualTo(entry.isImportant());
        assertThat(savedEntryOptional.get().isDone()).isEqualTo(entry.isDone());
        assertThat(savedEntryOptional.get().getDateOfCreation()).isNotNull();
        assertThat(savedEntryOptional.get().getDeadline()).isEqualTo(entry.getDeadline());
        assertThat(savedEntryOptional.get().getAdditionalInformation()).isEqualTo(entry.getAdditionalInformation());
    }

    @Transactional
    @Test
    public void save_new_entry_to_log_without_authorization() throws Exception {

        LogEntry entry = new LogEntry.Builder()
                .withText("test text")
                .withTagName(TagName.CLIENT)
                .withIsDone(true)
                .withTagId(2)
                .withAdditionalInformation("test additional information")
                .withDeadline(LocalDateTime.of(2023, 12, 23, 1, 2, 3))
                .build();

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/log")
                        .contentType("application/json")
                        .content(writeObjectToJsonFormat(entry))
                        .accept("application/json"))
                .andExpect(status().isUnauthorized());
    }

    @Transactional
    @Test
    @WithMockUser(roles = "USER")
    public void delete_entry_with_user_role_success() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/log")
                        .contentType("application/json")
                        .param("entryId", String.valueOf(2))
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Entry is deleted"));

        Optional<LogEntry> entryOptional = logEntryRepository.findById(2L);
        assertThat(entryOptional).isEmpty();
    }

    @Transactional
    @Test
    @WithMockUser(roles = "ADMIN")
    public void delete_entry_with_admin_role_success() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/log")
                        .contentType("application/json")
                        .param("entryId", String.valueOf(2))
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Entry is deleted"));

        Optional<LogEntry> entryOptional = logEntryRepository.findById(2L);
        assertThat(entryOptional).isEmpty();
    }

    @Transactional
    @Test
    @WithMockUser(roles = "ADMIN")
    public void delete_entry_with_wrong_id_success() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/log")
                        .contentType("application/json")
                        .param("entryId", String.valueOf(5))
                        .accept("application/json"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("You don't have entry with this ID"));
    }

    @Transactional
    @Test
    public void delete_entry_without_authorization() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/log")
                        .contentType("application/json")
                        .param("entryId", String.valueOf(2))
                        .accept("application/json"))
                .andExpect(status().isUnauthorized());
    }

    @Transactional
    @Test
    @WithMockUser(roles = "USER")
    public void change_is_important_with_user_role_success() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/log/change-important-status")
                        .contentType("application/json")
                        .param("entryId", String.valueOf(1))
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Status is changed"));

        Optional<LogEntry> entryOptional = logEntryRepository.findById(1L);
        assertThat(entryOptional).isNotEmpty();
        assertThat(entryOptional.get().isImportant()).isFalse();
    }

    @Transactional
    @Test
    @WithMockUser(roles = "ADMIN")
    public void change_is_important_with_admin_role_success() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/log/change-important-status")
                        .contentType("application/json")
                        .param("entryId", String.valueOf(1))
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Status is changed"));

        Optional<LogEntry> entryOptional = logEntryRepository.findById(1L);
        assertThat(entryOptional).isNotEmpty();
        assertThat(entryOptional.get().isImportant()).isFalse();
    }

    @Transactional
    @Test
    @WithMockUser(roles = "ADMIN")
    public void change_is_important_with_wrong_id() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/log/change-important-status")
                        .contentType("application/json")
                        .param("entryId", String.valueOf(5))
                        .accept("application/json"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("You don't have entry with this ID"));

    }
    @Transactional
    @Test
    public void change_is_important_without_authorization() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/log/change-important-status")
                        .contentType("application/json")
                        .param("entryId", String.valueOf(5))
                        .accept("application/json"))
                .andExpect(status().isUnauthorized());
    }

    @Transactional
    @Test
    @WithMockUser(roles = "USER")
    public void change_is_done_with_user_role_success() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/log/change-done-status")
                        .contentType("application/json")
                        .param("entryId", String.valueOf(1))
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Status is changed"));

        Optional<LogEntry> entryOptional = logEntryRepository.findById(1L);
        assertThat(entryOptional).isNotEmpty();
        assertThat(entryOptional.get().isDone()).isFalse();
    }

    @Transactional
    @Test
    @WithMockUser(roles = "ADMIN")
    public void change_is_done_with_admin_role_success() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/log/change-done-status")
                        .contentType("application/json")
                        .param("entryId", String.valueOf(1))
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Status is changed"));

        Optional<LogEntry> entryOptional = logEntryRepository.findById(1L);
        assertThat(entryOptional).isNotEmpty();
        assertThat(entryOptional.get().isDone()).isFalse();
    }

    @Transactional
    @Test
    @WithMockUser(roles = "ADMIN")
    public void change_is_done_with_wrong_id() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/log/change-done-status")
                        .contentType("application/json")
                        .param("entryId", String.valueOf(5))
                        .accept("application/json"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("You don't have entry with this ID"));

    }

    @Test
    public void change_is_done_without_authorization() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/log/change-done-status")
                        .contentType("application/json")
                        .param("entryId", String.valueOf(5))
                        .accept("application/json"))
                .andExpect(status().isUnauthorized());
    }


    private String writeObjectToJsonFormat(Object object) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper.writeValueAsString(object);
    }
}