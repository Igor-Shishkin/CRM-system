package com.crm.system.playload.response;

import com.crm.system.models.order.Order;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class OrderInfoDTO extends Order {

    private String clientFullName;
    private String clientPhoneNumber;
    private String clientEmail;
    private long clientId;

    public OrderInfoDTO(Order order) {
        super();

        this.setOrderId(order.getOrderId());
        this.setRealNeed(order.getRealNeed());
        this.setEstimateBudged(order.getEstimateBudged());
        this.setProjectApproved(order.isProjectApproved());
        this.setMeasurementsTaken(order.isMeasurementsTaken());
        this.setMeasurementOffered(order.isMeasurementOffered());
        this.setAgreementPrepared(order.isAgreementPrepared());
        this.setAgreementSigned(order.isAgreementSigned());
        this.setWasMeetingInOffice(order.isWasMeetingInOffice());
        this.setAddress(order.getAddress());
        this.setCalculationPromised(order.isCalculationPromised());
        this.setIsProjectShown(order.getIsProjectShown());
        this.setIsCalculationShown(order.getIsCalculationShown());
        this.setDateOfLastChange(order.getDateOfLastChange());
        this.setHasBeenPaid(order.isHasBeenPaid());
        this.setDateOfCreation(order.getDateOfCreation());
        this.setResultPrice(order.getResultPrice());

        this.setClient(null);
        this.setCalculations(order.getCalculations());

        this.setClientFullName(order.getClient().getFullName());
        this.setClientPhoneNumber(order.getClient().getPhoneNumber());
        this.setClientId(order.getClient().getClientId());
        this.setClientEmail(order.getClient().getEmail());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderInfoDTO that = (OrderInfoDTO) o;
        return clientId == that.clientId && Objects.equals(clientFullName, that.clientFullName) && Objects.equals(clientPhoneNumber, that.clientPhoneNumber) && Objects.equals(clientEmail, that.clientEmail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientFullName, clientPhoneNumber, clientEmail, clientId);
    }
}
