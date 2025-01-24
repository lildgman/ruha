package com.ruha.entity;

public enum RoleType {

    ADMIN('A'),
    NORMAL('N');

    private final char role;

    RoleType(char role) {
        this.role = role;
    }

    public char getRole() {
        return role;
    }

}
