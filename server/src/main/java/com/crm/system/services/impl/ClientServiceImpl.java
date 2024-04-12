package com.crm.system.services.impl;

import com.crm.system.exception.ClientAlreadyExistException;
import com.crm.system.exception.NameOrEmailIsEmptyException;
import com.crm.system.exception.RequestOptionalIsEmpty;
import com.crm.system.exception.SubjectNotBelongToActiveUser;
import com.crm.system.models.Client;
import com.crm.system.models.ClientStatus;
import com.crm.system.models.User;
import com.crm.system.models.order.Order;
import com.crm.system.playload.request.AddLeadDTO;
import com.crm.system.playload.request.EditClientDataDTO;
import com.crm.system.playload.response.ClientInfoDTO;
import com.crm.system.repository.ClientRepository;
import com.crm.system.services.ClientService;
import com.crm.system.services.LogEntryService;
import com.crm.system.services.UserService;
import com.crm.system.services.utils.logUtils.EntryType;
import com.crm.system.services.utils.logUtils.LogEntryForClientFacade;
import com.crm.system.services.utils.logUtils.decoratorsForLogEntry.MarkAsDoneDecorator;
import com.crm.system.services.utils.logUtils.decoratorsForLogEntry.MarkAsImportantDecorator;
import org.springframework.stereotype.Service;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;
    private final UserService userService;
    private final LogEntryService logEntryService;

    private final LogEntryForClientFacade logEntryFacade;

    public ClientServiceImpl(ClientRepository clientRepository, UserService userService,
                             LogEntryService logEntryService, LogEntryForClientFacade logEntryFacade) {
        this.clientRepository = clientRepository;
        this.userService = userService;
        this.logEntryService = logEntryService;
        this.logEntryFacade = logEntryFacade;
    }
    @Override
    public Set<ClientInfoDTO> getClientsWithClientStatusForUser() {

        Set<Client> clients = clientRepository.getClientsWithClientStatusForUser(userService.getActiveUserId());

        return clients.stream()
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

    public List<ClientInfoDTO> getClientsWithLeadStatusForUser() {

        Set<Client> leads = clientRepository.getClientsWithLeadStatusForUser(userService.getActiveUserId());

        return leads.stream()
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

    public List<ClientInfoDTO> getClientsWithBlacklistStatusForUser() {
        Set<Client> clients = clientRepository.getClientsWithBlackListStatusForUser(userService.getActiveUserId());
        return clients.stream()
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
            throw new ClientAlreadyExistException("Client with this email already exists");
        }
        User activeUser = userService.getActiveUser();
        Client savedLead = clientRepository.save(new Client(
                addLeadDTO.getFullName(),
                addLeadDTO.getEmail(),
                addLeadDTO.getPhoneNumber(),
                addLeadDTO.getAddress(),
                activeUser));

        logEntryFacade.createAndSaveMessage(savedLead,
                EntryType.ADD_CLIENT,
                new MarkAsImportantDecorator(), new MarkAsDoneDecorator());

        return savedLead.getClientId();
    }


    public void sentToBlackList(long clientId) {

        Client client = getClientByIdForActualUser(clientId);

        client.setStatus(ClientStatus.BLACKLIST);
        client.setDateOfLastChange(LocalDateTime.now());
        clientRepository.save(client);

        logEntryFacade.createAndSaveMessage(client,
                EntryType.SENT_CLIENT_TO_BLACKLIST,
                new MarkAsImportantDecorator(), new MarkAsDoneDecorator());

    }

    public void restoreClientFromBlackList(long clientId) throws SubjectNotBelongToActiveUser {

        Client client = getClientByIdForActualUser(clientId);

        client.setStatus(
                (hasPaidOrders(client)) ? ClientStatus.CLIENT : ClientStatus.LEAD);

        client.setDateOfLastChange(LocalDateTime.now());
        clientRepository.save(client);

        logEntryFacade.createAndSaveMessage(client,
                EntryType.RESTORE_CLIENT_FROM_BLACKLIST,
                new MarkAsImportantDecorator(), new MarkAsDoneDecorator());

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
            throw new NameOrEmailIsEmptyException("Name and email can't be empty");
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
                        (String.format("You do not have a client with ID=%d", clientId)));
    }

    public void saveClient(Client client) {
        clientRepository.save(client);
    }

    private boolean hasPaidOrders(Client client) {
        return client.getOrders().stream()
                .anyMatch(Order::isHasBeenPaid);
    }
}
