package com.crm.system.services;

import com.crm.system.exception.ClientAlreadyExistException;
import com.crm.system.exception.RequestOptionalIsEmpty;
import com.crm.system.exception.SubjectNotBelongToActiveUser;
import com.crm.system.models.Client;
import com.crm.system.models.HistoryMessage;
import com.crm.system.models.User;
import com.crm.system.models.order.Order;
import com.crm.system.playload.request.AddLidRequest;
import com.crm.system.playload.response.ClientInfoResponse;
import com.crm.system.playload.response.LeadInfoResponse;
import com.crm.system.repository.ClientRepository;
import com.crm.system.repository.HistoryMessageRepository;
import com.crm.system.repository.UserRepository;
import com.crm.system.security.services.UserDetailsImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ClientService {
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final HistoryMessageRepository historyMessageRepository;

    public ClientService(ClientRepository clientRepository, UserRepository userRepository, HistoryMessageRepository historyMessageRepository) {
        this.clientRepository = clientRepository;
        this.userRepository = userRepository;
        this.historyMessageRepository = historyMessageRepository;
    }

    public List<ClientInfoResponse> getAllClients() throws UserPrincipalNotFoundException {
        Optional<User> activeUser = getActiveUser(getActiveUserId());
        if (activeUser.isEmpty()) {
            throw new UserPrincipalNotFoundException("User not found");
        }
        User user = activeUser.get();
        List<ClientInfoResponse> infoClientResponses = new ArrayList<>(user.getLids().size() * 2);
        for (Client client : user.getLids()) {
            if (client.isClient()) {
                int numberOfPaidOrders = (int) client.getOrders().stream()
                        .filter(Order::isHasBeenPaid)
                        .count();
                ClientInfoResponse clientInfoResponse = new ClientInfoResponse.ClientBuilder()
                        .withId(client.getId())
                        .withFullName(client.getFullName())
                        .withAddress(client.getAddress())
                        .withEmail(client.getEmail())
                        .withPhoneNumber(client.getPhoneNumber())
                        .withIsClient(true)
                        .withQuantityOfOrders(client.getOrders().size())
                        .withNumberOfPaidOrders(numberOfPaidOrders)
                        .build();
                infoClientResponses.add(clientInfoResponse);
            }
        }
        return infoClientResponses;
    }

    public List<LeadInfoResponse> getAllLeads() throws UserPrincipalNotFoundException {
        Optional<User> activeUser = getActiveUser(getActiveUserId());
        if (activeUser.isEmpty()) {
            throw new UserPrincipalNotFoundException("User not found");
        }
        User user = activeUser.get();
        List<LeadInfoResponse> infoLidResponces = new ArrayList<>(user.getLids().size() * 2);
        for (com.crm.system.models.Client lid : user.getLids()) {
            if (!lid.isClient()) {
                infoLidResponces.add(new LeadInfoResponse.Builder()
                        .withId(lid.getId())
                        .withFullName(lid.getFullName())
                        .withAddress(lid.getAddress())
                        .withEmail(lid.getEmail())
                        .withPhoneNumber(lid.getPhoneNumber())
                        .withIsClient(true)
                        .withQuantityOfOrders(lid.getOrders().size())
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

        generateHistoryMessageAboutCreationNewLead(lead, activeUser.get());

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
    private void generateHistoryMessageAboutCreationNewLead(Client lead, User user) {
        HistoryMessage message = new HistoryMessage(String.format("Lead %s is created", lead.getFullName()));
        message.setDone(true);
        message.setImportant(false);
        message.setDateOfCreation(LocalDateTime.now());
    }
}
