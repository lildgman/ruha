package com.ruha.dto;

import lombok.Data;

@Data
public class MemberRequest {

    private String email;
    private String password;
    private String name;

    public MemberRequest(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }
}
