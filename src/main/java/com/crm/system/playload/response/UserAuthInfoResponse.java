package com.crm.system.playload.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserAuthInfoResponse {
    private Long id;
    private String username;
    private String email;
    private List<String> roles;

    public UserAuthInfoResponse(Long id, String username, String email, List<String> roles) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }
}
