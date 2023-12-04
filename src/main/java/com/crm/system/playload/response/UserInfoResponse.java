package com.crm.system.playload.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
public class UserInfoResponse {
    private Long id;
    private String username;
    private String email;
    private List<String> roles;
    private int clientsNumber;

    private UserInfoResponse(Long id, String username, String email, List<String> roles, int clientsNumber) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
        this.clientsNumber = clientsNumber;
    }
    public static class Builder{
        private Long id;
        private String username;
        private String email;
        private List<String> roles;
        private int clientsNumber;
        public Builder withId(long id) {
            this.id = id;
            return this;
        }
        public Builder withUsername(String username) {
            this.username = username;
            return this;
        }
        public Builder withEmail(String email) {
            this.email = email;
            return this;
        }
        public Builder withRoles(List<String> roles) {
            this.roles = roles;
            return this;
        }
        public Builder withLidsNumber(int clientsNumber) {
            this.clientsNumber = clientsNumber;
            return this;
        }
        public UserInfoResponse build() {
            return new UserInfoResponse(id,
                    username,
                    email,
                    roles,
                    clientsNumber);
        }
    }

    @Override
    public String toString() {
        return username + " (id: " + id + ") ";
    }
}