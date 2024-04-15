package com.crm.system.services.impl;

import com.crm.system.exception.MismanagementOfTheClientException;
import com.crm.system.models.order.ItemForAdditionalPurchases;
import com.crm.system.models.order.Order;
import com.crm.system.services.ItemsForAdditionalPurchasesService;
import com.crm.system.services.OrderService;
import com.crm.system.services.utils.itemsForPurchasesUtils.ItemProcessing;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ItemsForAdditionalPurchasesServiceImpl implements ItemsForAdditionalPurchasesService {
    private final OrderService orderService;
    private final ItemProcessing itemProcessing;

    public ItemsForAdditionalPurchasesServiceImpl(OrderService orderService, ItemProcessing itemProcessing) {
        this.orderService = orderService;
        this.itemProcessing = itemProcessing;
    }

    public void saveItems(Set<ItemForAdditionalPurchases> items, long orderId) {

        Order order = orderService.getOrder(orderId);
        checkIfTheOrderHasBeenSigned(order);

        items = itemProcessing.processReceivedItemsForSaving(items, order);
        itemProcessing.changeSetOfItemsInOrder(order, items);

        orderService.changeOrder(order);
    }


    private void checkIfTheOrderHasBeenSigned(Order order) {
        if (order.isAgreementSigned()) {
            throw new MismanagementOfTheClientException
                    ("You can't changes item's for addition purchases when agreement is signed");
        }
    }
}
