package com.crm.system.models.order;

import com.crm.system.models.Lid;
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
    @Column(name = "real_need")
    private String realNeed;
    private double estimateBudged;
    private boolean isProjectApproved;
    private boolean wasMeetingInOffice;
    private double price;
    @ManyToOne
    @JoinColumn(name = "lid_id")
    @JsonBackReference
    private Lid client;
    public Order() {
    }
}
