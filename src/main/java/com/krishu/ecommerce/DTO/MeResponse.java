package com.krishu.ecommerce.DTO;

import com.krishu.ecommerce.Role;

import java.util.List;

public class MeResponse {
    private String username;
    private String email;
    private List<Role> roles;

    public MeResponse(){}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}
