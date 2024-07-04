package com.crm.system.controllers;

import com.crm.system.models.Client;
import com.crm.system.models.ClientStatus;
import com.crm.system.models.logForUser.LogEntry;
import com.crm.system.models.logForUser.TagName;
import com.crm.system.playload.request.AddClientDTO;
import com.crm.system.playload.request.EditClientDataDTO;
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
import org.mockito.Mockito;
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

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
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

    @BeforeEach
    public void initialize() throws UserPrincipalNotFoundException {
        when(userService.getActiveUserId()).thenReturn(1L);
        when(userService.getActiveUser()).thenReturn(userRepository.findById(1L)
                .orElseThrow(() -> new UserPrincipalNotFoundException("Unauthorized - User not found")));
    }

    @BeforeAll
    static void beforeAll() {
        mySQLContainer.start();
    }

    @AfterAll
    static void afterAll() {
        mySQLContainer.stop();
    }




    @Test
    @WithMockUser(roles = "USER")
    public void add_lead_success() throws Exception {

        AddClientDTO newClient = new AddClientDTO();
        newClient.setFullName("test name");
        newClient.setEmail("test@gmail.com");
        newClient.setAddress("test address");
        newClient.setPhoneNumber("test +0 000 000 000");

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/user-board/add-new-client")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newClient))
                .accept("application/json"))
                .andExpect(status().isCreated())
                .andExpect(content().json("9"));

        Optional<Client> optionalClient = clientRepository.findById(9L);
        assertThat(optionalClient).isNotEmpty();
        assertThat(optionalClient.get().getFullName()).isEqualTo(newClient.getFullName());
        assertThat(optionalClient.get().getEmail()).isEqualTo(newClient.getEmail());
        assertThat(optionalClient.get().getAddress()).isEqualTo(newClient.getAddress());

        Optional<LogEntry> optionalLogEntry = logEntryRepository.findById(6L);

        assertThat(optionalLogEntry).isNotEmpty();
        assertThat(optionalLogEntry.get().getText()).isEqualTo("Client 'test name' is created");
        assertThat(optionalLogEntry.get().getTagName()).isEqualTo(TagName.CLIENT);
        assertThat(optionalLogEntry.get().getTagId()).isEqualTo(9L);
    }




    @Test
    @WithMockUser(roles = "USER")
    public void add_client_email_already_exist() throws Exception {
        AddClientDTO newLead = new AddClientDTO();
        newLead.setFullName("test name");
        newLead.setEmail("pitor@gmail.com");
        newLead.setAddress("test address");
        newLead.setPhoneNumber("test +0 000 000 000");

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/user-board/add-new-client")
                        .contentType("application/json")
                        .content(writeObjectToJsonFormat(newLead))
                        .accept("application/json"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Client with this email already exists"));
    }





    @Test
    @WithMockUser(roles = "USER")
    public void add_lead_with_empty_user_name() throws Exception {
        AddClientDTO newLead = new AddClientDTO();
        newLead.setEmail("pitor@gmail.com");
        newLead.setAddress("test address");
        newLead.setPhoneNumber("test +0 000 000 000");

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/user-board/add-new-client")
                .contentType("application/json")
                .content(writeObjectToJsonFormat(newLead))
                .accept("application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Invalid request content."));
    }





    @Test
    @WithMockUser(roles = "USER")
    public void add_lead_with_empty_email() throws Exception {
        AddClientDTO newLead = new AddClientDTO();
        newLead.setFullName("test name");
        newLead.setEmail("  ");
        newLead.setAddress("test address");
        newLead.setPhoneNumber("test +0 000 000 000");

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/user-board/add-new-client")
                .contentType("application/json")
                .content(writeObjectToJsonFormat(newLead))
                .accept("application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Invalid request content."));
    }





    @Test
    @WithMockUser(roles = "ADMIN")
    public void add_lead_with_wrong_role() throws Exception {
        AddClientDTO newLead = new AddClientDTO();
        newLead.setFullName("test name");
        newLead.setEmail("test@gmail.com");
        newLead.setAddress("test address");
        newLead.setPhoneNumber("test +0 000 000 000");

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/user-board/add-new-client")
                        .contentType("application/json")
                        .content(writeObjectToJsonFormat(newLead))
                        .accept("application/json"))
                .andExpect(status().isForbidden());
    }





    @Test
    @WithMockUser(roles = "USER")
    public void sent_lead_to_blacklist_success() throws Exception {
        long clientId = 3L;

        mockMvc.perform(MockMvcRequestBuilders
                .patch("/api/user-board/send-client-to-black-list")
                .contentType("application/json")
                        .param("clientId", String.valueOf(clientId))
                .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value(String.format("Lead with ID=%d is in blacklist", clientId)));

        Optional<Client> optionalClient = clientRepository.findById(3L);
        assertThat(optionalClient).isNotEmpty();
        assertThat(optionalClient.get().getStatus()).isEqualTo(ClientStatus.BLACKLIST);

        Optional<LogEntry> optionalLogEntry = logEntryRepository.findById(6L);
        assertThat(optionalLogEntry).isNotEmpty();

        assertThat(optionalLogEntry.get().getText())
                .isEqualTo("Client 'Sara Bernard' goes to blackList");
        assertThat(optionalLogEntry.get().getTagName()).isEqualTo(TagName.CLIENT);
        assertThat(optionalLogEntry.get().getTagId()).isEqualTo(clientId);
    }





    @Test
    @WithMockUser(roles = "USER")
    public void sent_lead_to_blacklist_user_doesnt_have_client_with_this_id() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/api/user-board/send-client-to-black-list")
                        .contentType("application/json")
                        .param("clientId", String.valueOf(8L))
                        .accept("application/json"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("You do not have a client with ID=8"));
    }




    @Test
    @WithMockUser(roles = "ADMIN")
    public void sent_lead_to_blacklist_with_wrong_role() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/api/user-board/send-client-to-black-list")
                        .contentType("application/json")
                        .param("clientId", String.valueOf(3L))
                        .accept("application/json"))
                .andExpect(status().isForbidden());
    }




    @Test
    @WithMockUser(roles = "USER")
    public void restore_client_from_blackList_status_must_be_client_success() throws Exception {
        long clientId = 5L;

        mockMvc.perform(MockMvcRequestBuilders
                .patch("/api/user-board/restore-client-from-black-list")
                .contentType("application/json")
                .param("clientId", String.valueOf(clientId))
                .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value(String.format("Client with ID=%d is restored from black list", clientId)));

        Optional<Client> optionalClient = clientRepository.findById(clientId);
        assertThat(optionalClient).isNotEmpty();
        assertThat(optionalClient.get().getStatus()).isEqualTo(ClientStatus.CLIENT);

        Optional<LogEntry> optionalLogEntry = logEntryRepository.findById(6L);
        assertThat(optionalLogEntry).isNotEmpty();
        assertThat(optionalLogEntry.get().getText())
                .isEqualTo("Client 'Solomon Duda' is restored from blackList");
        assertThat(optionalLogEntry.get().getTagName()).isEqualTo(TagName.CLIENT);
        assertThat(optionalLogEntry.get().getTagId()).isEqualTo(clientId);
    }




    @Test
    @WithMockUser(roles = "USER")
    public void restore_client_from_blackList_status_must_be_lead_success() throws Exception {
        long clientId = 4L;

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/api/user-board/restore-client-from-black-list")
                        .contentType("application/json")
                        .param("clientId", String.valueOf(clientId))
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value(String.format("Client with ID=%d is restored from black list", clientId)));

        Optional<Client> optionalClient = clientRepository.findById(clientId);
        assertThat(optionalClient).isNotEmpty();
        assertThat(optionalClient.get().getStatus()).isEqualTo(ClientStatus.LEAD);

        Optional<LogEntry> optionalLogEntry = logEntryRepository.findById(6L);
        assertThat(optionalLogEntry).isNotEmpty();
        assertThat(optionalLogEntry.get().getText())
                .isEqualTo("Client 'Marta Czajka' is restored from blackList");
        assertThat(optionalLogEntry.get().getTagName()).isEqualTo(TagName.CLIENT);
        assertThat(optionalLogEntry.get().getTagId()).isEqualTo(clientId);
    }





    @Test
    @WithMockUser(roles = "USER")
    public void restore_client_from_blackList_user_doesnt_have_this_client() throws Exception {
        long clientId = 8L;

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/api/user-board/restore-client-from-black-list")
                        .contentType("application/json")
                        .param("clientId", String.valueOf(clientId))
                        .accept("application/json"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value(String.format("You do not have a client with ID=%d", clientId)));

    }





    @Test
    @WithMockUser(roles = "ADMIN")
    public void restore_client_from_blackList_wrong_role() throws Exception {
        long clientId = 3L;

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/api/user-board/restore-client-from-black-list")
                        .contentType("application/json")
                        .param("clientId", String.valueOf(clientId))
                        .accept("application/json"))
                .andExpect(status().isForbidden());
    }





    @Test
    @WithMockUser(roles = "USER")
    public void get_all_clients_with_client_status_for_user_success() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/user-board/clients")
                .accept("application/json"))
                .andExpect(jsonPath("$.size()").value(3))
                .andExpect(jsonPath("$.[*].fullName")
                        .value(containsInAnyOrder("Sara Bernard", "Jonny Depp", "Sauron")));
    }





    @Test
    @WithMockUser(roles = "ADMIN")
    public void get_all_clients_with_client_status_for_user_wrong_role() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/user-board/clients")
                        .accept("application/json"))
                .andExpect(status().isForbidden());
    }





    @Test
    @WithMockUser(roles = "USER")
    public void get_all_clients_with_lead_status_for_user_success() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/user-board/leads")
                        .accept("application/json"))
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$.[*].fullName")
                        .value(containsInAnyOrder("Piotr Kaczka", "Monika Bałut")));
    }





    @Test
    @WithMockUser(roles = "ADMIN")
    public void get_all_clients_with_lead_status_for_user_wrong_role() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/user-board/leads")
                        .accept("application/json"))
                .andExpect(status().isForbidden());
    }





    @Test
    @WithMockUser(roles = "USER")
    public void get_all_clients_with_blacklist_status_for_user_success() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/user-board/get-black-list-clients")
                        .accept("application/json"))
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$.[*].fullName")
                        .value(containsInAnyOrder("Marta Czajka", "Solomon Duda")));
    }





    @Test
    @WithMockUser(roles = "ADMIN")
    public void get_all_clients_with_blacklist_status_for_user_wrong_role() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/user-board/leads")
                        .accept("application/json"))
                .andExpect(status().isForbidden());
    }





    @Test
    @WithMockUser(roles = "USER")
    public void get_client_info_success() throws Exception {
        long clientId = 2L;

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/user-board/client-info")
                .contentType("application/json")
                .param("clientId", String.valueOf(clientId))
                .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Monika Bałut"))
                .andExpect(jsonPath("$.email").value("monika@gmail.com"))
                .andExpect(jsonPath("$.phoneNumber").value("000666777"))
                .andExpect(jsonPath("$.status").value("LEAD"))
                .andExpect(jsonPath("$.address").value("Poland, Warszawa"))
                .andExpect(jsonPath("$.dateOfLastChange").value("2023-12-24 00:00:00"))
                .andExpect(jsonPath("$.dateOfCreation").value("2023-12-04 00:00:00"))
                .andExpect(jsonPath("$.orders.size()").value("1"))
                .andExpect(jsonPath("$.orders[0].realNeed").value("Order a handmade table"));
    }





    @Test
    @WithMockUser(roles = "USER")
    public void get_client_info_dont_have_client_with_this_id() throws Exception {
        long clientId = 8L;

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/user-board/client-info")
                        .contentType("application/json")
                        .param("clientId", String.valueOf(clientId))
                        .accept("application/json"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value(String.format("You do not have a client with ID=%d", clientId)));
    }





    @Test
    @WithMockUser(roles = "ADMIN")
    public void get_client_info_with_wrong_role() throws Exception {
        long clientId = 1L;

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/user-board/client-info")
                        .contentType("application/json")
                        .param("clientId", String.valueOf(clientId))
                        .accept("application/json"))
                .andExpect(status().isForbidden());
    }





    @Test
    @WithMockUser(roles = "USER")
    public void edit_client_data_success() throws Exception {
        EditClientDataDTO editClientDataDTO = new EditClientDataDTO();
        editClientDataDTO.setClientId(2L);
        editClientDataDTO.setEmail("changed.mail@gmail.com");
        editClientDataDTO.setPhoneNumber("changed phone number");
        editClientDataDTO.setAddress("changed address");
        editClientDataDTO.setFullName("Changed name");

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/api/user-board/edit-client-data")
                        .contentType("application/json")
                        .content(writeObjectToJsonFormat(editClientDataDTO))
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Changes are saved"));

        Optional<Client> optionalClient = clientRepository.findById(2L);
        assertThat(optionalClient).isNotEmpty();
        assertThat(optionalClient.get().getFullName()).isEqualTo("Changed name");
        assertThat(optionalClient.get().getEmail()).isEqualTo("changed.mail@gmail.com");
        assertThat(optionalClient.get().getPhoneNumber()).isEqualTo("changed phone number");
        assertThat(optionalClient.get().getAddress()).isEqualTo("changed address");
        assertThat(optionalClient.get().getDateOfLastChange().toString()).isNotEqualTo("2023-12-24 00:00:00");
    }





    @Test
    @WithMockUser(roles = "USER")
    public void edit_client_data_wrong_email() throws Exception {
        EditClientDataDTO editClientDataDTO = new EditClientDataDTO();
        editClientDataDTO.setClientId(2L);
        editClientDataDTO.setEmail("wrong.gmail.com");
        editClientDataDTO.setPhoneNumber("changed phone number");
        editClientDataDTO.setAddress("changed address");
        editClientDataDTO.setFullName("Changed name");

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/api/user-board/edit-client-data")
                        .contentType("application/json")
                        .content(writeObjectToJsonFormat(editClientDataDTO))
                        .accept("application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Invalid request content."));
    }





    @Test
    @WithMockUser(roles = "USER")
    public void edit_client_data_wrong_empty_fullname() throws Exception {
        EditClientDataDTO editClientDataDTO = new EditClientDataDTO();
        editClientDataDTO.setClientId(2L);
        editClientDataDTO.setEmail("test@gmail.com");
        editClientDataDTO.setPhoneNumber("changed phone number");
        editClientDataDTO.setAddress("changed address");
        editClientDataDTO.setFullName("");

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/api/user-board/edit-client-data")
                        .contentType("application/json")
                        .content(writeObjectToJsonFormat(editClientDataDTO))
                        .accept("application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Invalid request content."));
    }






    @Test
    @WithMockUser(roles = "USER")
    public void edit_client_data_empty_email() throws Exception {
        EditClientDataDTO editClientDataDTO = new EditClientDataDTO();
        editClientDataDTO.setClientId(2L);
        editClientDataDTO.setEmail("");
        editClientDataDTO.setPhoneNumber("changed phone number");
        editClientDataDTO.setAddress("changed address");
        editClientDataDTO.setFullName("Changed name");

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/api/user-board/edit-client-data")
                        .contentType("application/json")
                        .content(writeObjectToJsonFormat(editClientDataDTO))
                        .accept("application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Invalid request content."));
    }




    private String writeObjectToJsonFormat(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

}





























