package com.crm.system.playload.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LidInfoResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private boolean isClient;
    private int numberOfOrders;

    protected LidInfoResponse(Long id, String fullName, String email, String phoneNumber,
                              String address, boolean isClient, int quantityOfOrders) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.isClient = isClient;
        this.numberOfOrders = quantityOfOrders;
    }
    public  static class Builder {
        protected Long id;
        protected String fullName;
        protected String email;
        protected String phoneNumber;
        protected String address;
        protected boolean isClient;
        protected int numberOfOrders;
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
        public Builder withIsClient(boolean isClient) {
            this.isClient = isClient;
            return this;
        }
        public Builder withQuantityOfOrders(int quantityOfOrders) {
            this.numberOfOrders = quantityOfOrders;
            return this;
        }
        public LidInfoResponse build() {
            return new LidInfoResponse(id,
                    fullName,
                    email,
                    phoneNumber,
                    address,
                    isClient,
                    numberOfOrders);
        }
    }
}
