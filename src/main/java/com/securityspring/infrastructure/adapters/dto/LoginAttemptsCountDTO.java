package com.securityspring.infrastructure.adapters.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginAttemptsCountDTO {

    private String action;
    private Long total;

}
