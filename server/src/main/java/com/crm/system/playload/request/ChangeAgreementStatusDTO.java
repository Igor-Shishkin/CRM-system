package com.crm.system.playload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeAgreementStatusDTO {
    @JsonProperty("isAgreementSigned")
    private boolean isAgreementSigned;
    private long orderId;
}
