package com.crm.system.controllers;

import com.crm.system.models.Client;
import com.crm.system.models.logForUser.LogEntry;
import com.crm.system.models.logForUser.TagName;
import com.crm.system.models.order.ItemForAdditionalPurchases;
import com.crm.system.models.order.Order;
import com.crm.system.playload.request.AddClientDTO;
import com.crm.system.repository.*;
import com.crm.system.services.ClientService;
import com.crm.system.services.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.data.Percentage;
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

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Sql({"/schema.sql", "/data.sql"})
class ItemsForAdditionalPurchasesControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    ItemForAdditionPurchasesRepository itemRepository;
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
                .standaloneSetup(ItemsForAdditionalPurchasesController.class)
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
    @WithMockUser(roles = "USER")
    public void save_new_set_of_items_success() throws Exception {

        ItemForAdditionalPurchases itemOneCorrect = new ItemForAdditionalPurchases(
                "ItemOne", 5, 3, 10);
        ItemForAdditionalPurchases itemTwoCorrect = new ItemForAdditionalPurchases(
                "ItemTwo", 2, 3, 10);
        ItemForAdditionalPurchases itemThreeCorrect = new ItemForAdditionalPurchases(
                "ItemThree", 10, 10, 10);
        ItemForAdditionalPurchases itemFourWithoutName = new ItemForAdditionalPurchases(
                "", 5, 3, 10);
        ItemForAdditionalPurchases itemFiveWithoutQuantity = new ItemForAdditionalPurchases(
                "Item without quantity", -1, 3, 10);
        ItemForAdditionalPurchases itemSixWithoutUnitPrice = new ItemForAdditionalPurchases(
                "Item without unit price", 5, -1, 10);

        Set<ItemForAdditionalPurchases> receivedItems = Set.of(itemOneCorrect,
                itemTwoCorrect,
                itemThreeCorrect,
                itemFourWithoutName,
                itemFiveWithoutQuantity,
                itemSixWithoutUnitPrice);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/user-board/items-for-addition-purchases/save-items")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(receivedItems))
                        .param("orderId", String.valueOf(2))
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Items are saved"));

        Set<ItemForAdditionalPurchases> savedItems = itemRepository.findAll().stream()
                .filter(item -> item.getOrder().getOrderId() == 2)
                .collect(Collectors.toSet());

        assertThat(savedItems.size()).isEqualTo(3);
        assertThat(savedItems).extracting("itemName")
                .containsAll ( Set.of("ItemTwo", "ItemThree", "ItemOne"));

        assertThatThrownBy(() -> assertThat(savedItems).extracting("itemName")
                .containsAnyOf ("Item without quantity", "Item without unit price", ""))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("Item without quantity");

        Optional<ItemForAdditionalPurchases> optionalItemOne = savedItems.stream()
                .filter(item -> item.getItemName().equals("ItemOne"))
                .findFirst();
        assertThat(optionalItemOne).isNotEmpty();
        ItemForAdditionalPurchases itemOne = optionalItemOne.get();
        assertThat(itemOne.getQuantity()).isEqualTo(5);
        assertThat(itemOne.getUnitPrice()).isEqualTo(3);
        assertThat(itemOne.getTotalPrice()).isEqualTo(16.5);

        Optional<Order> optionalOrder = orderRepository.findById(2L);
        assertThat(optionalOrder).isNotEmpty();
        assertThat(optionalOrder.get().getResultPrice()).isCloseTo(133.1, Percentage.withPercentage(0.1));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void save_new_set_of_items_when_agreement_is_signed() throws Exception {

        ItemForAdditionalPurchases itemOneCorrect = new ItemForAdditionalPurchases(
                "ItemOne", 5, 3, 10);
        Set<ItemForAdditionalPurchases> receivedItems = Set.of(itemOneCorrect);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/user-board/items-for-addition-purchases/save-items")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(receivedItems))
                        .param("orderId", String.valueOf(1))
                        .accept("application/json"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value("You can't changes item's for addition purchases when agreement is signed"));
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    public void save_new_set_of_items_with_wrong_role() throws Exception {

        ItemForAdditionalPurchases itemOneCorrect = new ItemForAdditionalPurchases(
                "ItemOne", 5, 3, 10);
        Set<ItemForAdditionalPurchases> receivedItems = Set.of(itemOneCorrect);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/user-board/items-for-addition-purchases/save-items")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(receivedItems))
                        .param("orderId", String.valueOf(1))
                        .accept("application/json"))
                .andExpect(status().isForbidden());
    }
}