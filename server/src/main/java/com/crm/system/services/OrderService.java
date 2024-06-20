package com.crm.system.services;

import com.crm.system.models.order.Order;
import com.crm.system.playload.request.ChangeOrderDTO;
import com.crm.system.playload.request.CreateNewOrderDTO;
import com.crm.system.playload.response.ItemsForAdditionalPurchasesDTO;
import com.crm.system.playload.response.OrderInfoDTO;
import org.springframework.stereotype.Service;

import java.nio.file.attribute.UserPrincipalNotFoundException;

@Service
public interface OrderService {

    OrderInfoDTO getOrderInfoResponse(long orderId);
    Order getOrder(long orderId);
    ItemsForAdditionalPurchasesDTO getAdditionalPurchases(long orderId);
    void changeOrder(Order order);
    void signAgreement(long orderId) throws UserPrincipalNotFoundException;
    void cancelAgreement(long orderId) throws UserPrincipalNotFoundException;
    void confirmPayment(long orderId) throws UserPrincipalNotFoundException;
    void cancelPayment(long orderId) throws UserPrincipalNotFoundException;
    void saveOrderChanges(ChangeOrderDTO changedOrder);
    long createNewOrder(CreateNewOrderDTO request) throws UserPrincipalNotFoundException;
}
