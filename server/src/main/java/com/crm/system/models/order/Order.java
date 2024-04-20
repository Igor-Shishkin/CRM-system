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
import java.util.Objects;
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

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private Set<ItemForAdditionalPurchases> additionalPurchases = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    @JsonBackReference
    private Client client;

    public Order() {
    }
    public Order(String realNeed, double estimateBudged, Client client) {
        this.realNeed = realNeed;
        this.estimateBudged = estimateBudged;
        this.client = client;
        this.dateOfCreation = LocalDateTime.now();
        this.dateOfLastChange = LocalDateTime.now();
        this.address = client.getAddress();
    }


    @Override
    public boolean equals(Object object) {

        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Order order = (Order) object;
        return Double.compare(getEstimateBudged(), order.getEstimateBudged()) == 0 && isProjectApproved() == order.isProjectApproved() && isWasMeetingInOffice() == order.isWasMeetingInOffice() && Double.compare(getResultPrice(), order.getResultPrice()) == 0 && isHasBeenPaid() == order.isHasBeenPaid() && isMeasurementsTaken() == order.isMeasurementsTaken() && isMeasurementOffered() == order.isMeasurementOffered() && isCalculationPromised() == order.isCalculationPromised() && isAgreementPrepared() == order.isAgreementPrepared() && isAgreementSigned() == order.isAgreementSigned() && Objects.equals(getOrderId(), order.getOrderId()) && Objects.equals(getRealNeed(), order.getRealNeed()) && Objects.equals(getAddress(), order.getAddress()) && getIsProjectShown() == order.getIsProjectShown() && getIsCalculationShown() == order.getIsCalculationShown() && Objects.equals(getDateOfCreation(), order.getDateOfCreation()) && Objects.equals(getDateOfLastChange(), order.getDateOfLastChange());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOrderId(), getRealNeed(), getEstimateBudged(), isProjectApproved(), isWasMeetingInOffice(), getResultPrice(), isHasBeenPaid(), getAddress(), isMeasurementsTaken(), isMeasurementOffered(), isCalculationPromised(), getIsProjectShown(), getIsCalculationShown(), isAgreementPrepared(), isAgreementSigned(), getDateOfCreation(), getDateOfLastChange());
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", realNeed='" + realNeed + '\'' +
                ", estimateBudged=" + estimateBudged +
                ", resultPrice=" + resultPrice +
                ", hasBeenPaid=" + hasBeenPaid +
                ", isAgreementSigned=" + isAgreementSigned +
                ", dateOfCreation=" + dateOfCreation +
                '}';
    }
}
