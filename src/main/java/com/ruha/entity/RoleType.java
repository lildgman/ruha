package com.ruha.entity;

import lombok.Getter;

@Getter
public enum RoleType {

    ADMIN('A'),
    NORMAL('N');

    private final char role;

    RoleType(char role) {
        this.role = role;
    }

}
