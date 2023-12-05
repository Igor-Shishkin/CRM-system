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
import java.util.List;
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
        private String phoneNumber;

        private boolean isClient;

        @OneToMany(mappedBy = "lid", cascade = CascadeType.REMOVE)
        @JsonManagedReference
        private Set<Order> orders = new HashSet<>();

        @OneToMany(mappedBy = "lid", cascade = CascadeType.REMOVE)
        private List<HistoryMessage> history;

        @ManyToOne
        @JoinColumn(name = "user_id")
        @JsonBackReference
        private User user;

        public Lid() {
        }
}
