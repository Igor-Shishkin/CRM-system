package com.crm.system.services.impl;

import com.crm.system.exception.MismanagementOfTheClientException;
import com.crm.system.exception.RequestOptionalIsEmpty;
import com.crm.system.models.Client;
import com.crm.system.models.ClientStatus;
import com.crm.system.models.logForUser.LogEntry;
import com.crm.system.models.logForUser.TagName;
import com.crm.system.models.order.InfoIsShown;
import com.crm.system.models.order.ItemForAdditionalPurchases;
import com.crm.system.models.order.Order;
import com.crm.system.playload.request.ChangeOrderDTO;
import com.crm.system.playload.request.CreateNewOrderDTO;
import com.crm.system.playload.response.ItemsForAdditionalPurchasesDTO;
import com.crm.system.playload.response.OrderInfoDTO;
import com.crm.system.repository.OrderRepository;
import com.crm.system.services.ClientService;
import com.crm.system.services.LogEntryService;
import com.crm.system.services.OrderService;
import com.crm.system.services.UserService;
import com.crm.system.services.utils.orderUtils.OrderProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.function.Predicate;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final UserService userService;
    private final LogEntryService logEntryService;
    private final ClientService clientService;
    private final OrderProcessor orderProcessor;


    public OrderServiceImpl(OrderRepository orderRepository, UserService userService, LogEntryService logEntryService, ClientService clientService, OrderProcessor orderProcessor) {
        this.orderRepository = orderRepository;
        this.userService = userService;
        this.logEntryService = logEntryService;
        this.clientService = clientService;
        this.orderProcessor = orderProcessor;
    }


    public OrderInfoDTO getOrderInfoResponse(long orderId) {
        Order order = getOrderById(orderId);
        return new OrderInfoDTO(order);
    }


    public Order getOrder(long orderId) {
        return getOrderById(orderId);
    }

    public ItemsForAdditionalPurchasesDTO getAdditionalPurchases(long orderId) {
        Order order = getOrderById(orderId);
        ItemsForAdditionalPurchasesDTO calculations = new ItemsForAdditionalPurchasesDTO();
        calculations.setItems(order.getAdditionalPurchases());
        calculations.setResultPrice(order.getResultPrice());
        return calculations;
    }


    public void changeOrder(Order order) {
        orderRepository.save(order);
        saveDateOfLastChangeForClient(order.getClient());
    }


    public void signAgreement(long orderId) {
        Order order = getOrderById(orderId);
        orderProcessor.signAgreementForOrder(order);
        orderRepository.save(order);

        createNewHistoryMessage(order.getClient(),
                "Signed an agreement with ".concat(order.getClient().getFullName()));

        saveDateOfLastChangeForClient(order.getClient());
    }


    public void cancelAgreement(long orderId) {
        Order order = getOrderById(orderId);
        orderProcessor.canselAgreement(order);
        orderRepository.save(order);

        createNewHistoryMessage(order.getClient(),
                String.format("The contract with %s was canceled", order.getClient().getFullName()));

        saveDateOfLastChangeForClient(order.getClient());
    }


    public void confirmPayment(long orderId) {
        Order order = getOrderById(orderId);
        orderProcessor.confirmPayment(order);
        orderRepository.save(order);

        String historyMessage = String.format("'You have confirmed payment by %s", order.getClient().getFullName());
        createNewHistoryMessage(order.getClient(), historyMessage);

        saveDateOfLastChangeForClient(order.getClient());
    }


    public void cancelPayment(long orderId) {
        Order order = getOrderById(orderId);
        orderProcessor.cancelPayment(order);
        orderRepository.save(order);

        saveDateOfLastChangeForClient(order.getClient());

        createNewHistoryMessage(order.getClient(),
                String.format("'You have canceled payment by %s", order.getClient().getFullName()));
    }


    public void saveOrderChanges(ChangeOrderDTO changedOrder) {
        Order order = getOrderById(changedOrder.getOrderId());
        orderProcessor.setChangedParameters(order, changedOrder);
        order.setDateOfLastChange(LocalDateTime.now());
        orderRepository.save(order);

        saveDateOfLastChangeForClient(order.getClient());
    }


    public long createNewOrder(CreateNewOrderDTO request) {
        Client currentClient = getClientById(request.getClientId());
        Order newOrder = new Order(request.getRealNeed(), request.getEstimateBudget(), currentClient);
        Order savedOrder = orderRepository.save(newOrder);

        saveDateOfLastChangeForClient(currentClient);

        createNewHistoryMessage(currentClient,
                String.format("You have a new order from %s client. Congratulations!", currentClient.getFullName()));

        return savedOrder.getOrderId();
    }


    private Client getClientById(long clientId) {
        return clientService.getClientByIdForActualUser(clientId);
    }

    private Order getOrderById(long orderId) {
        long activeUserId = userService.getActiveUserId();
        return orderRepository.getOrderByOrderIdAndUserId(orderId, activeUserId)
                .orElseThrow(() -> new RequestOptionalIsEmpty("You don't have order with this ID"));
    }

    private void createNewHistoryMessage(Client client, String textMessage) {
        logEntryService.automaticallyCreateMessage(new LogEntry.Builder()
                .withText(textMessage)
                .withIsDone(true)
                .withIsImportant(true)
                .withTagName(TagName.CLIENT)
                .withTagId(client.getClientId())
                .withUser(client.getUser())
                .build());
    }

    private void saveDateOfLastChangeForClient(Client client) {
        client.setDateOfLastChange(LocalDateTime.now());
        clientService.saveClient(client);
    }

}
