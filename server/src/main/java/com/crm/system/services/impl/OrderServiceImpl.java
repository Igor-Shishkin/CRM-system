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
import com.crm.system.services.utils.logUtils.decoratorsForLogEntry.MarkAsDoneDecorator;
import com.crm.system.services.utils.logUtils.decoratorsForLogEntry.MarkAsImportantDecorator;
import com.crm.system.services.utils.logUtils.facadeForLogEntry.EntryType;
import com.crm.system.services.utils.logUtils.facadeForLogEntry.LogEntryFacade;
import com.crm.system.services.utils.logUtils.facadeForLogEntry.LogEntryForOrderFacade;
import com.crm.system.services.utils.orderUtils.OrderProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.time.LocalDateTime;
import java.util.function.Predicate;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final UserService userService;
    private final ClientService clientService;
    private final OrderProcessor orderProcessor;
    private final LogEntryForOrderFacade logFacade;


    public OrderServiceImpl(OrderRepository orderRepository,
                            UserService userService,
                            ClientService clientService,
                            OrderProcessor orderProcessor,
                            LogEntryForOrderFacade logFacade) {
        this.orderRepository = orderRepository;
        this.userService = userService;
        this.clientService = clientService;
        this.orderProcessor = orderProcessor;
        this.logFacade = logFacade;
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
        return new ItemsForAdditionalPurchasesDTO(order);
    }


    public void changeOrder(Order order) {
        orderRepository.save(order);
        saveDateOfLastChangeForClient(order.getClient());
    }


    public void signAgreement(long orderId) throws UserPrincipalNotFoundException {
        Order order = getOrderById(orderId);
        orderProcessor.signAgreementForOrder(order);
        orderRepository.save(order);

        logFacade.createAndSaveMessage(order,
                EntryType.SIGN_AGREEMENT_FOR_ORDER,
                new MarkAsDoneDecorator(), new MarkAsImportantDecorator());

        saveDateOfLastChangeForClient(order.getClient());
    }


    public void cancelAgreement(long orderId) throws UserPrincipalNotFoundException {
        Order order = getOrderById(orderId);
        orderProcessor.canselAgreement(order);
        orderRepository.save(order);

        logFacade.createAndSaveMessage(order,
                EntryType.CANCEL_AGREEMENT,
                new MarkAsDoneDecorator(), new MarkAsImportantDecorator());

        saveDateOfLastChangeForClient(order.getClient());
    }


    public void confirmPayment(long orderId) throws UserPrincipalNotFoundException {
        Order order = getOrderById(orderId);
        orderProcessor.confirmPayment(order);
        orderRepository.save(order);

        logFacade.createAndSaveMessage(order,
                EntryType.CONFIRM_AGREEMENT,
                new MarkAsDoneDecorator(), new MarkAsImportantDecorator());

        saveDateOfLastChangeForClient(order.getClient());
    }


    public void cancelPayment(long orderId) throws UserPrincipalNotFoundException {
        Order order = getOrderById(orderId);
        orderProcessor.cancelPayment(order);
        orderRepository.save(order);

        saveDateOfLastChangeForClient(order.getClient());

        logFacade.createAndSaveMessage(order,
                EntryType.CANCEL_PAYMENT,
                new MarkAsDoneDecorator(), new MarkAsImportantDecorator());
    }


    public void saveOrderChanges(ChangeOrderDTO changedOrder) {
        Order order = getOrderById(changedOrder.getOrderId());
        orderProcessor.setChangedParameters(order, changedOrder);
        order.setDateOfLastChange(LocalDateTime.now());
        orderRepository.save(order);

        saveDateOfLastChangeForClient(order.getClient());
    }


    public long createNewOrder(CreateNewOrderDTO request) throws UserPrincipalNotFoundException {
        Client currentClient = getClientById(request.getClientId());
        Order newOrder = new Order(request.getRealNeed(), request.getEstimateBudget(), currentClient);
        Order savedOrder = orderRepository.save(newOrder);

        saveDateOfLastChangeForClient(currentClient);

        logFacade.createAndSaveMessage(savedOrder,
                EntryType.CREATE_NEW_ORDER,
                new MarkAsDoneDecorator(), new MarkAsImportantDecorator());

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

    private void saveDateOfLastChangeForClient(Client client) {
        client.setDateOfLastChange(LocalDateTime.now());
        clientService.saveClient(client);
    }
}
