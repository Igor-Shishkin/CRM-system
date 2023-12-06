package com.crm.system.models;

import com.crm.system.models.order.Order;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "lids",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "email")
        })
public class Lid {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "lid_id")
        private Long id;

        @NotBlank
        @Size(max = 255)
        private String fullName;

        @Email
        @NotBlank
        @Size(max = 80)
        private String email;

        @Size(max = 50)
        @Column(name = "phone_number")
        private String phoneNumber;

        @Column(name = "is_client")
        private boolean isClient;

        private String address;

        @OneToMany(mappedBy = "lid", cascade = CascadeType.REMOVE)
        @JsonManagedReference
        private Set<Order> orders = new HashSet<>();

        @OneToMany(mappedBy = "lid", cascade = CascadeType.REMOVE)
        @JsonManagedReference
        private Set<HistoryMessage> history = new HashSet<>();

        @ManyToOne
        @JoinColumn(name = "user_id")
        @JsonBackReference
        private User user;

        public Lid() {
        }

        public Lid(String fullName, String email, String phoneNumber, String address, User user) {
                this.fullName = fullName;
                this.email = email;
                this.phoneNumber = phoneNumber;
                this.address = address;
                this.user = user;
        }
}
