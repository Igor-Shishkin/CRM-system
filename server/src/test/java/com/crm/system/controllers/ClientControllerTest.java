package com.crm.system.controllers;

import com.crm.system.models.ClientStatus;
import com.crm.system.models.history.HistoryMessage;
import com.crm.system.playload.request.AddLeadDTO;
import com.crm.system.repository.ClientRepository;
import com.crm.system.repository.HistoryMessageRepository;
import com.crm.system.repository.RoleRepository;
import com.crm.system.repository.UserRepository;
import com.crm.system.security.PasswordConfig;
import com.crm.system.services.ClientService;
import com.crm.system.services.HistoryMessageService;
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
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Sql({"/schema.sql", "/data.sql"})
class ClientControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ClientService clientService;
    @Autowired
    ClientRepository clientRepository;
    @MockBean
    private UserService userService;
    @Autowired
    HistoryMessageRepository historyMessageRepository;

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
    public void initialize() {
        when(userService.getActiveUserId()).thenReturn(1L);
    }

    @BeforeAll
    static void beforeAll() {  mySQLContainer.start(); }
    @AfterAll
    static void afterAll() {
        mySQLContainer.stop();
    }

    @Test
    @WithMockUser(roles = "USER")
    public void add_lead_success() throws Exception {
        AddLeadDTO newLead = new AddLeadDTO();
        newLead.setFullName("test name");
        newLead.setEmail("test@gmail.com");
        newLead.setAddress("test address");
        newLead.setPhoneNumber("test +0 000 000 000");

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/user-board/add-new-lead")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newLead))
                .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json("7"));

        assertThat(clientRepository.findById(7L).get().getFullName()).isEqualTo(newLead.getFullName());
        assertThat(clientRepository.findById(7L).get().getEmail()).isEqualTo(newLead.getEmail());

        assertThat(historyMessageRepository.findById(6L).get().getMessageText())
                .isEqualTo("Lead test name is created");
    }

    @Test
    @WithMockUser(roles = "USER")
    public void add_lead_email_already_exist() throws Exception {
        AddLeadDTO newLead = new AddLeadDTO();
        newLead.setFullName("test name");
        newLead.setEmail("pitor@gmail.com");
        newLead.setAddress("test address");
        newLead.setPhoneNumber("test +0 000 000 000");

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/user-board/add-new-lead")
                        .contentType("application/json")
                        .content(writeValueToJsonFormat(newLead))
                        .accept("application/json"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Lid with this email already exists"));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void add_lead_with_empty_user_name() throws Exception {
        AddLeadDTO newLead = new AddLeadDTO();
        newLead.setEmail("pitor@gmail.com");
        newLead.setAddress("test address");
        newLead.setPhoneNumber("test +0 000 000 000");

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/user-board/add-new-lead")
                .contentType("application/json")
                .content(writeValueToJsonFormat(newLead))
                .accept("application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Invalid request content."));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void add_lead_with_empty_email() throws Exception {
        AddLeadDTO newLead = new AddLeadDTO();
        newLead.setFullName("test name");
        newLead.setAddress("test address");
        newLead.setPhoneNumber("test +0 000 000 000");

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/user-board/add-new-lead")
                .contentType("application/json")
                .content(writeValueToJsonFormat(newLead))
                .accept("application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Invalid request content."));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void add_lead_with_wrong_role() throws Exception {
        AddLeadDTO newLead = new AddLeadDTO();
        newLead.setFullName("test name");
        newLead.setAddress("test address");
        newLead.setPhoneNumber("test +0 000 000 000");

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/user-board/add-new-lead")
                        .contentType("application/json")
                        .content(writeValueToJsonFormat(newLead))
                        .accept("application/json"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.detail").value("Invalid request content."));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void sent_lead_to_blacklist_success() throws Exception {
        long clientId = 3L;

        mockMvc.perform(MockMvcRequestBuilders
                .put("/api/user-board/send-client-to-black-list")
                .contentType("application/json")
                        .param("clientId", String.valueOf(clientId))
                .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value(String.format("Lead with %d id is in blacklist", clientId)));

        assertThat(clientRepository.findById(3L).get().getStatus()).isEqualTo(ClientStatus.BLACKLIST);
        assertThat(historyMessageRepository.findById(6L).get().getMessageText())
                .isEqualTo("Client Sara Bernard goes to blackList");
    }

    @Test
    @WithMockUser(roles = "USER")
    public void sent_lead_to_blacklist_client_doesnt_belong_user() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/user-board/send-client-to-black-list")
                        .contentType("application/json")
                        .param("clientId", String.valueOf(6L))
                        .accept("application/json"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Client with %d id doesn't exist"));
    }

    private String writeValueToJsonFormat(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

}





























