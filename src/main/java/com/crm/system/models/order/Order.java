package com.crm.system.models.order;

import com.crm.system.models.Lid;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long order_id;
    @NotBlank
    @Column(name = "real_need")
    private String realNeed;

    @Column(name = "estimate_budget")
    private double estimateBudged;

    @Column(name = "is_project_approved")
    private boolean isProjectApproved;

    @Column(name = "was_meeting_in_office")
    private boolean wasMeetingInOffice;

    @Column(name = "result_price")
    private double resultPrice = 0.0;

    @Column(name = "has_been_paid")
    private boolean hasBeenPaid;

    private String address;

    @Column(name = "is_calculation_promised")
    private boolean isCalculationPromised;

    @Column(name = "is_project_shown")
    private InfoIsShown isProjectShown = InfoIsShown.NOT_SHOWN;

    @Column(name = "is_calculation_shown")
    private InfoIsShown isCalculationShown = InfoIsShown.NOT_SHOWN;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<ProjectPhoto> lidPhotos = new HashSet<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Where(clause = "is_user_photo = true")
    private Set<ProjectPhoto> userPhotos = new HashSet<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.REMOVE)
    @JsonManagedReference
    private Set<ItemForCalcualtion> calculations = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "lid_id")
    @JsonBackReference
    private Lid lid;

    public Order() {
    }
}