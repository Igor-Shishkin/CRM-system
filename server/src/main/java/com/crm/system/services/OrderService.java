package com.crm.system.services;

import com.crm.system.exception.MismanagementOfTheClientException;
import com.crm.system.exception.RequestOptionalIsEmpty;
import com.crm.system.models.Client;
import com.crm.system.models.ClientStatus;
import com.crm.system.models.history.HistoryMessage;
import com.crm.system.models.history.TagName;
import com.crm.system.models.order.InfoIsShown;
import com.crm.system.models.order.ItemForAdditionalPurchases;
import com.crm.system.models.order.Order;
import com.crm.system.playload.request.ChangeOrderDTO;
import com.crm.system.playload.request.CreateNewOrderDTO;
import com.crm.system.playload.response.ItemsForAdditionalPurchasesDTO;
import com.crm.system.playload.response.OrderInfoDTO;
import com.crm.system.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.function.Predicate;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserService userService;
    private final HistoryMessageService historyMessageService;
    private final ClientService clientService;
    @Value("${app.crm.price.coefficient}")
    private double PRICE_COEFFICIENT;

    public OrderService(OrderRepository orderRepository, UserService userService, HistoryMessageService historyMessageService, ClientService clientService) {
        this.orderRepository = orderRepository;
        this.userService = userService;
        this.historyMessageService = historyMessageService;
        this.clientService = clientService;
    }


    public OrderInfoDTO getOrderInfoResponce(long orderId) {
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

    public void signAgreement(long orderId){
        Order order = getOrderById(orderId);
        if (order.isAgreementSigned()) {
            throw new MismanagementOfTheClientException("Agreement is already signed");
        }

        if (checkIfCalculationIsRight(order) && checkIfCalculationIsShownToClient(order)) {
            order.setAgreementSigned(true);
            order.setAgreementPrepared(true);
            order.setDateOfLastChange(LocalDateTime.now());
            orderRepository.save(order);

            createNewHistoryMessage(order.getClient(),
                    "Signed an agreement with ".concat(order.getClient().getFullName()));

            saveDateOfLastChangeForClient(order.getClient());
        } else {
            throw new MismanagementOfTheClientException
                    ("To sign the contract, you must fill out the calculations correctly and show it to Client.");
        }
    }

    public void cancelAgreement(long orderId) {
        Order order = getOrderById(orderId);
        if (!order.isAgreementSigned()) {
            throw new MismanagementOfTheClientException("Agreement isn't signed");
        }

        if (!order.isHasBeenPaid()) {
            order.setAgreementSigned(false);
            order.setDateOfLastChange(LocalDateTime.now());
            orderRepository.save(order);

            createNewHistoryMessage(order.getClient(),
                    String.format("The contract with %s was canceled", order.getClient().getFullName()));

            saveDateOfLastChangeForClient(order.getClient());
        } else {
            throw new MismanagementOfTheClientException("Order is already paid");
        }
    }

    public void confirmPayment(long orderId){
        Order order = getOrderById(orderId);
        if (order.isHasBeenPaid()) {
            throw new MismanagementOfTheClientException("payment was already made");
        }
        if (order.isAgreementSigned()) {
            order.setHasBeenPaid(true);
            order.setDateOfLastChange(LocalDateTime.now());
            order.getClient().setStatus(ClientStatus.CLIENT);
            order.getClient().setDateOfLastChange(LocalDateTime.now());
            orderRepository.save(order);


            String historyMessage = String.format("'You have confirmed payment by %s", order.getClient().getFullName());
            createNewHistoryMessage(order.getClient(), historyMessage);

            saveDateOfLastChangeForClient(order.getClient());
        } else {
            throw new MismanagementOfTheClientException("Agreement is not signed");
        }
    }

    public void cancelPayment(long orderId){
        Order order = getOrderById(orderId);
        if (!order.isHasBeenPaid()) {
            throw new MismanagementOfTheClientException("Payment was not made");
        }
        setClientStatus(order);
        order.setHasBeenPaid(false);
        order.setDateOfLastChange(LocalDateTime.now());
        order.getClient().setDateOfLastChange(LocalDateTime.now());
        orderRepository.save(order);

        createNewHistoryMessage(order.getClient(),
                String.format("'You have canceled payment by %s", order.getClient().getFullName()));

        saveDateOfLastChangeForClient(order.getClient());
    }



    public void saveOrderChanges(ChangeOrderDTO changedOrder) {
        Order order = getOrderById(changedOrder.getOrderId());
        setChangedParameters(order, changedOrder);
        order.setDateOfLastChange(LocalDateTime.now());

        orderRepository.save(order);

        saveDateOfLastChangeForClient(order.getClient());
    }

    public long createNewOrder(CreateNewOrderDTO request) {
        Client currentClient = getClientById(request.getClientId());
        Order newOrder = new Order(request.getRealNeed(), request.getEstimateBudget(), currentClient);
        Order savedOrder = orderRepository.save(newOrder);

        createNewHistoryMessage(currentClient,
                String.format("You have a new order from %s client. Congratulations!", currentClient.getFullName()));

        saveDateOfLastChangeForClient(currentClient);
        return savedOrder.getOrderId();
    }

    private Client getClientById(long clientId)  {
        return clientService.getClientByIdForActualUser(clientId);
    }

    private void setChangedParameters(Order order, ChangeOrderDTO changedOrder) {
        order.setIsCalculationShown(changedOrder.getIsCalculationShown());
        order.setAgreementPrepared(changedOrder.isAgreementPrepared());
        order.setAddress(changedOrder.getAddress());
        order.setRealNeed(changedOrder.getRealNeed());
        order.setCalculationPromised(changedOrder.isCalculationPromised());
        order.setEstimateBudged(changedOrder.getEstimateBudged());
        order.setIsProjectShown(changedOrder.getIsProjectShown());
        order.setMeasurementOffered(changedOrder.isMeasurementOffered());
        order.setMeasurementsTaken(changedOrder.isMeasurementsTaken());
        order.setProjectApproved(changedOrder.isProjectApproved());
        order.setWasMeetingInOffice(changedOrder.isWasMeetingInOffice());
    }

    private void setClientStatus(Order order) {

     if ( order.getClient().getOrders().stream().noneMatch(Order::isHasBeenPaid) )  {
         order.getClient().setStatus(ClientStatus.CLIENT);
     } else {
         order.getClient().setStatus(ClientStatus.LEAD);
     }
    }
    private boolean checkIfCalculationIsShownToClient(Order order) {
        if (!order.getIsCalculationShown().equals(InfoIsShown.NOT_SHOWN)) {
            return true;
        }
        throw new MismanagementOfTheClientException("You must show calculation to the client");
    }

    private boolean checkIfCalculationIsRight(Order order) {

        Predicate<ItemForAdditionalPurchases> isValidItem = item ->
                item.getItemName() != null && !item.getItemName().isEmpty() &&
                        item.getUnitPrice() > 0 &&
                        item.getTotalPrice() > 0 &&
                        item.getQuantity() > 0 &&
                        item.getTotalPrice() == item.getUnitPrice()*item.getQuantity()*PRICE_COEFFICIENT;

        boolean isValidItems = order.getAdditionalPurchases().stream().allMatch(isValidItem);
        boolean isResultPriceRight = order.getResultPrice() == order.getAdditionalPurchases().stream()
                .mapToDouble(ItemForAdditionalPurchases::getTotalPrice)
                .sum();
        return isValidItems && isResultPriceRight;

    }

    private Order getOrderById(long orderId) {
        long activeUserId = userService.getActiveUserId();
        return orderRepository.getOrderByOrderIdAndUserId(orderId, activeUserId)
                .orElseThrow(() -> new RequestOptionalIsEmpty("You don't have order with this ID"));
    }
    private void createNewHistoryMessage(Client client, String textMessage) {
        historyMessageService.automaticallyCreateMessage(new HistoryMessage.Builder()
                .withMessageText(textMessage)
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
