package com.crm.system.services;

import com.crm.system.models.Client;
import com.crm.system.playload.request.AddLeadDTO;
import com.crm.system.playload.request.EditClientDataDTO;
import com.crm.system.playload.response.ClientInfoDTO;
import org.springframework.stereotype.Service;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.List;
import java.util.Set;

@Service
public interface ClientService {

    Set<ClientInfoDTO> getClientsWithClientStatusForUser();
    List<ClientInfoDTO> getClientsWithLeadStatusForUser();
    List<ClientInfoDTO> getClientsWithBlacklistStatusForUser();
    long addNewLead(AddLeadDTO addLeadDTO) throws UserPrincipalNotFoundException ;
    void sentToBlackList(long clientId);
    void restoreClientFromBlackList(long clientId);
    Client getClient(long clientId);
    void editClientData(EditClientDataDTO request);
    Client getClientByIdForActualUser(long clientId);
    void saveClient(Client client);
}
