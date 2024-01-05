package com.crm.system.models.order;

import com.crm.system.models.Client;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

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
    @JsonProperty("isProjectApproved")
    private boolean isProjectApproved = false;

    @Column(name = "was_meeting_in_office")
    private boolean wasMeetingInOffice = false;

    @Column(name = "result_price")
    private double resultPrice = 0.0;

    @Column(name = "has_been_paid")
    private boolean hasBeenPaid = false;

    private String address;

    @Column(name = "is_measurements_taken")
    @JsonProperty("isMeasurementsTaken")
    private boolean isMeasurementsTaken = false;

    @Column(name = "is_measurement_offered")
    @JsonProperty("isMeasurementOffered")
    private boolean isMeasurementOffered = false;

    @Column(name = "is_calculation_promised")
    @JsonProperty("isCalculationPromised")
    private boolean isCalculationPromised = false;

    @Column(name = "is_project_shown")
    @Enumerated(EnumType.STRING)
    @JsonProperty("isProjectShown")
    private InfoIsShown isProjectShown = InfoIsShown.NOT_SHOWN;

    @Column(name = "is_calculation_shown")
    @Enumerated(EnumType.STRING)
    @JsonProperty("isCalculationShown")
    private InfoIsShown isCalculationShown = InfoIsShown.NOT_SHOWN;

    @Column(name = "is_agreement_prepared")
    @JsonProperty("isAgreementPrepared")
    private boolean isAgreementPrepared = false;

    @Column(name = "is_agreement_signed")
    @JsonProperty("isAgreementSigned")
    private boolean isAgreementSigned = false;

    @Column(name = "data_of_creation")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateOfCreation = LocalDateTime.now();

    @Column(name = "data_of_last_change")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateOfLastChange;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    @Column(name = "project_photos")
    private Set<ProjectPhoto> projectPhotos = new HashSet<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<ItemForCalcualtion> calculations = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "client_id")
    @JsonBackReference
    private Client client;

    public Order() {
    }
}
