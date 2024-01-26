package com.crm.system.services;

import com.crm.system.exception.MismanagementOfTheClientException;
import com.crm.system.exception.RequestOptionalIsEmpty;
import com.crm.system.exception.SubjectNotBelongToActiveUser;
import com.crm.system.models.Client;
import com.crm.system.models.ClientStatus;
import com.crm.system.models.User;
import com.crm.system.models.order.InfoIsShown;
import com.crm.system.models.order.ItemForCalcualtion;
import com.crm.system.models.order.Order;
import com.crm.system.playload.request.ChangeOrderDTO;
import com.crm.system.playload.request.CreateNewOrderDTO;
import com.crm.system.playload.response.NewCalculationsForOrderDTO;
import com.crm.system.playload.response.OrderInfoDTO;
import com.crm.system.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Predicate;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserService userService;
    private final HistoryMessageService historyMessageService;
    private final ClientService clientService;

    public OrderService(OrderRepository orderRepository, UserService userService, HistoryMessageService historyMessageService, ClientService clientService) {
        this.orderRepository = orderRepository;
        this.userService = userService;
        this.historyMessageService = historyMessageService;
        this.clientService = clientService;
    }


    public OrderInfoDTO getOrderInfoResponce(long orderId) throws UserPrincipalNotFoundException {
        Order order = getOrderById(orderId);
        OrderInfoDTO orderInfoDTO = new OrderInfoDTO(order);
        return orderInfoDTO;
    }


    public Order getOrder(long orderId) throws UserPrincipalNotFoundException {
        Order order = getOrderById(orderId);
        return order;
    }

    public NewCalculationsForOrderDTO getNewCalculations(long orderId) throws UserPrincipalNotFoundException {
        Order order = getOrderById(orderId);
        NewCalculationsForOrderDTO newCalculations = new NewCalculationsForOrderDTO();
        newCalculations.setItems(order.getCalculations());
        newCalculations.setResultPrice(order.getResultPrice());
        return newCalculations;
    }

    public void changeOrder(Order order) {
        orderRepository.save(order);
    }

    public void signAgreement(long orderId) throws UserPrincipalNotFoundException {
        Order order = getOrderById(orderId);
        if (order.isAgreementSigned()) {
            return;
        }

        if (checkIfCalculationIsRight(order) && checkIfCalculationIsShownToClient(order)) {
            order.setAgreementSigned(true);
            order.setAgreementPrepared(true);
            order.setDateOfLastChange(LocalDateTime.now());
            orderRepository.save(order);

            createNewHistoryMessage(order.getClient(),
                    "Signed an agreement with ".concat(order.getClient().getFullName()));
        } else {
            throw new MismanagementOfTheClientException
                    ("To sign the contract, you must fill out the calculations correctly.");
        }
    }

    public void cancelAgreement(long orderId) throws UserPrincipalNotFoundException {
        Order order = getOrderById(orderId);
        if (!order.isAgreementSigned()) {
            return;
        }

        if (!order.isHasBeenPaid()) {
            order.setAgreementSigned(false);
            order.setDateOfLastChange(LocalDateTime.now());
            orderRepository.save(order);

            createNewHistoryMessage(order.getClient(),
                    String.format("The contract with %s was canceled", order.getClient().getFullName()));
        } else {
            throw new MismanagementOfTheClientException("Order is already paid");
        }
    }

    public void confirmPayment(long orderId) throws UserPrincipalNotFoundException {
        Order order = getOrderById(orderId);
        if (order.isHasBeenPaid()) {
            return;
        }
        if (order.isAgreementSigned()) {
            order.setHasBeenPaid(true);
            order.setDateOfLastChange(LocalDateTime.now());
            order.getClient().setStatus(ClientStatus.CLIENT);
            order.getClient().setDateOfLastChange(LocalDateTime.now());
            orderRepository.save(order);

            historyMessageService.createHistoryMessageForClientWithNote(order.getClient(), order.getClient().getUser(),
                    String.format("'You have confirmed payment by %s", order.getClient().getFullName()),
                    "Congratulations on the successful completion of your order!");
        } else {
            throw new MismanagementOfTheClientException("Agreement is not signed");
        }
    }

    public void cancelPayment(long orderId) throws UserPrincipalNotFoundException {
        Order order = getOrderById(orderId);
        if (!order.isHasBeenPaid()) {
            return;
        }
        setClientStatus(order);
        order.setHasBeenPaid(false);
        order.setDateOfLastChange(LocalDateTime.now());
        order.getClient().setDateOfLastChange(LocalDateTime.now());

        createNewHistoryMessage(order.getClient(),
                String.format("'You have canceled payment by %s", order.getClient().getFullName()));

    }



    public void saveOrderChanges(ChangeOrderDTO changedOrder) throws UserPrincipalNotFoundException {
        Order order = getOrderById(changedOrder.getOrderId());
        setChangedParameters(order, changedOrder);
        order.setDateOfLastChange(LocalDateTime.now());

        orderRepository.save(order);
    }
    public long createNewOrder(CreateNewOrderDTO request) throws UserPrincipalNotFoundException {
        Client currentClient = getClientById(request.getClientId());
        Order newOrder = new Order(request.getRealNeed(), request.getEstimateBudget(), currentClient);
        Order savedOrder = orderRepository.save(newOrder);

        createNewHistoryMessage(currentClient,
                String.format("You have a new order from %s client. Congratulations!", currentClient.getFullName()));
        return savedOrder.getOrderId();
    }

    private Client getClientById(long clientId) throws UserPrincipalNotFoundException {
        Client currentClient = clientService.getClientById(clientId);
        return currentClient;
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
     if ( order.getClient().getOrders().stream()
             .noneMatch(Order::isHasBeenPaid) )  {
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
        Predicate<ItemForCalcualtion> isValidItem = item ->
                item.getThing() != null && !item.getThing().isEmpty() &&
                        item.getUnitPrice() > 0 &&
                        item.getTotalPrice() > 0 &&
                        item.getQuantity() > 0;
        boolean isValidItems = order.getCalculations().stream().allMatch(isValidItem);
        return isValidItems && order.getResultPrice() > 0;
    }

    private Order getOrderById(long orderId) throws UserPrincipalNotFoundException {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isEmpty()) {
            throw new RequestOptionalIsEmpty("There isn't order with this ID");
        }
        Order order = optionalOrder.get();
        if (isOrderBelongsActiveUser(order)) {
            return order;
        } else {
            throw new SubjectNotBelongToActiveUser("It's not your order");
        }
    }

    private boolean isOrderBelongsActiveUser(Order order) throws UserPrincipalNotFoundException {
        Optional<User> optionalUser = userService.getActiveUser();
        if (optionalUser.isEmpty()) {
            throw new UserPrincipalNotFoundException("I can't found active user");
        }
        User activeUser = optionalUser.get();
        return activeUser.getClients().stream()
                .anyMatch(client -> client.getId().equals(order.getClient().getId()));
    }
    private void createNewHistoryMessage(Client client, String textMessage) {
        historyMessageService.createHistoryMessageForClient(client, textMessage);
    }

}
