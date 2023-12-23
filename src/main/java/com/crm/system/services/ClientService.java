package com.crm.system.services;

import com.crm.system.exception.ClientAlreadyExistException;
import com.crm.system.exception.NameOrEmailIsEmptyException;
import com.crm.system.exception.RequestOptionalIsEmpty;
import com.crm.system.exception.SubjectNotBelongToActiveUser;
import com.crm.system.models.Client;
import com.crm.system.models.ClientStatus;
import com.crm.system.models.User;
import com.crm.system.models.order.Order;
import com.crm.system.playload.request.AddLidRequest;
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
        Optional<User> activeUser = getActiveUser(getActiveUserId());
        if (activeUser.isEmpty()) {
            throw new UserPrincipalNotFoundException("User not found");
        }
        User user = activeUser.get();
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
        long activeUserId = getActiveUserId();

        return clientRepository.findAll().stream()
                .filter(client -> client.getUser().getUserId().equals(activeUserId))
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

    public void addNewLead(AddLidRequest addLidRequest) throws UserPrincipalNotFoundException {
        if (clientRepository.existsByEmail(addLidRequest.getEmail())) {
            throw new ClientAlreadyExistException("Lid with this email already exists");
        }
        Optional<User> activeUser = getActiveUser(getActiveUserId());
        Client lead = activeUser.map(user -> new Client(
                addLidRequest.getFullName(),
                addLidRequest.getEmail(),
                addLidRequest.getPhoneNumber(),
                addLidRequest.getAddres(),
                user
        )).orElseThrow((
        ) -> new UserPrincipalNotFoundException("User not found"));

        String messageText = String.format("Lead %s is created", lead.getFullName());
        historyMessageService.createHistoryMessageForClient(lead, activeUser.get(), messageText);

        clientRepository.save(lead);
    }

    public void sentToBlackList(long lidId) throws UserPrincipalNotFoundException, SubjectNotBelongToActiveUser {
        Optional<User> activeUser = getActiveUser(getActiveUserId());
        if (activeUser.isPresent()) {
            Optional<Client> optionalClient = clientRepository.findById(lidId);
            if (optionalClient.isPresent()) {
                Client requestClient = optionalClient.get();
                if (requestClient.getUser().equals(activeUser.get())) {
                    requestClient.setStatus(ClientStatus.BLACKLIST);
                    clientRepository.save(requestClient);
                    String messageText = String.format("Client %s goes to blackList", requestClient.getFullName());
                    historyMessageService.createHistoryMessageForClient(requestClient, activeUser.get(), messageText);
                } else {
                    throw new SubjectNotBelongToActiveUser("It's not your Client. You don't have access to this Client.");
                }
            } else {
                throw new RequestOptionalIsEmpty(String.format("Client with %d id doesn't exist", lidId));
            }
        } else {
            throw new UserPrincipalNotFoundException("Active User isn't found");
        }
    }

    public Client getClient(long clientId) {
        long activeUserId = getActiveUserId();
        Optional<Client> optionalClient = clientRepository.findById(clientId);
        if (optionalClient.isPresent()) {
            if (optionalClient.get().getUser().getUserId().equals(activeUserId)) {
                Client client = optionalClient.get();
                for (Order order: client.getOrders()) {
                    order.setUserPhotos(null);
                    order.setClientPhotos(null);
                    order.setCalculations(null);
                }
                return client;
            } else  {
                throw new SubjectNotBelongToActiveUser("It's not you client!");
            }
        } else {
            throw new RequestOptionalIsEmpty("Client isn't found");
        }
    }
    public void editClientData(EditClientDataRequest request) {
        Optional<Client> optionalClient = clientRepository.findById(request.getClientId());
        if (optionalClient.isEmpty()) {
            throw new RequestOptionalIsEmpty("Client not found");
        }
        Client client = optionalClient.get();
        if (!client.getUser().getUserId().equals(getActiveUserId())) {
            throw new SubjectNotBelongToActiveUser("It's not your client!");
        }
        if (request.getFullName().isBlank() ||
                request.getEmail().isBlank()) {
            throw new NameOrEmailIsEmptyException("Name and email can't be empty!");
        }
        client.setFullName(request.getFullName());
        client.setEmail(request.getEmail());
        client.setPhoneNumber(request.getPhoneNumber());
        client.setAddress(request.getAddress());

        clientRepository.save(client);
    }
    private Optional<User> getActiveUser(long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user;
    }

    private long getActiveUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((UserDetailsImpl) authentication.getPrincipal()).getId();
        return userId;
    }


}
