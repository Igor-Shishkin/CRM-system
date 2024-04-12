package com.crm.system.services;

import com.crm.system.playload.request.SentEmailDTO;
import org.springframework.stereotype.Service;

@Service
public interface EmailService {
    void sentEmail(SentEmailDTO sentEmailDTO);
}
