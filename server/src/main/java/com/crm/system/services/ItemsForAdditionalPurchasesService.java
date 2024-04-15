package com.crm.system.services;

import com.crm.system.models.order.ItemForAdditionalPurchases;
import org.springframework.stereotype.Service;
import java.util.Set;

@Service
public interface ItemsForAdditionalPurchasesService {
    public void saveItems(Set<ItemForAdditionalPurchases> items, long orderId);
}
