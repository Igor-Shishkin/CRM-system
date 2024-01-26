package com.crm.system.playload.response;

import com.crm.system.models.ClientStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientInfoDTO {
    private Long id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private ClientStatus status;
    private int numberOfOrders;
    private int numberOfPaidOrders;

    protected ClientInfoDTO(Long id, String fullName, String email, String phoneNumber,
                            String address, ClientStatus status, int quantityOfOrders, int numberOfPaidOrders) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.status = status;
        this.numberOfOrders = quantityOfOrders;
        this.numberOfPaidOrders = numberOfPaidOrders;
    }
    public  static class Builder {
        private Long id;
        private String fullName;
        private String email;
        private String phoneNumber;
        private String address;
        private ClientStatus status;
        private int numberOfOrders;
        private int numberOfPaidOrders;
        public Builder withId(long id) {
            this.id = id;
            return this;
        }
        public Builder withFullName(String fullName) {
            this.fullName = fullName;
            return this;
        }
        public Builder withEmail(String email) {
            this.email = email;
            return this;
        }
        public Builder withPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }
        public Builder withAddress(String address) {
            this.address = address;
            return this;
        }
        public Builder withIsClient(ClientStatus status) {
            this.status = status;
            return this;
        }
        public Builder withNumberOfOrders(int numberOfOrders) {
            this.numberOfOrders = numberOfOrders;
            return this;
        }
        public Builder withNumberOfPaidOrders(int numberOfPaidOrders) {
            this.numberOfPaidOrders = numberOfPaidOrders;
            return this;
        }

        public ClientInfoDTO build() {
            return new ClientInfoDTO(id,
                    fullName,
                    email,
                    phoneNumber,
                    address,
                    status,
                    numberOfOrders,
                    numberOfPaidOrders);
        }
    }
}
