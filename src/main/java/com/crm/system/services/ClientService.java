package com.crm.system.services;

import com.crm.system.exception.ClientAlreadyExistException;
import com.crm.system.exception.NameOrEmailIsEmptyException;
import com.crm.system.exception.RequestOptionalIsEmpty;
import com.crm.system.exception.SubjectNotBelongToActiveUser;
import com.crm.system.models.Client;
import com.crm.system.models.ClientStatus;
import com.crm.system.models.User;
import com.crm.system.models.order.Order;
import com.crm.system.playload.request.AddLeadRequest;
import com.crm.system.playload.request.EditClientDataRequest;
import com.crm.system.playload.response.ClientInfoResponse;
import com.crm.system.repository.ClientRepository;
import com.crm.system.repository.UserRepository;
import com.crm.system.security.services.UserDetailsImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    public List<ClientInfoResponse> getAllClients() throws UserPrincipalNotFoundException {
        User user = getActiveUser();
        List<ClientInfoResponse> infoClientResponses = new ArrayList<>(user.getClients().size() * 2);
        for (Client client : user.getClients()) {
            if (client.getStatus().equals(ClientStatus.CLIENT)) {
                int numberOfPaidOrders = (int) client.getOrders().stream()
                        .filter(Order::isHasBeenPaid)
                        .count();
                ClientInfoResponse clientInfoResponse = new ClientInfoResponse.Builder()
                        .withId (client.getId())
                        .withFullName(client.getFullName())
                        .withAddress(client.getAddress())
                        .withEmail(client.getEmail())
                        .withPhoneNumber(client.getPhoneNumber())
                        .withIsClient(ClientStatus.CLIENT)
                        .withNmberOfOrders(client.getOrders().size())
                        .withNumberOfPaidOrders(numberOfPaidOrders)
                        .build();
                infoClientResponses.add(clientInfoResponse);
            }
        }
        return infoClientResponses;
    }

    public List<ClientInfoResponse> getAllLeads() throws UserPrincipalNotFoundException {
        User user = getActiveUser();
        return user.getClients().stream()
                .filter(client -> client.getStatus().equals(ClientStatus.LEAD))
                .map(client -> new ClientInfoResponse.Builder()
                        .withId(client.getId())
                        .withFullName(client.getFullName())
                        .withAddress(client.getAddress())
                        .withEmail(client.getEmail())
                        .withPhoneNumber(client.getPhoneNumber())
                        .withIsClient(ClientStatus.LEAD)
                        .withNmberOfOrders(client.getOrders().size())
                        .build())
                .collect(Collectors.toList());
    }

    public long addNewLead(AddLeadRequest addLeadRequest) throws UserPrincipalNotFoundException {
        if (clientRepository.existsByEmail(addLeadRequest.getEmail())) {
            throw new ClientAlreadyExistException("Lid with this email already exists");
        }
        User activeUser = getActiveUser();
        Client newLead = new Client(
                addLeadRequest.getFullName(),
                addLeadRequest.getEmail(),
                addLeadRequest.getPhoneNumber(),
                addLeadRequest.getAddress(),
                activeUser);

        String messageText = String.format("Lead %s is created", newLead.getFullName());
        Client savedLead = clientRepository.save(newLead);

        historyMessageService.createHistoryMessageForClient(savedLead, messageText);

        return savedLead.getId();
    }

    public void sentToBlackList(long clientId) throws UserPrincipalNotFoundException, SubjectNotBelongToActiveUser {

        Client client = getClientById(clientId);

        client.setStatus(ClientStatus.BLACKLIST);
        clientRepository.save(client);

        String messageText = String.format("Client %s goes to blackList", client.getFullName());
        historyMessageService.createHistoryMessageForClient(client, messageText);
    }



    public Client getClient(long clientId) throws UserPrincipalNotFoundException {
        Client client = getClientById(clientId);

        for (Order order: client.getOrders()) {
            order.setProjectPhotos(null);
            order.setCalculations(null);
            order.setClient(null);
        }
        return client;
    }
    public void editClientData(EditClientDataRequest request) throws UserPrincipalNotFoundException {
        if (request.getFullName().isBlank() ||
                request.getEmail().isBlank()) {
            throw new NameOrEmailIsEmptyException("Name and email can't be empty!");
        }

        Client client = getClientById(request.getClientId());

        client.setFullName(request.getFullName());
        client.setEmail(request.getEmail());
        client.setPhoneNumber(request.getPhoneNumber());
        client.setAddress(request.getAddress());

        clientRepository.save(client);
    }
    private User getActiveUser() throws UserPrincipalNotFoundException {
        Optional<User> optionalUser = userRepository.findById(getActiveUserId());
        if (optionalUser.isEmpty()) {
            throw new UserPrincipalNotFoundException("User not found");
        }
        return optionalUser.get();
    }

    private long getActiveUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((UserDetailsImpl) authentication.getPrincipal()).getId();
        return userId;
    }
    public Client getClientById(long clientId) throws UserPrincipalNotFoundException {
        Optional<Client> optionalClient = clientRepository.findById(clientId);
        if (optionalClient.isEmpty()) {
            throw new RequestOptionalIsEmpty(String.format("Client with %d id doesn't exist", clientId));
        }
        Client client = optionalClient.get();
        if (isClientBelongsToActiveUser(client)){
            return client;
        } else {
            throw new SubjectNotBelongToActiveUser("It's not your Client. You don't have access to this Client.");
        }
    }
    private boolean isClientBelongsToActiveUser(Client client) throws UserPrincipalNotFoundException {
        User activeUser = getActiveUser();
        return (client.getUser().equals(activeUser));
    }
}
