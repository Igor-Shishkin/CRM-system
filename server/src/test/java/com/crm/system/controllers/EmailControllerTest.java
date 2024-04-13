package com.crm.system.controllers;

import com.crm.system.models.ClientStatus;
import com.crm.system.models.logForUser.LogEntry;
import com.crm.system.models.logForUser.TagName;
import com.crm.system.playload.request.SentEmailDTO;
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
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Sql({"/schema.sql", "/data.sql"})
class EmailControllerTest {
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
    @MockBean
    JavaMailSender javaMailSender;

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

    @BeforeEach
    public void initialize() throws UserPrincipalNotFoundException {
        when(userService.getActiveUserId()).thenReturn(1L);
        when(userService.getActiveUser()).thenReturn(userRepository.findById(1L)
                .orElseThrow(() -> new UserPrincipalNotFoundException("There isn't User with this ID")));
    }

    @BeforeAll
    static void beforeAll() {  mySQLContainer.start(); }
    @AfterAll
    static void afterAll() {
        mySQLContainer.stop();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void sent_email_with_admin_role_success() throws Exception {
        SentEmailDTO sentEmailDTO = new SentEmailDTO();
        sentEmailDTO.setEmail("test@email.com");
        sentEmailDTO.setTextOfEmail("Some text");
        sentEmailDTO.setSubjectOfMail("test subject");

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/email/sent-email")
                        .contentType("application/json")
                        .content(writeObjectToJsonFormat(sentEmailDTO))
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Email sent successfully"));

        verify(javaMailSender).send(any(SimpleMailMessage.class));

        Optional<LogEntry> optionalLogEntry = logEntryRepository.findById(6L);
        assertThat(optionalLogEntry).isNotEmpty();

        assertThat(optionalLogEntry.get().getAdditionalInformation()).isEqualTo(sentEmailDTO.getTextOfEmail());
        assertThat(optionalLogEntry.get().getTagName()).isEqualTo(TagName.EMAIL);
        assertThat(optionalLogEntry.get().isDone()).isTrue();
        assertThat(optionalLogEntry.get().getText())
                .isEqualTo(String.format("Message sent. Subject: %s. Email: %s",
                        sentEmailDTO.getSubjectOfMail(),
                        sentEmailDTO.getEmail()));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void sent_email_with_user_role_success() throws Exception {
        SentEmailDTO sentEmailDTO = new SentEmailDTO();
        sentEmailDTO.setEmail("test@email.com");
        sentEmailDTO.setTextOfEmail("Some text");
        sentEmailDTO.setSubjectOfMail("test subject");

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/email/sent-email")
                        .contentType("application/json")
                        .content(writeObjectToJsonFormat(sentEmailDTO))
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Email sent successfully"));

        verify(javaMailSender).send(any(SimpleMailMessage.class));

        Optional<LogEntry> optionalLogEntry = logEntryRepository.findById(6L);
        assertThat(optionalLogEntry).isNotEmpty();

        assertThat(optionalLogEntry.get().getAdditionalInformation()).isEqualTo(sentEmailDTO.getTextOfEmail());
        assertThat(optionalLogEntry.get().getTagName()).isEqualTo(TagName.EMAIL);
        assertThat(optionalLogEntry.get().isDone()).isTrue();
        assertThat(optionalLogEntry.get().getText())
                .isEqualTo(String.format("Message sent. Subject: %s. Email: %s",
                        sentEmailDTO.getSubjectOfMail(),
                        sentEmailDTO.getEmail()));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void sent_email_email_address_is_empty() throws Exception {
        SentEmailDTO sentEmailDTO = new SentEmailDTO();
        sentEmailDTO.setEmail("");
        sentEmailDTO.setTextOfEmail("Some text");
        sentEmailDTO.setSubjectOfMail("test subject");

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/email/sent-email")
                        .contentType("application/json")
                        .content(writeObjectToJsonFormat(sentEmailDTO))
                        .accept("application/json"))
                .andExpect(status().isIAmATeapot())
                .andExpect(jsonPath("$.message")
                        .value("Email address or text of email is empty"));
    }
    @Test
    @WithMockUser(roles = "USER")
    public void sent_email_email_text_is_empty() throws Exception {
        SentEmailDTO sentEmailDTO = new SentEmailDTO();
        sentEmailDTO.setEmail("test@email.com");
        sentEmailDTO.setTextOfEmail("");
        sentEmailDTO.setSubjectOfMail("test subject");

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/email/sent-email")
                        .contentType("application/json")
                        .content(writeObjectToJsonFormat(sentEmailDTO))
                        .accept("application/json"))
                .andExpect(status().isIAmATeapot())
                .andExpect(jsonPath("$.message")
                        .value("Email address or text of email is empty"));
    }

    private String writeObjectToJsonFormat(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }
}