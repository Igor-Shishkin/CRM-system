package com.crm.system.services;

import com.crm.system.exception.RequestOptionalIsEmpty;
import com.crm.system.exception.SubjectNotBelongToActiveUser;
import com.crm.system.models.Lid;
import com.crm.system.models.User;
import com.crm.system.models.order.Order;
import com.crm.system.playload.request.AddLidRequest;
import com.crm.system.playload.response.ClientInfoResponse;
import com.crm.system.playload.response.LidInfoResponse;
import com.crm.system.repository.LidRepository;
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
public class LidService {
    private final LidRepository lidRepository;
    private final UserRepository userRepository;

    public LidService(LidRepository clientRepository, UserRepository userRepository) {
        this.lidRepository = clientRepository;
        this.userRepository = userRepository;
    }

    public void addClient(Lid client) {
        lidRepository.save(client);
    }

    public List<ClientInfoResponse> getAllClients() throws UserPrincipalNotFoundException {
        Optional<User> activeUser = getActiveUser(getActiveUserId());
        if (activeUser.isEmpty()) {
            throw new UserPrincipalNotFoundException("User not found");
        }
        User user = activeUser.get();
        List<ClientInfoResponse> infoClientResponces = new ArrayList<>(user.getLids().size() * 2);
        for (Lid client : user.getLids()) {
            if (client.isClient()) {
                int numberOfPaidOrders = 0;
                for (Order order : client.getOrders()) {
                    if (order.isHasBeenPaid()) { numberOfPaidOrders++; }
                }
//                ClientInfoResponse clientInfoResponse = new ClientInfoResponse.ClientBuilder()
//                        .withId(client.getId())
//                        .withFullName(client.getFullName())
//                        .withAddress(client.getAddress())
//                        .withEmail(client.getEmail())
//                        .withPhoneNumber(client.getPhoneNumber())
//                        .withIsClient(true)
//                        .withQuantityOfOrders(client.getOrders().size())
//                        .build();
                ClientInfoResponse.ClientBuilder clientBuilder = (ClientInfoResponse.ClientBuilder) new ClientInfoResponse.ClientBuilder()
                        .withId(client.getId())
                        .withFullName(client.getFullName())
                        .withAddress(client.getAddress())
                        .withEmail(client.getEmail())
                        .withPhoneNumber(client.getPhoneNumber())
                        .withIsClient(true)
                        .withQuantityOfOrders(client.getOrders().size());

                ClientInfoResponse clientInfoResponse = clientBuilder
                        .withNumberOfPaidOrders(numberOfPaidOrders)
                        .build();
                infoClientResponces.add(clientInfoResponse);
            }
        }
        return infoClientResponces;
    }

    public List<LidInfoResponse> getAllLids() throws UserPrincipalNotFoundException {
        Optional<User> activeUser = getActiveUser(getActiveUserId());
        if (activeUser.isEmpty()) {
            throw new UserPrincipalNotFoundException("User not found");
        }
        User user = activeUser.get();
        List<LidInfoResponse> infoLidResponces = new ArrayList<>(user.getLids().size() * 2);
        for (Lid lid : user.getLids()) {
            if (!lid.isClient()) {
                infoLidResponces.add(new LidInfoResponse.Builder()
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
    public void addNewLid(AddLidRequest addLidRequest) throws UserPrincipalNotFoundException {
        Optional<User> activeUser = getActiveUser(getActiveUserId());
        if (activeUser.isEmpty()) {
            throw new UserPrincipalNotFoundException("User not found");
        }
        User user = activeUser.get();
        Lid lid = new Lid(addLidRequest.getFullName(),
                addLidRequest.getEmail(),
                addLidRequest.getPhoneNumber(),
                addLidRequest.getAddres(),
                user);
        lidRepository.save(lid);
    }
    public void deleteLidById(long lidId) throws UserPrincipalNotFoundException, SubjectNotBelongToActiveUser {
        long activeUserId = getActiveUserId();
        Optional<Lid> optionalLid = lidRepository.findById(lidId);
        if (optionalLid.isEmpty()) {
            throw new RequestOptionalIsEmpty(String.format("Lid with %d id doesn't exist", lidId));
        }
        Lid requestLid = optionalLid.get();
        if (requestLid.getUser().getUserId().equals(activeUserId)) {
            lidRepository.deleteById(lidId);
        } else {
            throw new SubjectNotBelongToActiveUser("It's not your LID. You don't have access to this LID.");
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
