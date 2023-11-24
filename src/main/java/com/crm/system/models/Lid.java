package com.crm.system.models;

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
@Table(name = "clients",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "email")
        })
public class Lid {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "client_id")
        private Long id;
        @NotBlank
        @Size(max = 50)
        private String name;
        @Size(max = 50)
        private String surname;
        @Email
        @NotBlank
        @Size(max = 80)
        private String email;
        @Size(max = 50)
        private String phoneNumber;
        private boolean isClient;
        @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
        @JsonManagedReference
        private Set<Order> orders = new HashSet<>();
        @ManyToOne
        @JoinColumn(name = "user_id")
        @JsonBackReference
        private User user;

        public Lid(String name, String surname, String email, String phoneNumber, boolean isClient, User user) {
                this.name = name;
                this.surname = surname;
                this.email = email;
                this.phoneNumber = phoneNumber;
                this.isClient = isClient;
                this.user = user;
        }
        public Lid() {
        }
}
