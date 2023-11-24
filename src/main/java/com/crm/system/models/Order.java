package com.crm.system.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Size(max = 100)
    private String thing;
    private double price;
    @ManyToOne
    @JoinColumn(name = "client_id")
    @JsonBackReference
    private Lid client;

    public Order(String thing, double price) {
        this.thing = thing;
        this.price = price;
    }
    public Order() {
    }
}
