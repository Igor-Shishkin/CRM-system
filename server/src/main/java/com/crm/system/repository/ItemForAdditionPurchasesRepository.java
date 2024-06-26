package com.crm.system.repository;

import com.crm.system.models.order.ItemForAdditionalPurchases;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemForAdditionPurchasesRepository extends JpaRepository<ItemForAdditionalPurchases, Long> {
}
