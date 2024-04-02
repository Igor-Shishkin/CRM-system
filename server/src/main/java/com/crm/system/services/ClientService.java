package com.crm.system.services;

import com.crm.system.exception.ClientAlreadyExistException;
import com.crm.system.exception.NameOrEmailIsEmptyException;
import com.crm.system.exception.RequestOptionalIsEmpty;
import com.crm.system.exception.SubjectNotBelongToActiveUser;
import com.crm.system.models.Client;
import com.crm.system.models.ClientStatus;
import com.crm.system.models.User;
import com.crm.system.models.history.HistoryMessage;
import com.crm.system.models.history.TagName;
import com.crm.system.models.order.Order;
import com.crm.system.playload.request.AddLeadDTO;
import com.crm.system.playload.request.EditClientDataDTO;
import com.crm.system.playload.response.ClientInfoDTO;
import com.crm.system.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ClientService {
    private final ClientRepository clientRepository;
    private final UserService userService;
    private final HistoryMessageService historyMessageService;

    public ClientService(ClientRepository clientRepository, UserService userService,
                         HistoryMessageService historyMessageService) {
        this.clientRepository = clientRepository;
        this.userService = userService;
        this.historyMessageService = historyMessageService;
    }

    public Set<ClientInfoDTO> getClientsForUser() throws UserPrincipalNotFoundException {
        User user = userService.getActiveUser();

        return user.getClients().stream()
                .filter(client -> client.getStatus().equals(ClientStatus.CLIENT))
                .map(client -> {
                    int numberOfPaidOrders = (int) client.getOrders().stream()
                            .filter(Order::isHasBeenPaid)
                            .count();
                    return new ClientInfoDTO.Builder()
                            .withId(client.getClientId())
                            .withFullName(client.getFullName())
                            .withAddress(client.getAddress())
                            .withEmail(client.getEmail())
                            .withPhoneNumber(client.getPhoneNumber())
                            .withIsClient(ClientStatus.CLIENT)
                            .withNumberOfOrders(client.getOrders().size())
                            .withNumberOfPaidOrders(numberOfPaidOrders)
                            .build();
                })
                .collect(Collectors.toSet());
    }

    public List<ClientInfoDTO> getLeadsForUser() throws UserPrincipalNotFoundException {
        User user = userService.getActiveUser();

        return user.getClients().stream()
                .filter(client -> client.getStatus().equals(ClientStatus.LEAD))
                .map(client -> new ClientInfoDTO.Builder()
                        .withId(client.getClientId())
                        .withFullName(client.getFullName())
                        .withAddress(client.getAddress())
                        .withEmail(client.getEmail())
                        .withPhoneNumber(client.getPhoneNumber())
                        .withIsClient(ClientStatus.LEAD)
                        .withNumberOfOrders(client.getOrders().size())
                        .build())
                .collect(Collectors.toList());
    }

    public List<ClientInfoDTO> getBlackListClientsForUser() throws UserPrincipalNotFoundException {
        User user = userService.getActiveUser();
        return user.getClients().stream()
                .filter(client -> client.getStatus().equals(ClientStatus.BLACKLIST))
                .map(client -> new ClientInfoDTO.Builder()
                        .withId(client.getClientId())
                        .withFullName(client.getFullName())
                        .withAddress(client.getAddress())
                        .withEmail(client.getEmail())
                        .withPhoneNumber(client.getPhoneNumber())
                        .withIsClient(ClientStatus.BLACKLIST)
                        .withNumberOfOrders(client.getOrders().size())
                        .build())
                .collect(Collectors.toList());
    }

    public long addNewLead(AddLeadDTO addLeadDTO) throws UserPrincipalNotFoundException {
        if (clientRepository.existsByEmail(addLeadDTO.getEmail())) {
            throw new ClientAlreadyExistException("Lid with this email already exists");
        }
        User activeUser = userService.getActiveUser();
        Client savedLead = clientRepository.save(new Client(
                addLeadDTO.getFullName(),
                addLeadDTO.getEmail(),
                addLeadDTO.getPhoneNumber(),
                addLeadDTO.getAddress(),
                activeUser));

        String messageText = String.format("Lead %s is created", savedLead.getFullName());
        historyMessageService.automaticallyCreateMessage(new HistoryMessage.Builder()
                .withMessageText(messageText)
                .withIsDone(true)
                .withIsImportant(true)
                .withTagName(TagName.CLIENT)
                .withTagId(savedLead.getClientId())
                .withUser(activeUser)
                .build());

        return savedLead.getClientId();
    }


    public void sentToBlackList(long clientId) throws SubjectNotBelongToActiveUser {

        Client client = getClientByIdForActualUser(clientId);

        client.setStatus(ClientStatus.BLACKLIST);
        client.setDateOfLastChange(LocalDateTime.now());
        clientRepository.save(client);

        String messageText = String.format("Client %s goes to blackList", client.getFullName());
        historyMessageService.automaticallyCreateMessage(new HistoryMessage.Builder()
                .withMessageText(messageText)
                .withIsDone(true)
                .withIsImportant(true)
                .withTagName(TagName.CLIENT)
                .withTagId(clientId)
                .withUser(client.getUser())
                .build());
    }

    public void restoreClientFromBlackList(long clientId) throws SubjectNotBelongToActiveUser {

        Client client = getClientByIdForActualUser(clientId);

        client.setStatus(
                (hasPaidOrders(client)) ? ClientStatus.CLIENT : ClientStatus.LEAD);

        client.setDateOfLastChange(LocalDateTime.now());
        clientRepository.save(client);

        String messageText = String.format("Client %s is restored from blackList", client.getFullName());
        historyMessageService.automaticallyCreateMessage(new HistoryMessage.Builder()
                .withMessageText(messageText)
                .withIsDone(true)
                .withIsImportant(true)
                .withTagName(TagName.CLIENT)
                .withTagId(clientId)
                .withUser(client.getUser())
                .build());
    }


    public Client getClient(long clientId) {
        Client client = getClientByIdForActualUser(clientId);

        client.getOrders()
                .forEach(order -> {
                    order.setAdditionalPurchases(null);
                    order.setClient(null);
                });
        return client;

    }

    public void editClientData(EditClientDataDTO request) {
        if (request.getFullName().isBlank() ||
                request.getEmail().isBlank()) {
            throw new NameOrEmailIsEmptyException("Name and email can't be empty!");
        }

        Client client = getClientByIdForActualUser(request.getClientId());

        client.setFullName(request.getFullName());
        client.setEmail(request.getEmail());
        client.setPhoneNumber(request.getPhoneNumber());
        client.setAddress(request.getAddress());
        client.setDateOfLastChange(LocalDateTime.now());

        clientRepository.save(client);
    }

    public Client getClientByIdForActualUser(long clientId) {

        return clientRepository.findClientByClientIdAndUserId(userService.getActiveUserId(), clientId)
                .orElseThrow(() -> new RequestOptionalIsEmpty
                        (String.format("You do not have a client with %d ID", clientId)));
    }

    public void saveClient(Client client) {
        clientRepository.save(client);
    }

    private boolean hasPaidOrders(Client client) {
        return client.getOrders().stream()
                .anyMatch(Order::isHasBeenPaid);
    }
}
