package com.crm.system.playload.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientInfoResponse extends LeadInfoResponse {
    private int numberOfPaidOrders;

    protected ClientInfoResponse(Long id, String fullName, String email, String phoneNumber,
                                 String address, boolean isClient, int numberOfOrders,
                                 int numberOfPaidOrders) {
        super(id, fullName, email, phoneNumber, address, isClient, numberOfOrders);
        this.numberOfPaidOrders = numberOfPaidOrders;
    }

    public static class ClientBuilder extends LeadInfoResponse.Builder<ClientBuilder> {
        private int numberOfPaidOrders;

        public ClientBuilder withNumberOfPaidOrders(int numberOfPaidOrders) {
            this.numberOfPaidOrders = numberOfPaidOrders;
            return this;
        }

        public ClientInfoResponse build() {
            return new ClientInfoResponse(
                    id, fullName, email, phoneNumber, address, isClient, numberOfOrders, numberOfPaidOrders
            );
        }

        @Override
        protected ClientBuilder self() {
            return this;
        }
    }
}

