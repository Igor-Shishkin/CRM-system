package com.crm.system.models;

import com.crm.system.models.order.Order;
import com.crm.system.playload.request.AddClientDTO;
import com.crm.system.playload.request.EditClientDataDTO;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "clients",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "email")
        })
public class Client {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "client_id")
        private Long clientId;

        @NotBlank
        @Size(max = 255)
        @Column(name = "full_name")
        private String fullName;

        @Email
        @NotBlank
        @Size(max = 80)
        @Email
        private String email;

        @Size(max = 50)
        @Column(name = "phone_number")
        private String phoneNumber;

        @Column(name = "status")
        @Enumerated(EnumType.STRING)
        private ClientStatus status;

        private String address;

        @Column(name = "data_of_creation")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private final LocalDateTime dateOfCreation = LocalDateTime.now();

        @Column(name = "data_of_last_change")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime dateOfLastChange;

        @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
        @JsonManagedReference
        private Set<Order> orders = new HashSet<>();
        
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id")
        @JsonBackReference
        private User user;

        public Client() {
        }

        public Client(String fullName, String email, String phoneNumber, String address, User user) {
                this.fullName = fullName;
                this.email = email;
                this.phoneNumber = phoneNumber;
                this.address = address;
                this.user = user;
                this.dateOfLastChange = LocalDateTime.now();
                this.status = ClientStatus.LEAD;
        }
        public Client(AddClientDTO clientDTO, User user) {
                this.fullName = clientDTO.getFullName();
                this.email = clientDTO.getEmail();
                this.phoneNumber = clientDTO.getPhoneNumber();
                this.address = clientDTO.getAddress();
                this.user = user;
                this.dateOfLastChange = LocalDateTime.now();
                this.status = ClientStatus.LEAD;
        }

        public void editClientData(EditClientDataDTO request){
                this.setFullName(request.getFullName());
                this.setEmail(request.getEmail());
                this.setPhoneNumber(request.getPhoneNumber());
                this.setAddress(request.getAddress());
                this.setDateOfLastChange(LocalDateTime.now());
        }


        @Override
        public String toString() {
                return "Client{" +
                        "clientId=" + clientId +
                        ", fullName='" + fullName + '\'' +
                        ", email='" + email + '\'' +
                        ", phoneNumber='" + phoneNumber + '\'' +
                        ", status=" + status +
                        ", address='" + address + '\'' +
                        ", dateOfCreation=" + dateOfCreation +
                        ", dateOfLastChange=" + dateOfLastChange +
                        '}';
        }
}
