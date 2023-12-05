package com.crm.system.models.order;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "item")
public class ItemForCalcualtion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long itemId;

    private String thing;
    private String material;
    private int quantity;
    @Column(name = "unit_price")
    private double unitPrice;
    @Column(name = "total_price")
    private double totalPrice;

}
