package com.securityspring.domain.enums;


import lombok.Getter;

public enum RolesUserEnum {

    USER("USER"),
    ADMIN( "ADMIN"),
    ANALYST("ANALYST");

    @Getter
    private final String description;

    RolesUserEnum(final String description) {
        this.description = description;
    }

    public static RolesUserEnum fromString(final String role) {
        if (role == null) return USER;
        try {
            return RolesUserEnum.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            return USER;
        }
    }

}
