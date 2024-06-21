package com.crm.system.services;

import com.crm.system.models.Client;
import com.crm.system.playload.request.AddClientDTO;
import com.crm.system.playload.request.EditClientDataDTO;
import com.crm.system.playload.response.ClientInfoDTO;
import org.springframework.stereotype.Service;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.Set;

@Service
public interface ClientService {

    Set<ClientInfoDTO> getClientsWithClientStatusForUser();
    Set<ClientInfoDTO> getClientsWithLeadStatusForUser();
    Set<ClientInfoDTO> getClientsWithBlacklistStatusForUser();
    long addNewClient(AddClientDTO addClientDTO) throws UserPrincipalNotFoundException ;
    void sentToBlackList(long clientId) throws UserPrincipalNotFoundException;
    void restoreClientFromBlackList(long clientId) throws UserPrincipalNotFoundException;
    Client getInfoWithOrdersClient(long clientId);
    void editClientData(EditClientDataDTO request);
    Client getClientByIdForActualUser(long clientId);
    void saveClient(Client client);
}
