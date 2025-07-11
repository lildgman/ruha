package com.ruha.dto.member;

import lombok.Data;

@Data
public class CreateMemberRequest {

    private String email;
    private String password;
    private String name;

    public CreateMemberRequest(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }
}
