package com.crm.system.models.order;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Entity
@Getter
@Setter
@Table(name = "items_for_additional_purchases")
public class ItemForAdditionalPurchases {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long itemId;

    @Column(name = "item_name")
    private String itemName;

    private int quantity;

    @Column(name = "unit_price")
    private double unitPrice;

    @Column(name = "total_price")
    private double totalPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @JsonBackReference
    private Order order;

    public ItemForAdditionalPurchases() {
    }

    public ItemForAdditionalPurchases(String itemName, int quantity, double unitPrice, double totalPrice) {
        this.itemName = itemName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        ItemForAdditionalPurchases that = (ItemForAdditionalPurchases) object;
        return getQuantity() == that.getQuantity() && Double.compare(getUnitPrice(), that.getUnitPrice()) == 0 && Double.compare(getTotalPrice(), that.getTotalPrice()) == 0 && Objects.equals(getItemId(), that.getItemId()) && Objects.equals(getItemName(), that.getItemName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getItemId(), getItemName(), getQuantity(), getUnitPrice(), getTotalPrice());
    }

    @Override
    public String toString() {
        return "ItemForAdditionalPurchases{" +
                "itemId=" + itemId +
                ", itemName='" + itemName + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", totalPrice=" + totalPrice +
                '}';
    }
}
