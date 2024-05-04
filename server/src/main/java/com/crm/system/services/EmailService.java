package com.crm.system.services;

import com.crm.system.playload.request.SentEmailDTO;
import org.springframework.stereotype.Service;

import java.nio.file.attribute.UserPrincipalNotFoundException;

@Service
public interface EmailService {
    void sentEmail(SentEmailDTO sentEmailDTO) throws UserPrincipalNotFoundException;
}
