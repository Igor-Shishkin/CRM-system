package com.crm.system.controllers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import com.crm.system.exception.*;
import com.crm.system.models.Client;
import com.crm.system.playload.request.AddLeadDTO;
import com.crm.system.playload.request.EditClientDataDTO;
import com.crm.system.playload.response.ClientInfoDTO;
import com.crm.system.playload.response.MessageResponse;
import com.crm.system.services.ClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ClientController.class)
class ClientControllerTest {

    @MockBean
    private ClientService clientService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void testAddNewLead_Success() throws Exception {
        AddLeadDTO addLeadDTO = new AddLeadDTO();
        when(clientService.addNewLead(addLeadDTO)).thenReturn(1L);

        mockMvc.perform(get("/api/user-board/add-new-lead")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .param("leadId", String.valueOf(1L)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Order controller: You don't have order with this ID"));
    }


    @Test
    void addNewLead() {
    }

    @Test
    void sendClientToBlackList() {
    }

    @Test
    void restoreClientFromBlackList() {
    }

    @Test
    void getAllClientsForUser() {
    }

    @Test
    void getAllLeadsForUser() {
    }

    @Test
    void getBlackListClientsForUser() {
    }

    @Test
    void getClient() {
    }

    @Test
    void editClientInfo() {
    }
}