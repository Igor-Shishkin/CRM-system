package com.crm.system.services;

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
import com.crm.system.repository.UserRepository;
import com.crm.system.security.services.UserDetailsImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ClientService {
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final HistoryMessageService historyMessageService;

    public ClientService(ClientRepository clientRepository, UserRepository userRepository, HistoryMessageService historyMessageService) {
        this.clientRepository = clientRepository;
        this.userRepository = userRepository;
        this.historyMessageService = historyMessageService;
    }

    public Set<ClientInfoDTO> getClientsForUser() throws UserPrincipalNotFoundException {
        User user = getActiveUser();

        Set<ClientInfoDTO> infoClientDTO  = user.getClients().stream()
                .map(client -> {
                    int numberOfPaidOrders = (int) client.getOrders().stream()
                            .filter(Order::isHasBeenPaid)
                            .count();
                    return new ClientInfoDTO.Builder()
                            .withId (client.getClientId())
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

        return infoClientDTO;
    }

    public List<ClientInfoDTO> getLeadsForUser() throws UserPrincipalNotFoundException {
        User user = getActiveUser();

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
        User user = getActiveUser();
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
        User activeUser = getActiveUser();
        Client newLead = new Client(
                addLeadDTO.getFullName(),
                addLeadDTO.getEmail(),
                addLeadDTO.getPhoneNumber(),
                addLeadDTO.getAddress(),
                activeUser);

        String messageText = String.format("Lead %s is created", newLead.getFullName());
        Client savedLead = clientRepository.save(newLead);

        historyMessageService.createImportantHistoryMessageForClient(savedLead, messageText);

        return savedLead.getClientId();
    }

    public void sentToBlackList(long clientId) throws UserPrincipalNotFoundException, SubjectNotBelongToActiveUser {

        Client client = getClientById(clientId);

        client.setStatus(ClientStatus.BLACKLIST);
        client.setDateOfLastChange(LocalDateTime.now());
        clientRepository.save(client);

        String messageText = String.format("Client %s goes to blackList", client.getFullName());
        historyMessageService.createHistoryMessageForClient(client, messageText);
    }
    public void restoreClientFromBlackList(long clientId) throws UserPrincipalNotFoundException, SubjectNotBelongToActiveUser {

        Client client = getClientById(clientId);

        client.setStatus(
                (hasPaidOrders(client)) ? ClientStatus.CLIENT : ClientStatus.LEAD);

        client.setDateOfLastChange(LocalDateTime.now());
        clientRepository.save(client);

        String messageText = String.format("Client %s is restored from blackList", client.getFullName());
        historyMessageService.createHistoryMessageForClient(client, messageText);
    }




    public Client getClient(long clientId) throws UserPrincipalNotFoundException {
        Client client = getClientById(clientId);

        client.getOrders()
                .forEach(order -> {
                    order.setCalculations(null);
                    order.setClient(null);
                });
        return client;

    }
    public void editClientData(EditClientDataDTO request) throws UserPrincipalNotFoundException {
        if (request.getFullName().isBlank() ||
                request.getEmail().isBlank()) {
            throw new NameOrEmailIsEmptyException("Name and email can't be empty!");
        }

        Client client = getClientById(request.getClientId());

        client.setFullName(request.getFullName());
        client.setEmail(request.getEmail());
        client.setPhoneNumber(request.getPhoneNumber());
        client.setAddress(request.getAddress());
        client.setDateOfLastChange(LocalDateTime.now());

        clientRepository.save(client);
    }
    private User getActiveUser() throws UserPrincipalNotFoundException {

        User activeUser = userRepository.findById(getActiveUserId())
                .orElseThrow(() -> new UserPrincipalNotFoundException("User not found"));
        return activeUser;
    }

    public Client getClientById(long clientId) throws UserPrincipalNotFoundException {

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RequestOptionalIsEmpty
                        (String.format("Client with %d id doesn't exist", clientId)));

        if (isClientBelongsToActiveUser(client)){
            return client;
        } else {
            throw new SubjectNotBelongToActiveUser("It's not your Client. You don't have access to this Client.");
        }
    }

    public void saveClient(Client client) {
        clientRepository.save(client);
    }

    private long getActiveUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((UserDetailsImpl) authentication.getPrincipal()).getId();
        return userId;
    }

    private boolean isClientBelongsToActiveUser(Client client) throws UserPrincipalNotFoundException {

        Set<Long> allClientIdForUserById = clientRepository.findAllClientIdForUserById(getActiveUserId());
        return allClientIdForUserById.stream().anyMatch(id -> id.equals(client.getClientId()));
    }

    private boolean hasPaidOrders(Client client) {
        return client.getOrders().stream()
                .anyMatch(Order::isHasBeenPaid);
    }


}
