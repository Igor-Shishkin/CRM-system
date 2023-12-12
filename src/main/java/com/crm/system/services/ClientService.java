package com.crm.system.services;

import com.crm.system.exception.ClientAlreadyExistException;
import com.crm.system.exception.RequestOptionalIsEmpty;
import com.crm.system.exception.SubjectNotBelongToActiveUser;
import com.crm.system.models.Client;
import com.crm.system.models.ClientStatus;
import com.crm.system.models.User;
import com.crm.system.models.order.Order;
import com.crm.system.playload.request.AddLidRequest;
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
                        .withQuantityOfOrders(client.getOrders().size())
                        .withNumberOfPaidOrders(numberOfPaidOrders)
                        .build();
                infoClientResponses.add(clientInfoResponse);
            }
        }
        return infoClientResponses;
    }

    public List<ClientInfoResponse> getAllLeads() throws UserPrincipalNotFoundException {
        Optional<User> activeUser = getActiveUser(getActiveUserId());
        if (activeUser.isEmpty()) {
            throw new UserPrincipalNotFoundException("User not found");
        }
        User user = activeUser.get();
        List<ClientInfoResponse> infoLidResponces = new ArrayList<>(user.getClients().size() * 2);
        for (Client client : user.getClients()) {
            if (client.getStatus().equals(ClientStatus.LEAD)) {
                infoLidResponces.add(new ClientInfoResponse.Builder()
                        .withId(client.getId())
                        .withFullName(client.getFullName())
                        .withAddress(client.getAddress())
                        .withEmail(client.getEmail())
                        .withPhoneNumber(client.getPhoneNumber())
                        .withIsClient(ClientStatus.LEAD)
                        .withQuantityOfOrders(client.getOrders().size())
                        .build());
            }
        }
        return infoLidResponces;
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

        historyMessageService.createHistoryMessageAboutNewLead(lead, activeUser.get());

        clientRepository.save(lead);
    }

    public void deleteLidById(long lidId) throws UserPrincipalNotFoundException, SubjectNotBelongToActiveUser {
        long activeUserId = getActiveUserId();
        Optional<Client> optionalClient = clientRepository.findById(lidId);
        if (optionalClient.isPresent()) {
            Client requestClient = optionalClient.get();
            if (requestClient.getUser().getUserId().equals(activeUserId)) {
                clientRepository.deleteById(lidId);
            } else {
                throw new SubjectNotBelongToActiveUser("It's not your LID. You don't have access to this LID.");
            }
        } else {
            throw new RequestOptionalIsEmpty(String.format("Lid with %d id doesn't exist", lidId));
        }
    }

    public Optional<User> getActiveUser(long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user;
    }

    private long getActiveUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((UserDetailsImpl) authentication.getPrincipal()).getId();
        System.out.println("\n" + userId + "\n");
        return userId;
    }
}
