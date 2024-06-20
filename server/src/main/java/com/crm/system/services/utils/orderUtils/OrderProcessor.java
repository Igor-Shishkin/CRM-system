package com.crm.system.services.utils.orderUtils;

import com.crm.system.exception.MismanagementOfTheClientException;
import com.crm.system.models.ClientStatus;
import com.crm.system.models.order.InfoIsShown;
import com.crm.system.models.order.ItemForAdditionalPurchases;
import com.crm.system.models.order.Order;
import com.crm.system.playload.request.ChangeOrderDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.function.Predicate;

@Component
public class OrderProcessor {

    @Value("${app.crm.price.coefficient}")
    private double PRICE_COEFFICIENT;

    public void signAgreementForOrder(Order order) {
        checkIsAgreementNotSign(order);
        checkIfCalculationIsShownToClient(order);
        checkIfCalculationIsRight(order);

        order.setAgreementSigned(true);
        order.setAgreementPrepared(true);
        order.setDateOfLastChange(LocalDateTime.now());
    }

    public void canselAgreement(Order order) {
        checkIfAgreementIsSign(order);
        checkIfOrderHasNotBePaid(order);

        order.setAgreementSigned(false);
        order.setDateOfLastChange(LocalDateTime.now());
    }

    public void confirmPayment(Order order) {
        checkIfOrderHasNotBePaid(order);
        checkIfAgreementIsSign(order);

        order.setHasBeenPaid(true);
        order.setDateOfLastChange(LocalDateTime.now());
        order.getClient().setStatus(ClientStatus.CLIENT);
        order.getClient().setDateOfLastChange(LocalDateTime.now());
    }

    public void cancelPayment(Order order) {
        checkIfOrderHasBePaid(order);

        setClientStatus(order);
        order.setHasBeenPaid(false);
        order.setDateOfLastChange(LocalDateTime.now());
    }

    public void setChangedParameters(Order order, ChangeOrderDTO changedOrder) {
        order.setIsCalculationShown(changedOrder.getIsCalculationShown());
        order.setAgreementPrepared(changedOrder.isAgreementPrepared());
        order.setAddress(changedOrder.getAddress());
        order.setRealNeed(changedOrder.getRealNeed());
        order.setCalculationPromised(changedOrder.isCalculationPromised());
        order.setEstimateBudged(changedOrder.getEstimateBudged());
        order.setIsProjectShown(changedOrder.getIsProjectShown());
        order.setMeasurementOffered(changedOrder.isMeasurementOffered());
        order.setMeasurementsTaken(changedOrder.isMeasurementsTaken());
        order.setProjectApproved(changedOrder.isProjectApproved());
        order.setWasMeetingInOffice(changedOrder.isWasMeetingInOffice());
    }


    private void checkIfOrderHasNotBePaid(Order order) {
        if (order.isHasBeenPaid()) {
            throw new MismanagementOfTheClientException("Order is already paid");
        }
    }

    private void checkIfAgreementIsSign(Order order) {
        if (!order.isAgreementSigned()) {
            throw new MismanagementOfTheClientException("Agreement isn't signed");
        }
    }

    private void checkIsAgreementNotSign(Order order) {
        if (order.isAgreementSigned()) {
            throw new MismanagementOfTheClientException("Agreement is already signed");
        }
    }

    private void checkIfCalculationIsShownToClient(Order order) {
        if (order.getIsCalculationShown().equals(InfoIsShown.NOT_SHOWN)) {
            throw new MismanagementOfTheClientException("You must show calculation to the client");
        }
    }

    private void checkIfCalculationIsRight(Order order) {
        Predicate<ItemForAdditionalPurchases> isValidItem = item ->
                item.getItemName() != null && !item.getItemName().isEmpty() &&
                        item.getUnitPrice() > 0 &&
                        item.getTotalPrice() > 0 &&
                        item.getQuantity() > 0 &&
                        item.getTotalPrice() == item.getUnitPrice() * item.getQuantity() * PRICE_COEFFICIENT;

        boolean isValidItems = order.getAdditionalPurchases().stream().allMatch(isValidItem);
        boolean isResultPriceRight = order.getResultPrice() == order.getAdditionalPurchases().stream()
                .mapToDouble(ItemForAdditionalPurchases::getTotalPrice)
                .sum();
        if (!isValidItems || !isResultPriceRight) {
            throw new MismanagementOfTheClientException
                    ("To sign the contract, you must fill out the calculations correctly and show it to Client.");
        }
    }

    private void setClientStatus(Order order) {

        if (order.getClient().getOrders().stream().noneMatch(Order::isHasBeenPaid)) {
            order.getClient().setStatus(ClientStatus.CLIENT);
        } else {
            order.getClient().setStatus(ClientStatus.LEAD);
        }
    }

    private void checkIfOrderHasBePaid(Order order) {
        if (!order.isHasBeenPaid()) {
            throw new MismanagementOfTheClientException("Payment was not made");
        }
    }
}
