package com.crm.system.services.impl;

import com.crm.system.exception.ClientAlreadyExistException;
import com.crm.system.exception.RequestOptionalIsEmpty;
import com.crm.system.models.Client;
import com.crm.system.models.ClientStatus;
import com.crm.system.models.order.Order;
import com.crm.system.playload.request.AddClientDTO;
import com.crm.system.playload.request.EditClientDataDTO;
import com.crm.system.playload.response.ClientInfoDTO;
import com.crm.system.repository.ClientRepository;
import com.crm.system.services.ClientService;
import com.crm.system.services.UserService;
import com.crm.system.services.utils.clientUtils.ClientInfoDTOMapper;
import com.crm.system.services.utils.logUtils.facadeForLogEntry.EntryType;
import com.crm.system.services.utils.logUtils.facadeForLogEntry.LogEntryForClientFacade;
import com.crm.system.services.utils.logUtils.decoratorsForLogEntry.MarkAsDoneDecorator;
import com.crm.system.services.utils.logUtils.decoratorsForLogEntry.MarkAsImportantDecorator;
import org.springframework.stereotype.Service;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.time.LocalDateTime;
import java.util.Set;

@Service
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;
    private final UserService userService;
    private final LogEntryForClientFacade logEntryFacade;
    private final ClientInfoDTOMapper clientInfoDTOMapper;

    public ClientServiceImpl(ClientRepository clientRepository,
                             UserService userService,
                             LogEntryForClientFacade logEntryFacade,
                             ClientInfoDTOMapper clientInfoDTOMapper) {
        this.clientRepository = clientRepository;
        this.userService = userService;
        this.logEntryFacade = logEntryFacade;
        this.clientInfoDTOMapper = clientInfoDTOMapper;
    }


    @Override
    public Set<ClientInfoDTO> getClientsWithClientStatusForUser() {

        Set<Client> clients = clientRepository.getClientsWithClientStatusForUser(userService.getActiveUserId());
        return clientInfoDTOMapper.mapClientsToClientInfoDTOWithNumberPaidOrders(clients);
    }


    public Set<ClientInfoDTO> getClientsWithLeadStatusForUser() {

        Set<Client> clients = clientRepository.getClientsWithLeadStatusForUser(userService.getActiveUserId());
        return clientInfoDTOMapper.mapClientsToClientInfoDTO(clients);
    }


    public Set<ClientInfoDTO> getClientsWithBlacklistStatusForUser() {
        Set<Client> clients = clientRepository.getClientsWithBlackListStatusForUser(userService.getActiveUserId());
        return clientInfoDTOMapper.mapClientsToClientInfoDTO(clients);
    }


    public long addNewLead(AddClientDTO addClientDTO) throws UserPrincipalNotFoundException {

        checkIfThereIsClientWithThisEmail(addClientDTO.getEmail());

        Client savedClient = clientRepository.save(
                new Client(addClientDTO, userService.getActiveUser()));

        logEntryFacade.createAndSaveMessage(
                savedClient,
                EntryType.ADD_CLIENT,
                new MarkAsImportantDecorator(), new MarkAsDoneDecorator());

        return savedClient.getClientId();
    }


    public void sentToBlackList(long clientId) throws UserPrincipalNotFoundException {

        Client client = getClientByIdForActualUser(clientId);

        clientRepository.updateClientStatusAndDateOfLastChange(clientId, ClientStatus.BLACKLIST, LocalDateTime.now());

        logEntryFacade.createAndSaveMessage(client,
                EntryType.SENT_CLIENT_TO_BLACKLIST,
                new MarkAsImportantDecorator(), new MarkAsDoneDecorator());
    }



    public void restoreClientFromBlackList(long clientId) throws UserPrincipalNotFoundException {

        Client client = getClientByIdForActualUser(clientId);

        ClientStatus status = hasPaidOrders(client) ? ClientStatus.CLIENT : ClientStatus.LEAD;
        clientRepository.updateClientStatusAndDateOfLastChange(clientId, status, LocalDateTime.now());

        logEntryFacade.createAndSaveMessage(client,
                EntryType.RESTORE_CLIENT_FROM_BLACKLIST,
                new MarkAsImportantDecorator(), new MarkAsDoneDecorator());
    }


    public Client getInfoWithOrdersClient(long clientId) {
        Client client = getClientByIdForActualUser(clientId);

        client.getOrders()
                .forEach(order -> {
                    order.setAdditionalPurchases(null);
                    order.setClient(null);
                });
        return client;
    }


    public void editClientData(EditClientDataDTO request) {

        Client client = getClientByIdForActualUser(request.getClientId());
        client.editClientData(request);

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


    private void checkIfThereIsClientWithThisEmail(String email) {
        if (clientRepository.existsByEmail(email)) {
            throw new ClientAlreadyExistException("Client with this email already exists");
        }
    }
}
