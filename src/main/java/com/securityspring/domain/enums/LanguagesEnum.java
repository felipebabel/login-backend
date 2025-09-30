package com.securityspring.domain.enums;


import lombok.Getter;

public enum LanguagesEnum {

    EN("ENGLISH"),
    ES( "SPANISH"),
    DE("GERMAN"),
    PT("PORTUGUESE");

    @Getter
    private final String description;

    LanguagesEnum(final String description) {
        this.description = description;
    }

    public static LanguagesEnum fromString(final String role) {
        if (role == null) return EN;
        try {
            return LanguagesEnum.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            return EN;
        }
    }

}
