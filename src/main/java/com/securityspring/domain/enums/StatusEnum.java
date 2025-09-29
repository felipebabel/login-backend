package com.securityspring.domain.enums;


import lombok.Getter;

public enum StatusEnum {

    ACTIVE(1, "ACTIVE"),
    PENDING(2, "PENDING"),
    BLOCKED(3, "BLOCKED"),
    INACTIVE(4, "INACTIVE");

    @Getter
    private final int code;
    private final String description;

    StatusEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

}
