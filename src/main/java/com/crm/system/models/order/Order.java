package com.crm.system.models.order;

import com.crm.system.models.Client;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;
    @NotBlank
    @Column(name = "real_need")
    private String realNeed;

    @Column(name = "estimate_budget")
    private double estimateBudged;

    @Column(name = "is_project_approved")
    private boolean isProjectApproved = false;

    @Column(name = "was_meeting_in_office")
    private boolean wasMeetingInOffice = false;

    @Column(name = "result_price")
    private double resultPrice = 0.0;

    @Column(name = "has_been_paid")
    private boolean hasBeenPaid = false;

    private String address;

    @Column(name = "measurements_taken")
    private boolean measurementsTaken = false;

    @Column(name = "measurement_offered")
    private boolean measurementOffered;

    @Column(name = "is_calculation_promised")
    private boolean isCalculationPromised = false;

    @Column(name = "is_project_shown")
    @Enumerated(EnumType.STRING)
    private InfoIsShown isProjectShown = InfoIsShown.NOT_SHOWN;

    @Column(name = "is_calculation_shown")
    @Enumerated(EnumType.STRING)
    private InfoIsShown isCalculationShown = InfoIsShown.NOT_SHOWN;

    @Column(name = "has_agreement_prepared")
    private boolean hasAgreementPrepared;

    @Column(name = "data_of_creation")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateOfCreation;

    @Column(name = "data_of_last_change")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateOfLastChange;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<ProjectPhoto> clientPhotos = new HashSet<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Where(clause = "is_user_photo = true")
    private Set<ProjectPhoto> userPhotos = new HashSet<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.REMOVE)
    @JsonManagedReference
    private Set<ItemForCalcualtion> calculations = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "client_id")
    @JsonBackReference
    private Client client;

    public Order() {
    }
}
