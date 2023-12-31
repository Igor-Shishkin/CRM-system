package com.crm.system.playload.response;

import com.crm.system.models.order.Order;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderInfoResponse extends Order {

    private String clientFullName;
    private String clientPhoneNumber;
    private String clientEmail;
    private long clientId;

    public OrderInfoResponse(Order order) {
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
        this.setProjectPhotos(order.getProjectPhotos());
        this.setCalculations(order.getCalculations());

        this.setClientFullName(order.getClient().getFullName());
        this.setClientPhoneNumber(order.getClient().getPhoneNumber());
        this.setClientId(order.getClient().getId());
        this.setClientEmail(order.getClient().getEmail());
    }
}
