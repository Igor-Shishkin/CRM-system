package com.crm.system.playload.request;

import com.crm.system.models.order.InfoIsShown;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeOrderRequest {
    private Long orderId;
    private String realNeed;
    private double estimateBudged;
    @JsonProperty("isProjectApproved")
    private boolean isProjectApproved;
    private boolean wasMeetingInOffice;
    private String address;
    @JsonProperty("isMeasurementsTaken")
    private boolean isMeasurementsTaken = false;
    @JsonProperty("isMeasurementOffered")
    private boolean isMeasurementOffered = false;
    @JsonProperty("isCalculationPromised")
    private boolean isCalculationPromised = false;
    @Enumerated(EnumType.STRING)
    @JsonProperty("isProjectShown")
    private InfoIsShown isProjectShown = InfoIsShown.NOT_SHOWN;
    @Enumerated(EnumType.STRING)
    @JsonProperty("isCalculationShown")
    private InfoIsShown isCalculationShown = InfoIsShown.NOT_SHOWN;
    @JsonProperty("isAgreementPrepared")
    private boolean isAgreementPrepared = false;
}

