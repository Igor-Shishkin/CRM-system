package com.crm.system.controllers;

import com.crm.system.exception.MismanagementOfTheClientException;
import com.crm.system.exception.RequestOptionalIsEmpty;
import com.crm.system.exception.SubjectNotBelongToActiveUser;
import com.crm.system.models.Client;
import com.crm.system.models.order.ItemForAdditionalPurchases;
import com.crm.system.models.order.Order;
import com.crm.system.playload.request.ChangeOrderDTO;
import com.crm.system.playload.request.CreateNewOrderDTO;
import com.crm.system.playload.response.ItemsForAdditionalPurchasesDTO;
import com.crm.system.playload.response.OrderInfoDTO;
import com.crm.system.services.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.Mockito.*;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(OrderController.class)
class OrderControllerTest {


    @MockBean
    OrderService orderService;
    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();
    private static OrderInfoDTO expectedDTO;
    private static ItemsForAdditionalPurchasesDTO expectedItemsForAdditionalPurchasesDTO;

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void get_order_by_id_success() throws Exception {

        when(orderService.getOrderInfoResponce(1L)).thenReturn(expectedDTO);

        mockMvc.perform(get("/api/user-board/order")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                        .param("orderId", String.valueOf(expectedDTO.getOrderId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(expectedDTO.getOrderId()))
                .andExpect(jsonPath("$.address").value(expectedDTO.getAddress()))
                .andExpect(jsonPath("$.realNeed").value(expectedDTO.getRealNeed()))
                .andExpect(jsonPath("$.clientEmail").value(expectedDTO.getClientEmail()))
                .andExpect(jsonPath("$.clientId").value(expectedDTO.getClientId()))
                .andExpect(jsonPath("$.clientFullName").value(expectedDTO.getClientFullName()))
                .andExpect(jsonPath("$.clientPhoneNumber").value(expectedDTO.getClientPhoneNumber()))
                .andExpect(jsonPath("$.resultPrice"). value(expectedDTO.getResultPrice()))
                .andExpect(jsonPath("$.estimateBudged").value(0.0));
    }
    @Test
    @WithMockUser(username = "user", roles = "USER")
    void get_order_by_id_error() throws Exception {

        when(orderService.getOrderInfoResponce(1L))
                .thenThrow(new RequestOptionalIsEmpty("You don't have order with this ID"));

        mockMvc.perform(get("/api/user-board/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .param("orderId", String.valueOf(expectedDTO.getOrderId())))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message")
                        .value("Order controller: You don't have order with this ID"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void get_calculations_by_order_id_success() throws Exception {
        when(orderService.getCalculations(1L)).thenReturn(expectedItemsForAdditionalPurchasesDTO);

        mockMvc.perform(get("/api/user-board/order/calculations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .param("orderId", String.valueOf(expectedDTO.getOrderId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultPrice").value(expectedItemsForAdditionalPurchasesDTO.getResultPrice()))
                .andExpect( jsonPath("$.items").isArray());
    }
    @Test
    @WithMockUser(username = "user", roles = "USER")
    void get_calculations_by_order_id_error() throws Exception {
        when(orderService.getCalculations(1L))
                .thenThrow(new RequestOptionalIsEmpty("You don't have order with this ID"));

        mockMvc.perform(get("/api/user-board/order/calculations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .param("orderId", String.valueOf(expectedDTO.getOrderId())))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message")
                        .value("Order controller: You don't have order with this ID"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void sign_agreement_by_order_id_success() throws Exception {

        mockMvc.perform(post("/api/user-board/order/sign-agreement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .param("orderId", String.valueOf(expectedDTO.getOrderId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Agreement's signed"));
    }
    @Test
    @WithMockUser(username = "user", roles = "USER")
    void sign_agreement_by_order_id_mismanagement_error() throws Exception {

        doThrow(new MismanagementOfTheClientException("Agreement is already signed"))
                .when(orderService).signAgreement(1);


        mockMvc.perform(post("/api/user-board/order/sign-agreement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .param("orderId", String.valueOf(expectedDTO.getOrderId())))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message")
                        .value("Order controller: Agreement is already signed"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void sign_agreement_by_order_id_optional_empty_error() throws Exception {

        doThrow(new RequestOptionalIsEmpty("You don't have order with this ID"))
                .when(orderService).signAgreement(1);


        mockMvc.perform(post("/api/user-board/order/sign-agreement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .param("orderId", String.valueOf(expectedDTO.getOrderId())))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message")
                        .value("Order controller: You don't have order with this ID"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void cancel_agreement_by_id_success() throws Exception {
        mockMvc.perform(post("/api/user-board/order/cancel-agreement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .param("orderId", String.valueOf(expectedDTO.getOrderId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Agreement's status is canceled"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void cancel_agreement_by_id_optional_empty_error() throws Exception {
        doThrow(new RequestOptionalIsEmpty("You don't have order with this ID"))
                .when(orderService).cancelAgreement(1);

        mockMvc.perform(post("/api/user-board/order/cancel-agreement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .param("orderId", String.valueOf(expectedDTO.getOrderId())))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message")
                        .value("Order controller: You don't have order with this ID"));
    }
    @Test
    @WithMockUser(username = "user", roles = "USER")
    void cancel_agreement_by_id_mismanagement_error() throws Exception {
        doThrow(new MismanagementOfTheClientException("Agreement isn't signed"))
                .when(orderService).cancelAgreement(1);

        mockMvc.perform(post("/api/user-board/order/cancel-agreement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .param("orderId", String.valueOf(expectedDTO.getOrderId())))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message")
                        .value("Order controller: Agreement isn't signed"));
    }
    @Test
    @WithMockUser(username = "user", roles = "USER")
    void cancel_payment_by_order_id_error() throws Exception {
        doThrow(new MismanagementOfTheClientException("Payment was not made"))
                .when(orderService).cancelPayment(1);

        mockMvc.perform(post("/api/user-board/order/cancel-payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .param("orderId", String.valueOf(expectedDTO.getOrderId())))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message")
                        .value("Order controller: Payment was not made"));
    }
    @Test
    @WithMockUser(username = "user", roles = "USER")
    void cancel_payment_by_order_id_success() throws Exception {
        mockMvc.perform(post("/api/user-board/order/cancel-agreement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .param("orderId", String.valueOf(expectedDTO.getOrderId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Agreement's status is canceled"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void confirm_payment_by_order_id_success() throws Exception {
        mockMvc.perform(post("/api/user-board/order/confirm-payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .param("orderId", String.valueOf(expectedDTO.getOrderId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Payment is confirm"));
    }
    @Test
    @WithMockUser(username = "user", roles = "USER")
    void confirm_payment_by_order_id_mismanagement_error() throws Exception {

        doThrow(new MismanagementOfTheClientException("Payment was not made"))
                .when(orderService).confirmPayment(1);

        mockMvc.perform(post("/api/user-board/order/confirm-payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .param("orderId", String.valueOf(expectedDTO.getOrderId())))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message")
                        .value("Order controller: Payment was not made"));
    }
    @Test
    @WithMockUser(username = "user", roles = "USER")
    void confirm_payment_by_order_id_optional_empty_error() throws Exception {

        doThrow(new RequestOptionalIsEmpty("You don't have order with this ID"))
                .when(orderService).confirmPayment(1);

        mockMvc.perform(post("/api/user-board/order/confirm-payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .param("orderId", String.valueOf(expectedDTO.getOrderId())))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message")
                        .value("Order controller: You don't have order with this ID"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void save_order_changes_success() throws Exception {
        ChangeOrderDTO orderDTO = new ChangeOrderDTO();
        orderDTO.setAddress("test address");
        orderDTO.setOrderId(1L);
        orderDTO.setEstimateBudged(765);

        mockMvc.perform(post("/api/user-board/order/save-order-changes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDTO))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Changes are saved"));
    }
    @Test
    @WithMockUser(username = "user", roles = "USER")
    void save_order_changes_without_body() throws Exception {
        mockMvc.perform(post("/api/user-board/order/save-order-changes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }
    @Test
    @WithMockUser(username = "user", roles = "USER")
    void save_order_changes_error() throws Exception {
        ChangeOrderDTO orderDTO = new ChangeOrderDTO();
        orderDTO.setAddress("test address");
        orderDTO.setOrderId(1L);
        orderDTO.setEstimateBudged(765);

        doThrow(new RequestOptionalIsEmpty("You don't have order with this ID"))
                .when(orderService).saveOrderChanges(any(ChangeOrderDTO.class));

        mockMvc.perform(post("/api/user-board/order/save-order-changes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDTO))
                        .with(csrf()))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message")
                        .value("Order controller: You don't have order with this ID"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void create_new_order_sucess() throws Exception {
        CreateNewOrderDTO newOrderDTO = new CreateNewOrderDTO();
        newOrderDTO.setClientId(1L);
        newOrderDTO.setRealNeed("test need");
        newOrderDTO.setEstimateBudget(1234);

        when(orderService.createNewOrder(any(CreateNewOrderDTO.class))).thenReturn(10L);

        mockMvc.perform(post("/api/user-board/order/create-new-order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newOrderDTO))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("10"));
    }
    @Test
    @WithMockUser(username = "user", roles = "USER")
    void create_new_order_without_body() throws Exception {
        mockMvc.perform(post("/api/user-board/order/save-order-changes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }
    @Test
    @WithMockUser(username = "user", roles = "USER")
    void create_new_order_error() throws Exception {
        CreateNewOrderDTO newOrderDTO = new CreateNewOrderDTO();
        newOrderDTO.setClientId(1L);
        newOrderDTO.setRealNeed("test need");
        newOrderDTO.setEstimateBudget(1234);

        doThrow(new SubjectNotBelongToActiveUser("It's not your Client. You don't have access to this Client."))
                .when(orderService).createNewOrder(any(CreateNewOrderDTO.class));

        mockMvc.perform(post("/api/user-board/order/create-new-order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newOrderDTO))
                        .with(csrf()))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message")
                   .value("Order controller: It's not your Client. You don't have access to this Client."));
    }

    static {
        Client exampleClient = new Client("Test name", "test@gmail.com",
                "55555", "Poland", null);
        exampleClient.setClientId(1L);

        ItemForAdditionalPurchases itemOne = new ItemForAdditionalPurchases();
        itemOne.setItemName("thingOne");
        itemOne.setQuantity(1);
        itemOne.setUnitPrice(5);
        itemOne.setTotalPrice(5.5);
        ItemForAdditionalPurchases itemTwo = new ItemForAdditionalPurchases();
        itemTwo.setItemName("thingTwo");
        itemTwo.setQuantity(3);
        itemTwo.setUnitPrice(10);
        itemTwo.setTotalPrice(33);

        Order orderExample = new Order();
        orderExample.setOrderId(1L);
        orderExample.setRealNeed("Test need");
        orderExample.setAddress("Poland");
        orderExample.setClient(exampleClient);
        orderExample.setAdditionalPurchases(Set.of(itemOne, itemTwo));
        orderExample.setResultPrice(38.5);

        expectedDTO = new OrderInfoDTO(orderExample);
        expectedItemsForAdditionalPurchasesDTO = new ItemsForAdditionalPurchasesDTO();
        expectedItemsForAdditionalPurchasesDTO.setItems(orderExample.getAdditionalPurchases());
        expectedItemsForAdditionalPurchasesDTO.setResultPrice(orderExample.getResultPrice());
    }
}