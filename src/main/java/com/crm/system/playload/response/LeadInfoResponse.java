package com.crm.system.playload.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeadInfoResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private boolean isClient;
    private int numberOfOrders;

    protected LeadInfoResponse(Long id, String fullName, String email, String phoneNumber,
                               String address, boolean isClient, int quantityOfOrders) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.isClient = isClient;
        this.numberOfOrders = quantityOfOrders;
    }
    public  static class Builder<T extends Builder<T>> {
        protected Long id;
        protected String fullName;
        protected String email;
        protected String phoneNumber;
        protected String address;
        protected boolean isClient;
        protected int numberOfOrders;
        public T withId(long id) {
            this.id = id;
            return self();
        }
        public T withFullName(String fullName) {
            this.fullName = fullName;
            return self();
        }
        public T withEmail(String email) {
            this.email = email;
            return self();
        }
        public T withPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return self();
        }
        public T withAddress(String address) {
            this.address = address;
            return self();
        }
        public T withIsClient(boolean isClient) {
            this.isClient = isClient;
            return self();
        }
        public T withQuantityOfOrders(int quantityOfOrders) {
            this.numberOfOrders = quantityOfOrders;
            return self();
        }

        protected T self() {
            return (T) this;
        }
        public LeadInfoResponse build() {
            return new LeadInfoResponse(id,
                    fullName,
                    email,
                    phoneNumber,
                    address,
                    isClient,
                    numberOfOrders);
        }
    }
}
