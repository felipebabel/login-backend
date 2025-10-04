package com.securityspring.domain.enums;


import lombok.Getter;

public enum GenderEnum {

    M("MALE"),
    F( "FEMALE"),
    O("OTHER");

    @Getter
    private final String description;

    GenderEnum(final String description) {
        this.description = description;
    }

    public static GenderEnum fromString(final String gender) {
        if (gender == null) return null;

        for (GenderEnum genderEnum : GenderEnum.values()) {
            if (genderEnum.getDescription().equalsIgnoreCase(gender)) {
                return genderEnum;
            }
        }
        return null;
    }
}
