package com.crm.system.services.utils.clientUtils;

import com.crm.system.models.Client;
import com.crm.system.models.ClientStatus;
import com.crm.system.models.order.Order;
import com.crm.system.playload.response.ClientInfoDTO;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ClientInfoDTOMapper {

    public Set<ClientInfoDTO> mapClientsToClientInfoDTOWithNumberPaidOrders(Set<Client> clients) {
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
                            .withIsClient(client.getStatus())
                            .withNumberOfOrders(client.getOrders().size())
                            .withNumberOfPaidOrders(numberOfPaidOrders)
                            .build();
                })
                .collect(Collectors.toSet());
    }

    public Set<ClientInfoDTO> mapClientsToClientInfoDTO(Set<Client> clients) {
        return clients.stream()
                .map(client -> new ClientInfoDTO.Builder()
                        .withId(client.getClientId())
                        .withFullName(client.getFullName())
                        .withAddress(client.getAddress())
                        .withEmail(client.getEmail())
                        .withPhoneNumber(client.getPhoneNumber())
                        .withIsClient(client.getStatus())
                        .withNumberOfOrders(client.getOrders().size())
                        .build())
                .collect(Collectors.toSet());
    }
}
